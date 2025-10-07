package builderb0y.fractallightning.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {

	public LightningEntityMixin() {
		super(null, null);
	}

	@Definition(id = "ambientTick", field = "Lnet/minecraft/entity/LightningEntity;ambientTick:I")
	@Expression("this.ambientTick == 2")
	@WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean fractalLightning_modifyCondition1(int left, int right, Operation<Boolean> original) {
		return this.age == 8;
	}

	@Definition(id = "ambientTick", field = "Lnet/minecraft/entity/LightningEntity;ambientTick:I")
	@Expression("this.ambientTick < 0")
	@WrapOperation(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private boolean fractalLightning_modifyCondition2(int left, int right, Operation<Boolean> original) {
		return this.age >= 16;
	}

	@Definition(id = "remainingActions", field = "Lnet/minecraft/entity/LightningEntity;remainingActions:I")
	@Expression("this.remainingActions == 0")
	@WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean fractalLightning_modifyCondition3(int left, int right, Operation<Boolean> original) {
		return true;
	}

	@Definition(id = "ambientTick", field = "Lnet/minecraft/entity/LightningEntity;ambientTick:I")
	@Expression("this.ambientTick >= 0")
	@WrapOperation(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private boolean fractalLightning_modifyCondition4(int left, int right, Operation<Boolean> original) {
		return this.age == 8;
	}

	@Definition(
		id = "getWorld",
		#if MC_VERSION >= MC_1_21_9
			method = "Lnet/minecraft/entity/LightningEntity;getEntityWorld()Lnet/minecraft/world/World;"
		#else
			method = "Lnet/minecraft/entity/LightningEntity;getWorld()Lnet/minecraft/world/World;"
		#endif
	)
	@Definition(id = "setLightningTicksLeft", method = "Lnet/minecraft/world/World;setLightningTicksLeft(I)V")
	@Expression("this.getWorld().setLightningTicksLeft(@(2))")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int fractalLightning_modifyConstant(int original) {
		return 4;
	}
}