package builderb0y.fractallightning;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderers;
#if MC_VERSION < MC_26_2_0
import net.minecraft.world.entity.EntityType;
#else
import net.minecraft.world.entity.EntityTypes;
#endif

@Environment(EnvType.CLIENT)
public class FractallightningClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		#if MC_VERSION < MC_26_2_0
		EntityRenderers.register(EntityType.LIGHTNING_BOLT, FractalLightningEntityRenderer::new);
		#else
		EntityRenderers.register(EntityTypes.LIGHTNING_BOLT, FractalLightningEntityRenderer::new);
		#endif
	}
}