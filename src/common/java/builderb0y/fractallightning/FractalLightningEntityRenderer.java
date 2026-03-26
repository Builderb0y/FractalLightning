package builderb0y.fractallightning;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LightningBoltRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.LightningBolt;

@Environment(EnvType.CLIENT)
public class FractalLightningEntityRenderer extends EntityRenderer<LightningBolt, LightningBoltRenderState> {

	public FractalLightningEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public LightningBoltRenderState createRenderState() {
		return new LightningBoltRenderState();
	}

	@Override
	public void extractRenderState(LightningBolt entity, LightningBoltRenderState state, float tickDelta) {
		super.extractRenderState(entity, state, tickDelta);
		state.seed = entity.seed;
	}

	@Override
	public void submit(
		LightningBoltRenderState state,
		PoseStack matrices,
		SubmitNodeCollector queue,
		CameraRenderState cameraState
	) {
		queue.submitCustomGeometry(
			matrices,
			LightningRenderer.LIGHTNING_LAYER,
			(PoseStack.Pose matrix, VertexConsumer buffer) -> {
				new LightningRendererImpl(matrix.pose(), buffer, state.ageInTicks).generatePoints(state.seed);
			}
		);
	}

	@Override
	public boolean affectedByCulling(LightningBolt entity) {
		return false;
	}
}