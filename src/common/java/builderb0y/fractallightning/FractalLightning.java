package builderb0y.fractallightning;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.Identifier;

import builderb0y.fractallightning.config.FractalLightningConfig;

public class FractalLightning implements ModInitializer {

	public static final String
		MODID   = "fractallightning",
		MODNAME = "Fractal Lightning";
	public static final Logger
		LOGGER = LoggerFactory.getLogger(MODNAME);

	public static Identifier modID(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}

	@Override
	public void onInitialize() {
		FractalLightningConfig.init();
	}
}