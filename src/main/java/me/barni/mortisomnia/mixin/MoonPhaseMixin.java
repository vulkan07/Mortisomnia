package me.barni.mortisomnia.mixin;


import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Shifts the moon cycle in the world so that
// the first night is a waxing crescent instead of full moon
// making full moon the 3rd night

@Mixin(DimensionType.class)
public abstract class MoonPhaseMixin {

    @Inject( method = "getMoonPhase(J)I", at = @At("HEAD"), cancellable = true)
    private void getMoonPhase(long time, CallbackInfoReturnable<Integer> ci) {
        int value = (int)(time / 24000L % 8L + 4L) % 8;
        ci.setReturnValue(value);
    }

}
