package builderb0y.fractallightning;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.entity.EntityType;

@Environment(EnvType.CLIENT)
public class FractallightningClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererFactories.register(EntityType.LIGHTNING_BOLT, FractalLightningEntityRenderer::new);
	}
}