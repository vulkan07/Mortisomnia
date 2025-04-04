package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaEntities;
import me.barni.mortisomnia.entity.WeepingAngelEntity;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;


public class WeepingAngelParactivity extends Paractivity {

    public static final String id = "weeping_angel";
    public static Paractivity create( PlayerEntity player) { return new WeepingAngelParactivity(player); }
    public String getName() { return id; }

    private static final int MAX_EXISTING_ANGELS = 6;
    private static final int MIN_TO_SPAWN = 2;
    private static final int MAX_TO_SPAWN = 4;
    private static final int SPAWN_OUTER_DISTANCE = 20; // Maximum distance from player (square)
    private static final int SPAWN_INNER_DISTANCE = 5; // Minimum distance from player (square)
    private static final int SPAWN_MAX_Y_OFFSET = 7;
    private static final int MAX_SPAWN_TRIES = 50;
    private static final int SEARCH_RANGE = 48;

    private static final int PHASE_SPAWN = 0;
    private static final int PHASE_ATTACK = 1;

    private int phase = 0;
    private List<Entity> angels;

    public WeepingAngelParactivity(PlayerEntity player) {
        super(player);
        setMeta(2, ParaController.toControllerTime(10,0), 50,200);
    }


    // Allow spawning on snow layer blocks TODO merge with isValidPos
    public static boolean canSpawnOnBlock(World world, BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        return block.isSolidBlock(world,pos) || block.isOf(Blocks.SNOW) || block.isOf(Blocks.DIRT_PATH) || block.getBlock() instanceof SlabBlock;
    }

    private boolean isValidPos(WeepingAngelEntity angel, BlockPos bpos) {
        for (var e : world.getOtherEntities(angel, new Box(bpos).expand(1))) {
            if (e instanceof WeepingAngelEntity) return false;
        }
        boolean valid;
        valid = !world.getBlockState(bpos.down()).isAir();
//        valid &= canSpawnOnBlock(world,bpos); // not working burh
        valid &= !world.getBlockState(bpos.down()).isOf(Blocks.WATER);
        valid &= !world.getBlockState(bpos.down()).isOf(Blocks.LAVA);
        valid &= world.getBlockState(bpos).isAir();
        valid &= world.getBlockState(bpos.up(1)).isAir();
        valid &= world.getBlockState(bpos.up(2)).isAir();

        if (valid) {
            angel.setPosition(bpos.getX()+.5, bpos.getY(),bpos.getZ()+.5);
            if (Utils.canPlayerSeeEntity(player,angel))
                return false;
        }

        return valid;
    }

    @Nullable
    private boolean findRandomSpawnPos(WeepingAngelEntity angel) {
        BlockPos origin = new BlockPos(player.getBlockPos());
        BlockPos pos;

        for (int i = 0; i < MAX_SPAWN_TRIES; i++) {
            pos = origin.add(
                    RANDOM.nextInt(SPAWN_INNER_DISTANCE, SPAWN_OUTER_DISTANCE+1) * (RANDOM.nextBoolean() ? 1 : -1),
                    -SPAWN_MAX_Y_OFFSET,
                    RANDOM.nextInt(SPAWN_INNER_DISTANCE, SPAWN_OUTER_DISTANCE+1) * (RANDOM.nextBoolean() ? 1 : -1)
            );

            for (int j = 0; j < SPAWN_MAX_Y_OFFSET *2; j++) {
                if (isValidPos(angel, pos.up(j)))
                    return true;
            }
        }

        return false;
    }

    @Override
    protected ParaResult customInit() {
        if (!Utils.isNightTimeEnoughFor(world, 180))
            return ParaResult.fail("not night time or not enough night left");

        // Check how many angels are already present nearby
        angels = world.getOtherEntities(player, new Box(player.getBlockPos()).expand(SEARCH_RANGE), ent -> ent instanceof WeepingAngelEntity);
        if (angels.size() >= MAX_EXISTING_ANGELS)
            return ParaResult.fail("too many angels nearby (" + angels.size() + ")");

        return ParaResult.success();
    }

    private ParaResult spawnAngels() {
        // How many to spawn accounting for others that we have found
        int spawnCount = RANDOM.nextInt(MIN_TO_SPAWN, MAX_TO_SPAWN);
        if (spawnCount+ angels.size() > MAX_EXISTING_ANGELS)
            spawnCount = MAX_EXISTING_ANGELS - angels.size();

        angels.clear(); // From here this stores the angles of this activity, not the others found
        int spawned = 0;

        for (int i = 0; i < spawnCount; i++) {


            WeepingAngelEntity e = new WeepingAngelEntity(MortisomniaEntities.WEEPING_ANGEL,world);
            if (!findRandomSpawnPos(e)) {
                e.discard();
            } else {
                float yaw = RANDOM.nextInt(8)*45;
                e.setYaw(yaw);
                e.setHeadYaw(yaw);
                world.spawnEntity(e);
                angels.add(e);
                spawned++;
            }
        }
        if (spawned == 0) {
            cancel();
            return ParaResult.fail("Could not spawn any angels");
        }
        this.phase++;
        return ParaResult.success();
    }

    @Override
    public ParaResult tick() {
        if (this.phase == PHASE_SPAWN)
            return spawnAngels();

        // Remove dead angels
        var it = angels.listIterator();
        WeepingAngelEntity angel;
        while (it.hasNext()) {
            angel = (WeepingAngelEntity) it.next();
            if (angel.isDead() || angel.isRemoved()) it.remove();
        }

        if (angels.isEmpty())
            return ParaResult.end("No angels left");


        return ParaResult.success();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return !(other instanceof WeepingAngelParactivity); // Only one can happen at once
    }

    @Override
    public void cancel() {
        for (var angel : angels) angel.kill((ServerWorld) world); //TODO unholy
        super.cancel();
    }
}
