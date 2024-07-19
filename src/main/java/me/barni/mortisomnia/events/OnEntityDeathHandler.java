package me.barni.mortisomnia.events;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaItems;
import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;

public class OnEntityDeathHandler {
    public static void onEntityAfterDeath(Entity entity, DamageSource damageSource) {
        if (entity.getWorld() instanceof ServerWorld world) {
            if (damageSource.getAttacker() instanceof PlayerEntity attacker) {
                ParaController.getInstance().incrementPlayerHaunt(attacker, 2); // +4..8 haunt

                world.spawnParticles(MortisomniaParticles.ECTOPLASM, entity.getX(), entity.getY() + 1, entity.getZ(), (int) (entity.getHeight() * 5), 0, 0, 0, .2);

                if (Utils.isCompleteNight(world)) {

                    // 1:2 chance of fragment drop
                    if (RANDOM.nextInt(2) == 0) {

                        int i = RANDOM.nextInt(0, 10);
                        ParaController.getInstance().incrementPlayerHaunt(attacker, (i%4)+4); // +4..8 haunt
                        world.spawnParticles(MortisomniaParticles.ECTOPLASM, entity.getX(), entity.getY() + (int) (entity.getHeight() * .7) + .2, entity.getZ(), 10, .1, .1, .1, .5);
                        entity.playSound(MortisomniaSounds.SOUL_SFX, .27f, 1f);

                        // 1:7 chance of purified fragment
                        if (i < 1)
                            world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(MortisomniaItems.PURIFIED_ECTO_FRAGMENT, 1)));
                        else
                            world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(MortisomniaItems.ECTO_FRAGMENT, (i%3)+1)));
                    }
                }
            }
        }

    }
}