package me.barni.mortisomnia.paractivity;

import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface ParactivityFactory {
    Paractivity create(PlayerEntity player);
}
