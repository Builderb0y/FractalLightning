package builderb0y.fractallightning.config;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;

public class ClothConfigCompat {

	public static Supplier<FractalLightningConfig> init() {
		try {
			return ClothCode.initCloth();
		}
		catch (LinkageError error) {
			FractalLightningConfigLoader.LOGGER.info("Failed to register ConfigSerializer. Cloth Config is probably not installed.");
			return Suppliers.ofInstance(FractalLightningConfigLoader.loadAndSave());
		}
	}

	public static class ClothCode {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static Supplier<FractalLightningConfig> initCloth() {
			AutoConfig.register(FractalLightningConfig.class.asSubclass(ConfigData.class), ClothCode::createSerializer);
			return (Supplier)(AutoConfig.getConfigHolder(FractalLightningConfig.class.asSubclass(ConfigData.class)));
		}

		public static ConfigSerializer<ConfigData> createSerializer(Config config, Class<?> clazz) {
			return new ConfigSerializer<>() {

				@Override
				public void serialize(ConfigData config) throws SerializationException {
					try {
						FractalLightningConfigLoader.save((FractalLightningConfig)(config));
					}
					catch (Exception exception) {
						throw new SerializationException(exception);
					}
				}

				@Override
				public ConfigData deserialize() throws SerializationException {
					try {
						return (ConfigData)(FractalLightningConfigLoader.load());
					}
					catch (Exception exception) {
						throw new SerializationException(exception);
					}
				}

				@Override
				public ConfigData createDefault() {
					return (ConfigData)(new FractalLightningConfig());
				}
			};
		}
	}
}