package builderb0y.fractallightning;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public abstract class LightningRenderer {

	public static RenderType LIGHTNING_LAYER;

	static {
		RenderType layer = RenderTypes.lightning();
		got:
		if (FabricLoader.getInstance().isModLoaded("iris")) {
			try {
				layer = (RenderType)(Class.forName("net.irisshaders.iris.pathways.LightningHandler").getDeclaredField("IRIS_LIGHTNING").get(null));
				FractalLightning.LOGGER.info("Using new iris lightning render layer.");
				break got;
			}
			catch (Exception ignored) {}
			try {
				layer = (RenderType)(Class.forName("net.coderbot.iris.pipeline.LightningHandler").getDeclaredField("IRIS_LIGHTNING").get(null));
				FractalLightning.LOGGER.info("Using old iris lightning render layer.");
				break got;
			}
			catch (Exception ignored) {}
			FractalLightning.LOGGER.warn("Could not locate Iris lightning render layer. Defaulting to vanilla.");
		}
		LIGHTNING_LAYER = layer;
	}

	public final Matrix4f modelViewMatrix;
	public final Vector4f scratch;
	public final VertexConsumer buffer;

	public LightningRenderer(Matrix4f modelViewMatrix, VertexConsumer buffer) {
		this.modelViewMatrix = modelViewMatrix;
		this.scratch = new Vector4f();
		this.buffer = buffer;
	}

	public void generatePoints(
		long seed,
		float startX,
		float startY,
		float startZ,
		float endX,
		float endY,
		float endZ,
		float width
	) {
		this.modelViewMatrix.transform(this.scratch.set(startX, startY, startZ, 1.0F));
		float transformedStartX = this.scratch.x;
		float transformedStartY = this.scratch.y;
		float transformedStartZ = this.scratch.z;
		this.modelViewMatrix.transform(this.scratch.set(endX, endY, endZ, 1.0F));
		float transformedEndX = this.scratch.x;
		float transformedEndY = this.scratch.y;
		float transformedEndZ = this.scratch.z;
		this.generatePointsRecursive(
			seed,
			startX,
			startY,
			startZ,
			0.0F,
			endX,
			endY,
			endZ,
			1.0F,
			transformedStartX,
			transformedStartY,
			transformedStartZ,
			transformedEndX,
			transformedEndY,
			transformedEndZ,
			width
		);
	}

	public abstract float adjustWidth(float width, float startFrac, float endFrac);

	//the methods in this class have a lot of parameters
	//because I want to avoid allocating any objects in them.
	public void generatePointsRecursive(
		long seed,
		float startX,
		float startY,
		float startZ,
		float startFrac,
		float endX,
		float endY,
		float endZ,
		float endFrac,
		float transformedStartX,
		float transformedStartY,
		float transformedStartZ,
		float transformedEndX,
		float transformedEndY,
		float transformedEndZ,
		float width
	) {
		float adjustedWidth = this.adjustWidth(width, startFrac, endFrac);
		if (!(adjustedWidth >= 0.0F)) return;

		float cameraDistanceSquared = Math.min(
			transformedStartX * transformedStartX + transformedStartY * transformedStartY + transformedStartZ * transformedStartZ,
			transformedEndX * transformedEndX + transformedEndY * transformedEndY + transformedEndZ * transformedEndZ
		);
		float dx = endX - startX;
		float dy = endY - startY;
		float dz = endZ - startZ;
		float segmentLengthSquared = dx * dx + dy * dy + dz * dz;
		if (segmentLengthSquared > 1.0F / 4096.0F && segmentLengthSquared > cameraDistanceSquared * (1.0F / 4096.0F)) {
			long offsetSeed = RNG.stafford(seed ^ 0x84133286A32BEF3FL);
			//half way between start and end.
			float midX    = (startX    + endX   ) * 0.5F;
			float midY    = (startY    + endY   ) * 0.5F;
			float midZ    = (startZ    + endZ   ) * 0.5F;
			float midFrac = (startFrac + endFrac) * 0.5F;
			//normally I'd generate a point on or in a unit sphere,
			//so that the direction is not biased in any direction,
			//but I think a cube is fine in this case.
			//the bias is not noticeable at all to me.
			float offsetX = RNG.nextUniformFloat(offsetSeed += RNG.PHI64);
			float offsetY = RNG.nextUniformFloat(offsetSeed += RNG.PHI64);
			float offsetZ = RNG.nextUniformFloat(offsetSeed += RNG.PHI64);
			//now project the cube onto the plane defined by the point
			//(start + end) / 2, and the normal vector (end - start).
			{
				//traditional projection from point to plane
				//would first divide dxyz by normalMagnitude,
				//but this would require a square root operation.
				//what I'm doing instead is dividing the dot product
				//by segmentLengthSquared, and not touching dxyz.
				//the multiplication by dxyz and division by segmentLengthSquared
				//cancel out to be effectively equivalent to dividing dxyz
				//by normalMagnitude, and it saves a square root operation.
				float dot = (offsetX * dx + offsetY * dy + offsetZ * dz) / segmentLengthSquared;
				offsetX -= dx * dot;
				offsetY -= dy * dot;
				offsetZ -= dz * dot;

				//scale offset to be 0.1875x the length of (end - start).
				float currentLengthSquared = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
				//sqrt(a) / sqrt(b) = sqrt(a / b).
				float scalar = 0.1875F * ((float)(Math.sqrt(segmentLengthSquared / currentLengthSquared)));
				offsetX *= scalar;
				offsetY *= scalar;
				offsetZ *= scalar;
			}
			//offset midpoint.
			midX += offsetX;
			midY += offsetY;
			midZ += offsetZ;

			//do all that over again to generate the branch.
			float branchX = midX;
			float branchY = midY;
			float branchZ = midZ;

			offsetX = RNG.nextUniformFloat(offsetSeed += RNG.PHI64);
			offsetY = RNG.nextUniformFloat(offsetSeed += RNG.PHI64);
			offsetZ = RNG.nextUniformFloat(offsetSeed += RNG.PHI64);
			{
				float dot = (offsetX * dx + offsetY * dy + offsetZ * dz) / segmentLengthSquared;
				offsetX -= dx * dot;
				offsetY -= dy * dot;
				offsetZ -= dz * dot;

				float currentLengthSquared = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
				float scalar = 0.375F * ((float)(Math.sqrt(segmentLengthSquared / currentLengthSquared)));
				offsetX *= scalar;
				offsetY *= scalar;
				offsetZ *= scalar;
			}
			branchX += offsetX;
			branchY += offsetY;
			branchZ += offsetZ;

			//transform mid and branch positions.
			this.modelViewMatrix.transform(this.scratch.set(midX, midY, midZ, 1.0F));
			float transformedMidX = this.scratch.x;
			float transformedMidY = this.scratch.y;
			float transformedMidZ = this.scratch.z;

			this.modelViewMatrix.transform(this.scratch.set(branchX, branchY, branchZ, 1.0F));
			float transformedBranchX = this.scratch.x;
			float transformedBranchY = this.scratch.y;
			float transformedBranchZ = this.scratch.z;

			long splitSeed = RNG.stafford(seed ^ 0xB9287001C0AFFAD8L);

			//now generate child branches.
			this.generatePointsRecursive(
				RNG.permute(splitSeed, 1),
				startX,
				startY,
				startZ,
				startFrac,
				midX,
				midY,
				midZ,
				midFrac,
				transformedStartX,
				transformedStartY,
				transformedStartZ,
				transformedMidX,
				transformedMidY,
				transformedMidZ,
				width
			);
			this.generatePointsRecursive(
				RNG.permute(splitSeed, 2),
				midX,
				midY,
				midZ,
				midFrac,
				endX,
				endY,
				endZ,
				endFrac,
				transformedMidX,
				transformedMidY,
				transformedMidZ,
				transformedEndX,
				transformedEndY,
				transformedEndZ,
				width
			);
			this.generatePointsRecursive(
				RNG.permute(splitSeed, 3),
				midX,
				midY,
				midZ,
				midFrac,
				branchX,
				branchY,
				branchZ,
				endFrac,
				transformedMidX,
				transformedMidY,
				transformedMidZ,
				transformedBranchX,
				transformedBranchY,
				transformedBranchZ,
				width * 0.5F
			);
		}
		else { //subdivision == 0
			this.addQuads(
				transformedStartX,
				transformedStartY,
				transformedStartZ,
				startFrac,
				transformedEndX,
				transformedEndY,
				transformedEndZ,
				endFrac,
				cameraDistanceSquared,
				adjustedWidth
			);
		}
	}

	public abstract void addQuads(
		float transformedStartX,
		float transformedStartY,
		float transformedStartZ,
		float startFrac,
		float transformedEndX,
		float transformedEndY,
		float transformedEndZ,
		float endFrac,
		float cameraDistanceSquared,
		float width
	);

	public void addColoredQuads(
		float transformedStartX,
		float transformedStartY,
		float transformedStartZ,
		float transformedEndX,
		float transformedEndY,
		float transformedEndZ,
		float cameraDistanceSquared,
		float width,
		int innerArgb,
		int outerArgb
	) {
		//align lightning quads so they face the player.
		//I used to do this by taking the cross product of
		//(end - start) and (camera - start)
		//to get a vector which is on an axis parallel to the screen.
		//then we just offset the vertices along this axis.
		//but then I did some more optimization work and extracted the matrix operation
		//from the vertex code to instead apply to the start and end positions.
		//the net result is that start and end are now relative to the camera,
		//and the camera is at (0, 0, 0).
		//the cross product is still used,
		//it's just that it can be simplified slightly now.
		float dx = transformedEndX - transformedStartX;
		float dy = transformedEndY - transformedStartY;
		float dz = transformedEndZ - transformedStartZ;
		float crossX = dz * transformedStartY - dy * transformedStartZ;
		float crossY = dx * transformedStartZ - dz * transformedStartX;
		float crossZ = dy * transformedStartX - dx * transformedStartY;
		float crossLengthSquared = crossX * crossX + crossY * crossY + crossZ * crossZ;
		float scalar = width * ((float)(Math.sqrt(cameraDistanceSquared / crossLengthSquared)));
		crossX *= scalar;
		crossY *= scalar;
		crossZ *= scalar;

		this.vertex(transformedStartX + crossX, transformedStartY + crossY, transformedStartZ + crossZ, outerArgb);
		this.vertex(  transformedEndX + crossX,   transformedEndY + crossY,   transformedEndZ + crossZ, outerArgb);
		this.vertex(  transformedEndX         ,   transformedEndY         ,   transformedEndZ         , innerArgb);
		this.vertex(transformedStartX         , transformedStartY         , transformedStartZ         , innerArgb);

		this.vertex(transformedStartX         , transformedStartY         , transformedStartZ         , innerArgb);
		this.vertex(  transformedEndX         ,   transformedEndY         ,   transformedEndZ         , innerArgb);
		this.vertex(  transformedEndX - crossX,   transformedEndY - crossY,   transformedEndZ - crossZ, outerArgb);
		this.vertex(transformedStartX - crossX, transformedStartY - crossY, transformedStartZ - crossZ, outerArgb);
	}

	public void vertex(float x, float y, float z, int argb) {
		this.buffer.addVertex(x, y, z).setColor(argb);
	}
}