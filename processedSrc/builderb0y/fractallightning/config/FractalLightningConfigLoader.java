package builderb0y.fractallightning.config;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import builderb0y.fractallightning.FractalLightning;
import builderb0y.fractallightning.FractallightningClient;
import builderb0y.fractallightning.config.FractalLightningConfig.Loadable;
import builderb0y.fractallightning.config.FractalLightningConfig.Savable;

public class FractalLightningConfigLoader {

	public static final Logger
		LOGGER = LoggerFactory.getLogger(FractalLightning.MODNAME + "/Config");
	public static final String
		CONFIG_FILE_NAME = FractalLightning.MODNAME + ".properties";
	public static final Path
		CONFIG_FOLDER   = FabricLoader.getInstance().getConfigDir().toAbsolutePath(),
		CONFIG_FILE     = CONFIG_FOLDER.resolve(CONFIG_FILE_NAME),
		TMP_CONFIG_FILE = CONFIG_FOLDER.resolve(CONFIG_FILE_NAME + ".tmp");

	public static FractalLightningConfig load() throws Exception {
		if (Files.exists(CONFIG_FILE)) {
			return parse(Files.readString(CONFIG_FILE));
		}
		else {
			return new FractalLightningConfig();
		}
	}

	public static void save(FractalLightningConfig config) throws Exception {
		config.validatePostLoad();
		Files.writeString(TMP_CONFIG_FILE, unparse(config), StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		Files.move(TMP_CONFIG_FILE, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING);
	}

	public static FractalLightningConfig loadAndSave() {
		String oldText = null;
		FractalLightningConfig config;
		if (Files.exists(CONFIG_FILE)) try {
			oldText = Files.readString(CONFIG_FILE, StandardCharsets.UTF_8);
			config = parse(oldText);
		}
		catch (Exception exception) {
			LOGGER.warn("Could not parse " + CONFIG_FILE_NAME, exception);
			config = new FractalLightningConfig();
		}
		else {
			config = new FractalLightningConfig();
		}

		try {
			String newText = unparse(config);
			if (!newText.equals(oldText)) {
				Files.writeString(TMP_CONFIG_FILE, newText, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				Files.move(TMP_CONFIG_FILE, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (Exception exception) {
			LOGGER.warn("Could not save " + CONFIG_FILE_NAME, exception);
		}
		return config;
	}

	public static FractalLightningConfig parse(String text) {
		FractalLightningConfig config = new FractalLightningConfig();
		text
		.lines()
		.map(String::trim)
		.filter(Predicate.not(String::isEmpty))
		.filter((String line) -> line.charAt(0) != '#')
		.forEach((String line) -> {
			int equals = line.indexOf('=');
			if (equals >= 0) try {
				String key = line.substring(0, equals).trim();
				String value = line.substring(equals + 1).trim();
				Method method = FractalLightningConfig.class.getDeclaredMethod(key, String.class);
				if (method.isAnnotationPresent(Loadable.class)) {
					method.invoke(config, value);
				}
				else {
					LOGGER.warn("Ignoring malformed line " + line);
				}
			}
			catch (Exception exception) {
				LOGGER.warn("Ignoring malformed line " + line);
			}
			else {
				LOGGER.warn("Ignoring malformed line " + line);
			}
		});
		return config;
	}

	public static String unparse(FractalLightningConfig config) throws Exception {
		JsonObject lang;
		try (InputStream stream = FractallightningClient.class.getResourceAsStream("/assets/fractallightning/lang/en_us.json")) {
			if (stream == null) throw new FileNotFoundException("/assets/fractallightning/lang/en_us.json");
			lang = (JsonObject)(JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
		}
		StringBuilder builder = new StringBuilder(1024);
		for (Method method : FractalLightningConfig.class.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Savable.class)) {
				{
					JsonElement element = lang.get("text.autoconfig.fractallightning.option." + method.getName() + ".@Tooltip");
					if (element != null) {
						builder.append('#').append(element.getAsString()).append('\n');
					}
				}
				for (int tooltipIndex = 0; true; tooltipIndex++) {
					JsonElement element = lang.get("text.autoconfig.fractallightning.option." + method.getName() + ".@Tooltip[" + tooltipIndex + ']');
					if (element == null) break;
					builder.append('#').append(element.getAsString()).append('\n');
				}
				builder.append(method.getName()).append('=').append(method.invoke(config)).append("\n\n");
			}
		}
		builder.setLength(builder.length() - 2); //trim trailing newlines.
		return builder.toString();
	}
}