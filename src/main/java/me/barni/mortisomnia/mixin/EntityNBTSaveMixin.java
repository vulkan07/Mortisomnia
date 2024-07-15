package me.barni.mortisomnia.mixin;

import me.barni.mortisomnia.IEntityNBTSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

@Mixin(Entity.class)
public class EntityNBTSaveMixin implements IEntityNBTSaver {
    @Unique
    private static final String KEY = MOD_ID + ".data";

    @Unique
    private NbtCompound persistentData;

    @Override
    public NbtCompound mortisomnia$getPersistentData() {
        if (persistentData == null)
            persistentData = new NbtCompound();
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable info) {
        if(persistentData != null) {
            nbt.put(KEY, persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(KEY, 10)) {
            persistentData = nbt.getCompound(KEY);
        }
    }
}
