package me.barni.mortisomnia.paractivity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IParactivity {

     // Control
     ParaResult init(boolean ignoreHauntRequired);
     ParaResult tick();
     void cancel();

     // Logic
     boolean isFinished();
     boolean permitsParactivity(Paractivity other);

     // Data
     String getName();
     PlayerEntity getPlayer();
     World getWorld();
}
