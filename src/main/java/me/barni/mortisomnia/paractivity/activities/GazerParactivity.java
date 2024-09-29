package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaEntities;
import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.entity.GazerEntity;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;


public class GazerParactivity extends Paractivity {

    private static final int GAZER_COUNT = 20;
    private static final int SPAWN_OUTER_DISTANCE = 20; // Maximum distance from player (square)
    private static final int SPAWN_INNER_DISTANCE = 9; // Minimum distance from player (square)

    private static final int SPAWN_MIN_Y_OFFSET = 0;
    private static final int SPAWN_MAX_Y_OFFSET = 8;
    private static final int MAX_SPAWN_TRIES = 30;

    private static final int PHASE_1_TIME = 3*20;
    private static final int PHASE_2_TIME = 10*20;

    private int stage = 0;
    private Utils.TickTimer stageTimer = new Utils.TickTimer(0);
    private BlockPos[] positions = new BlockPos[GAZER_COUNT];
    private GazerEntity[] gazers = new GazerEntity[GAZER_COUNT];


    public GazerParactivity(PlayerEntity player) {
        super(player);
        setMeta(2, ParaController.toControllerTime(10,0), 50,130);
    }


    private boolean isValidPos(BlockPos pos) {
        return  world.getBlockState(pos.add(0, 0, 0)).isOf(Blocks.AIR) &&
                world.getBlockState(pos.add(0, 1, 0)).isOf(Blocks.AIR) &&
                world.getBlockState(pos.add(0, 2, 0)).isOf(Blocks.AIR);
    }

    @Nullable
    private BlockPos findRandomSpawnPos() {
        BlockPos origin = new BlockPos(player.getBlockPos());
        BlockPos pos;

        for (int i = 0; i < MAX_SPAWN_TRIES; i++) {
            /*
            pos = origin.add(
                    RANDOM.nextInt(SPAWN_INNER_DISTANCE, SPAWN_OUTER_DISTANCE+1) * (RANDOM.nextBoolean() ? 1 : -1),
                    +SPAWN_MAX_Y_OFFSET,
                    RANDOM.nextInt(SPAWN_INNER_DISTANCE, SPAWN_OUTER_DISTANCE+1) * (RANDOM.nextBoolean() ? 1 : -1)
            );
            */
            Vec3d r = Utils.randomPointOnCircle(RANDOM.nextInt(SPAWN_INNER_DISTANCE, SPAWN_OUTER_DISTANCE));
            pos = origin.add((int)r.x, RANDOM.nextInt(SPAWN_MIN_Y_OFFSET,SPAWN_MAX_Y_OFFSET), (int)r.z);

            if (isValidPos(pos))
                return pos;
        }

        return null;
    }

    @Override
    protected ParaResult customInit() {
        if (!Utils.isNightTimeEnoughFor(world, (PHASE_1_TIME+PHASE_2_TIME)/20))
            return ParaResult.fail("not night time");
        if (world.getLightLevel(LightType.BLOCK, player.getBlockPos()) > 3)
            return ParaResult.fail("not dark enough around player");

        return ParaResult.success();
    }

    void startStage() {
        // darkness effect
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, PHASE_1_TIME+10, 1, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, PHASE_1_TIME+PHASE_2_TIME+10, 4, true, false));
        stageTimer.setTime(PHASE_1_TIME, true); // wait 5s
        stage++;
    }
    void spawnStage() {
        if (!stageTimer.tick()) return;


        int gazerCount = 0;
        for (int i = 0; i < GAZER_COUNT; i++) {

            BlockPos pos = findRandomSpawnPos();
            if (pos == null) continue;

            GazerEntity e = new GazerEntity(MortisomniaEntities.GAZER, world);

            gazers[gazerCount] = e;
            gazerCount++;

            e.setPosition(Vec3d.of(pos).add(.5,0,.5));
            world.spawnEntity(e);

        }

        stageTimer.setTime(PHASE_2_TIME, true);
        stage++;
    }
    void endStage() {
        ServerWorld w = (ServerWorld) world;
//        for (int i = 0; i < 5; i++) {
            w.spawnParticles(MortisomniaParticles.ECTOPLASM, player.getX()+RANDOM.nextFloat(-7,7), player.getY()+RANDOM.nextFloat(1)-2,player.getZ()+RANDOM.nextFloat(-7,7) , 2, .25, 1, .25, 0.2);
//        }

        if (!stageTimer.tick()) return;

        for (var gazer : gazers) {
            if (gazer != null)
                gazer.setDisappear();
        }

        player.playSoundToPlayer(MortisomniaSounds.GHOST, SoundCategory.MASTER, 1,1);

        stage++;
    }

    @Override
    public ParaResult tick() {
        /*
        return ParaResult.end();

         */
        switch (stage) {
            case 0 -> startStage();
            case 1 -> spawnStage();
            case 2 -> endStage();
            default -> end();
        }

        return ParaResult.success();
    }

    @Override
    public String getName() {
        return Paractivity.GAZER;
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }
}
