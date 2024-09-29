package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class CreeperParactivity extends Paractivity {

    private static final float CREEPER_DIST = 0.9f;

    private CreeperEntity creeper = null;
    private int stage = 0;
    private float startAngle = 0;
    private float oppAngle = 0;


    public CreeperParactivity(PlayerEntity player) {
        super(player);
        setMeta(2, ParaController.toControllerTime(3,0), 30,100);
    }


    private boolean isValidPos(BlockPos pos) {
        return  world.getBlockState(pos).isOf(Blocks.AIR) && world.getBlockState(pos.add(0, 1, 0)).isOf(Blocks.AIR);
    }


    @Override
    protected ParaResult customInit() {
        startAngle = player.headYaw;
        oppAngle = startAngle+180%360;
        return ParaResult.success();
    }

    void hissStage() {
        Vec3d v = new Vec3d(
                player.getX() + Math.cos(Math.toRadians(startAngle-90))*CREEPER_DIST,
                player.getY(),
                player.getZ() + Math.sin((Math.toRadians(startAngle-90)))*CREEPER_DIST);
        world.playSound(null, v.getX(), v.getY(), v.getZ(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.MASTER, 2.0f, .5f);
        ((ServerWorld)world).spawnParticles(MortisomniaParticles.ECTOPLASM, v.getX(), v.getY()+.3, v.getZ(), 3, .2f,.2f,.2f, .1f);

        stage++;
    }
    void lookStage() {
        float a = player.getHeadYaw()+180;
        float angDif = Math.abs(oppAngle-a);
        if (angDif>90)
            stage++;
    }
    void spawnStage() {
        float a = player.getHeadYaw()+180;
        float angDif = Math.abs(oppAngle-a);
        if (angDif>110)
            return;


        creeper = new CreeperEntity(EntityType.CREEPER, world);
        Vec3d v = new Vec3d(
                player.getX() + Math.cos(Math.toRadians(oppAngle-90))*CREEPER_DIST,
                player.getY(),
                player.getZ() + Math.sin((Math.toRadians(oppAngle-90)))*CREEPER_DIST);

        creeper.setPosition(v);
        world.spawnEntity(creeper);


        stage++;
    }

    void endStage() {
        stage++;
    }

    @Override
    public ParaResult tick() {
        switch (stage) {
            case 0 -> hissStage();
            case 1 -> lookStage();
            case 2 -> spawnStage();
            case 3 -> endStage();
            default -> end();
        }

        return ParaResult.success();
    }

    @Override
    public String getName() {
        return Paractivity.CREEPER;
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }
}
