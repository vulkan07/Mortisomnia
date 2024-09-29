package me.barni.mortisomnia.mixin;


import me.barni.mortisomnia.Mortisomnia;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

// Shifts the moon cycle in the world so that
// the first night is a waxing crescent instead of full moon
// making full moon the 3rd night

@Mixin(SheepEntity.class)
public abstract class SheepShearDropMixin {

    @Shadow @Final private static Map<DyeColor, ItemConvertible> DROPS;

    // Purpose: Drop mostly 1 (rarely 2) wool instead of 2-3 on shearing a sheep
    @Inject( method = "sheared(Lnet/minecraft/sound/SoundCategory;)V", at = @At("HEAD"), cancellable = true)
    private void sheared(SoundCategory shearedSoundCategory, CallbackInfo ci) {
        SheepEntity me = (SheepEntity)(Object)this;
        me.getWorld().playSoundFromEntity(null, me, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
        me.setSheared(true);
//        int i = Mortisomnia.RANDOM.nextInt(2) + 1;
//        for(int j = 0; j < i; ++j)

        me.dropItem(DROPS.get(me.getColor()), 1);
        if (Mortisomnia.RANDOM.nextInt(5)==0)
            me.dropItem(DROPS.get(me.getColor()), 1);

        ci.cancel();

    }

}
