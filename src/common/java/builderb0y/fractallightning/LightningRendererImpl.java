package builderb0y.fractallightning;

import org.joml.Matrix4f;
import builderb0y.fractallightning.config.FractalLightningConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ARGB;

public class LightningRendererImpl extends LightningRenderer {

	public static final float
		baseWidth = 0.015625F,
		eighthWidth = baseWidth * 0.125F;

	public final float age;
	public final boolean rainbow;

	public LightningRendererImpl(Matrix4f modelViewMatrix, VertexConsumer buffer, float age) {
		super(modelViewMatrix, buffer);
		this.age = age;
		this.rainbow = FractalLightningConfig.instance().rainbow_mode.active;
	}

	@Override
	public float adjustWidth(float width, float startFrac, float endFrac) {
		if (this.age >= 8.0F) {
			return width - (this.age * eighthWidth - baseWidth);
		}
		else if (startFrac >= this.age * 0.125F) {
			return 0.0F;
		}
		else {
			return width;
		}
	}

	public static double square(double number) {
		return number * number;
	}

	public static double cosCurve(double number) {
		number *= number;
		return (1.0D - 16.0D * number) * (1.0D - (16.0D - 4.0D * Math.PI) * number);
	}

	//wavelength of 1, not tau.
	public static double fastSin01(double angle) {
		angle -= Math.floor(angle);
		return angle <= 0.5D ? cosCurve(angle - 0.25D) : -cosCurve(angle - 0.75D);
	}

	//wavelength of 1, not tau.
	public static double fastCos01(double angle) {
		return fastSin01(angle + 0.25D);
	}

	@Override
	public void addQuads(
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
	) {
		int innerArgb, outerArgb;
		if (this.rainbow) {
			double red   = square(fastCos01(startFrac              ) * 0.5D + 0.5D);
			double green = square(fastCos01(startFrac - 1.0D / 3.0D) * 0.5D + 0.5D);
			double blue  = square(fastCos01(startFrac - 2.0D / 3.0D) * 0.5D + 0.5D);

			double rcpMagnitude = 1.0D / Math.sqrt(red * red + green * green + blue * blue);
			red   = Math.sqrt(red   * rcpMagnitude);
			green = Math.sqrt(green * rcpMagnitude);
			blue  = Math.sqrt(blue  * rcpMagnitude);

			innerArgb = ARGB.color(127, Math.min((int)(red *  64.0D + 192.0D), 255), Math.min((int)(green *  64.0D + 192.0D), 255), Math.min((int)(blue *  64.0D + 192.0D), 255));
			outerArgb = ARGB.color(  0, Math.min((int)(red * 256.0D         ), 255), Math.min((int)(green * 256.0D         ), 255), Math.min((int)(blue * 256.0D         ), 255));
		}
		else {
			innerArgb = 0x7FFFFFFF;
			outerArgb = 0x00003F7F;
		}
		this.addColoredQuads(
			transformedStartX,
			transformedStartY,
			transformedStartZ,
			transformedEndX,
			transformedEndY,
			transformedEndZ,
			cameraDistanceSquared,
			width,
			innerArgb,
			outerArgb
		);
	}

	public void generatePoints(long seed) {
		this.generatePoints(
			seed,
			RNG.nextUniformFloat (seed += RNG.PHI64) * 32.0F,
			RNG.nextPositiveFloat(seed += RNG.PHI64) * 64.0F + 128.0F,
			RNG.nextUniformFloat (seed += RNG.PHI64) * 32.0F,
			0.0F,
			0.0F,
			0.0F,
			baseWidth
		);
	}
}