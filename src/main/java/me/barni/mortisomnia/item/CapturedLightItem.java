package me.barni.mortisomnia.item;

import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.activities.CapturedLightParactivity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;

public class CapturedLightItem extends Item {
    public CapturedLightItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var user = context.getPlayer();
        if (context.getWorld() instanceof ServerWorld serverWorld) {
            CapturedLightParactivity p = new CapturedLightParactivity(user);

            if (!ParaController.getInstance().addHandledParactivity(p, false, true)) {
                return ActionResult.FAIL;
            }
            // PARTICLE
            serverWorld.spawnParticles(
                    MortisomniaParticles.ECTOPLASM, user.getPos().x, user.getPos().y+1, user.getPos().z,
                    48, 0, .35, 0, 1.5);
            serverWorld.spawnParticles(
                    ParticleTypes.LAVA, user.getPos().x, user.getPos().y + 1.7, user.getPos().z,
                    8, 0, 0, 0, 0);
            serverWorld.spawnParticles(
                    ParticleTypes.FLAME, user.getPos().x, user.getPos().y + 1.7, user.getPos().z,
                    16, 0, 0, 0, .1);

            //SOUND
            serverWorld.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z,
                    MortisomniaSounds.SOUL_SFX, SoundCategory.NEUTRAL, .05f, 1.2f);
            serverWorld.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z,
                    MortisomniaSounds.USE_CAPTURED_LIGHT, SoundCategory.NEUTRAL, 1f, 1f);
/*
            //SHAKE
            ServerPlayNetworking.send((ServerPlayerEntity) user, Mortisomnia.CAMSHAKE_PACKET,
                    PacketByteBufs.create().writeFloat(.35f).writeFloat(.17f).writeFloat(.3f));

*/
            user.getStackInHand(context.getHand()).decrementUnlessCreative(1, user);
        }
        return ActionResult.SUCCESS;
    }
}
