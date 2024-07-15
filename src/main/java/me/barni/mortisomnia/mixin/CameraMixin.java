package me.barni.mortisomnia.mixin;


import me.barni.mortisomnia.ClientCamShakeManager;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    private Vec3d pos;

    @Inject( method = "update", at = @At("TAIL") )
    private void cameraUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        ClientCamShakeManager.getInstance().update();
        this.pos = this.pos.add(ClientCamShakeManager.getInstance().getShake());
    }

}
