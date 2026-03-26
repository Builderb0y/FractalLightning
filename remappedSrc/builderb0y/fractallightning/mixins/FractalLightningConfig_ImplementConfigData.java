package builderb0y.fractallightning.mixins;

import me.shedaniel.autoconfig.ConfigData;
import org.spongepowered.asm.mixin.Mixin;

import builderb0y.fractallightning.config.FractalLightningConfig;

@Mixin(FractalLightningConfig.class)
public class FractalLightningConfig_ImplementConfigData implements ConfigData {

}