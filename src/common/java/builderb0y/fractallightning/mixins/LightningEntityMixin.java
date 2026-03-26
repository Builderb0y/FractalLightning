package builderb0y.fractallightning.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningBolt.class)
public abstract class LightningEntityMixin extends Entity {

	public LightningEntityMixin() {
		super(null, null);
	}

	@Definition(id = "ambientTick", field = "Lnet/minecraft/world/entity/LightningBolt;life:I")
	@Expression("this.ambientTick == 2")
	@WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean fractalLightning_modifyCondition1(int left, int right, Operation<Boolean> original) {
		return this.tickCount == 8;
	}

	@Definition(id = "ambientTick", field = "Lnet/minecraft/world/entity/LightningBolt;life:I")
	@Expression("this.ambientTick < 0")
	@WrapOperation(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private boolean fractalLightning_modifyCondition2(int left, int right, Operation<Boolean> original) {
		return this.tickCount >= 16;
	}

	@Definition(id = "remainingActions", field = "Lnet/minecraft/world/entity/LightningBolt;flashes:I")
	@Expression("this.remainingActions == 0")
	@WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean fractalLightning_modifyCondition3(int left, int right, Operation<Boolean> original) {
		return true;
	}

	@Definition(id = "ambientTick", field = "Lnet/minecraft/world/entity/LightningBolt;life:I")
	@Expression("this.ambientTick >= 0")
	@WrapOperation(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private boolean fractalLightning_modifyCondition4(int left, int right, Operation<Boolean> original) {
		return this.tickCount == 8;
	}

	@Definition(
		id = "getWorld",
		method = "Lnet/minecraft/world/entity/LightningBolt;level()Lnet/minecraft/world/level/Level;"
	)
	@Definition(id = "setLightningTicksLeft", method = "Lnet/minecraft/world/level/Level;setSkyFlashTime(I)V")
	@Expression("this.getWorld().setLightningTicksLeft(@(2))")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int fractalLightning_modifyConstant(int original) {
		return 4;
	}
}