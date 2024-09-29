package me.barni.mortisomnia.item;

import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.activities.CapturedLightParactivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CapturedLightItem extends Item {
    public CapturedLightItem(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            CapturedLightParactivity p = new CapturedLightParactivity(user);

            if (!ParaController.getInstance().addHandledParactivity(p, false, true)) {
                return TypedActionResult.fail(user.getStackInHand(hand));
            }

            if (world.isClient()) {
                return TypedActionResult.pass(user.getStackInHand(hand));
            }
            ServerWorld serverWorld = (ServerWorld) world;
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
            world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z,
                    MortisomniaSounds.SOUL_SFX, SoundCategory.NEUTRAL, .05f, 1.2f);
            world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z,
                    MortisomniaSounds.USE_CAPTURED_LIGHT, SoundCategory.NEUTRAL, 1f, 1f);
/*
            //SHAKE
            ServerPlayNetworking.send((ServerPlayerEntity) user, Mortisomnia.CAMSHAKE_PACKET,
                    PacketByteBufs.create().writeFloat(.35f).writeFloat(.17f).writeFloat(.3f));

*/
            user.getStackInHand(hand).decrementUnlessCreative(1, user);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
