package builderb0y.fractallightning.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.FieldAccess;

import builderb0y.fractallightning.FractalLightning;

public class YaclCompat {

	public static Supplier<FractalLightningConfig> init() {
		try {
			return YaclCode.initYacl();
		}
		catch (LinkageError error) {
			FractalLightningConfigLoader.LOGGER.info("Failed to register ConfigSerializer. Cloth Config is probably not installed.");
			return Suppliers.ofInstance(FractalLightningConfigLoader.loadAndSave());
		}
	}

	public static class YaclCode {

		public static final ConfigClassHandler<FractalLightningConfig> HANDLER = (
			ConfigClassHandler
			.createBuilder(FractalLightningConfig.class)
			.id(FractalLightning.modID("config"))
			.serializer((ConfigClassHandler<FractalLightningConfig> handler) -> new ConfigSerializer<>(handler) {

				public static final Map<String, Field> FIELD_MAP = (
					Arrays
					.stream(FractalLightningConfig.class.getFields())
					.filter((Field field) -> (field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0)
					.collect(Collectors.toMap(Field::getName, Function.identity()))
				);

				@Override
				public void save() {
					try {
						FractalLightningConfigLoader.save(this.config.instance());
					}
					catch (Exception exception) {
						FractalLightning.LOGGER.warn("Failed to save config file:", exception);
					}
				}

				@Override
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public LoadResult loadSafely(Map<ConfigField<?>, FieldAccess<?>> bufferAccessMap) {
					try {
						FractalLightningConfig loaded = FractalLightningConfigLoader.load();
						for (Map.Entry<ConfigField<?>, ? extends FieldAccess> entry : bufferAccessMap.entrySet()) {
							entry.getValue().set(FIELD_MAP.get(entry.getKey().serial().orElseThrow().serialName()).get(loaded));
						}
						return LoadResult.SUCCESS;
					}
					catch (Exception exception) {
						FractalLightning.LOGGER.warn("Failed to load config file:", exception);
						return LoadResult.FAILURE;
					}
				}
			})
			.build()
		);

		public static Supplier<FractalLightningConfig> initYacl() {
			return HANDLER::instance;
		}
	}
}