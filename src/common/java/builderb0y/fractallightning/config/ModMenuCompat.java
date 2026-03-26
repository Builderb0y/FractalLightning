package builderb0y.fractallightning.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screens.Screen;

public class ModMenuCompat implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (Screen parent) -> {
			try {
				return ClothCode.getConfigScreen(parent);
			}
			catch (LinkageError ignored) {
				return null;
			}
		};
	}

	public static class ClothCode {

		public static Screen getConfigScreen(Screen parent) {
			return YaclCompat.YaclCode.HANDLER.generateGui().generateScreen(parent);
		}
	}
}