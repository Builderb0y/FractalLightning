package builderb0y.fractallightning.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;
import java.util.function.Supplier;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

import builderb0y.fractallightning.FractalLightning;

@Config(name = FractalLightning.MODID)
public class FractalLightningConfig {

	@Excluded
	public static final Supplier<FractalLightningConfig> INSTANCE = ClothConfigCompat.init();

	public static FractalLightningConfig instance() {
		return INSTANCE.get();
	}

	public static void init() {}

	//@Override
	public void validatePostLoad() {}

	@Tooltip
	@EnumHandler(option = EnumDisplayOption.BUTTON)
	public RainbowMode rainbow_mode = RainbowMode.PRIDE_MONTH_ONLY;
	@Savable public String rainbow_mode() { return this.rainbow_mode.name().toLowerCase(Locale.ROOT); }
	@Loadable public void rainbow_mode(String mode) { this.rainbow_mode = RainbowMode.valueOf(mode.toUpperCase(Locale.ROOT)); }

	public static enum RainbowMode {
		PRIDE_MONTH_ONLY(LocalDate.now().getMonth() == Month.JUNE),
		ALWAYS(true);

		public final boolean active;

		RainbowMode(boolean active) {
			this.active = active;
		}
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Savable {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Loadable {}
}