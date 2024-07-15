package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.entity.WeepingAngelEntity;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;
import static me.barni.mortisomnia.datagen.MortisomniaEntities.WEEPING_ANGEL;


public class WeepingAngelParactivity extends Paractivity {

    private static final int MAX_EXISTING_ANGELS = 4;
    private static final int MIN_TO_SPAWN = 1;
    private static final int MAX_TO_SPAWN = 3;
    private static final int SPAWN_OUTER_DISTANCE = 40; // Maximum distance from player (square)
    private static final int SPAWN_INNER_DISTANCE = 10; // Minimum distance from player (square)

    private static final int SPAWN_MAX_Y_OFFSET = 10;
    private static final int MAX_SPAWN_TRIES = 30;
    private static final int SEARCH_RANGE = 48;

    private int foundAngels;

    public WeepingAngelParactivity(PlayerEntity player) {
        super(player);
        setMeta(2, ParaController.toControllerTime(10,0), 20,120);
    }


    // Allow spawning on snow layer blocks
    public static boolean canSpawnOnBlock(World world, BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        return block.isSolidBlock(world,pos) || block.isOf(Blocks.SNOW) || block.isOf(Blocks.DIRT_PATH) || block.getBlock() instanceof SlabBlock;
    }

    private boolean isValidPos(BlockPos pos) {
        BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(player.getEyePos(), Vec3d.of(pos.up()), RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, player));
        if (hitResult.getType() == HitResult.Type.MISS)
            return false;

        return canSpawnOnBlock(world, pos.add(0, -1, 0)) &&
                world.getBlockState(pos.add(0, 0, 0)).isOf(Blocks.AIR) &&
                world.getBlockState(pos.add(0, 1, 0)).isOf(Blocks.AIR) &&
                world.getBlockState(pos.add(0, 2, 0)).isOf(Blocks.AIR) &&
                world.getLightLevel(LightType.BLOCK, pos) < 3;
    }

    @Nullable
    private BlockPos findRandomSpawnPos() {
        BlockPos origin = new BlockPos(player.getBlockPos());
        BlockPos pos;

        for (int i = 0; i < MAX_SPAWN_TRIES; i++) {
            pos = origin.add(
                    RANDOM.nextInt(SPAWN_INNER_DISTANCE, SPAWN_OUTER_DISTANCE+1) * (RANDOM.nextBoolean() ? 1 : -1),
                    -SPAWN_MAX_Y_OFFSET,
                    RANDOM.nextInt(SPAWN_INNER_DISTANCE, SPAWN_OUTER_DISTANCE+1) * (RANDOM.nextBoolean() ? 1 : -1)
            );

            for (int j = 0; j < SPAWN_MAX_Y_OFFSET *2; j++) {
                pos = pos.add(0,j,0);
                if (isValidPos(pos))
                    return pos;
            }
        }

        return null;
    }

    @Override
    protected ParaResult customInit() {
        if (!Utils.isNightTimeEnoughFor(world, 30))
            return ParaResult.fail("not night time");

        foundAngels = world.getOtherEntities(player, new Box(player.getBlockPos()).expand(SEARCH_RANGE), ent -> ent instanceof WeepingAngelEntity).size();
        if (foundAngels >= MAX_EXISTING_ANGELS)
            return ParaResult.fail("too many angels nearby (" + MAX_EXISTING_ANGELS + ")");

        return ParaResult.success();
    }

    @Override
    public ParaResult tick() {

        int spawnCount = RANDOM.nextInt(MIN_TO_SPAWN, MAX_TO_SPAWN);
        if (spawnCount+foundAngels > MAX_EXISTING_ANGELS)
            spawnCount = MAX_EXISTING_ANGELS - foundAngels;
        int actual = 0;

        for (int i = 0; i < spawnCount; i++) {

            BlockPos pos = findRandomSpawnPos();
            if (pos == null) continue;

            WeepingAngelEntity e = WEEPING_ANGEL.create(world);
            if (e == null) continue;

            e.setPosition(Vec3d.of(pos).add(.5,0,.5));
            e.setAngelYaw(RANDOM.nextInt(8)*45);
            world.spawnEntity(e);

            actual++;
        }
        if (actual == 0) {
            cancel();
            return ParaResult.fail("Could not spawn any angels");
        }

        return ParaResult.end();
    }

    @Override
    public String getName() {
        return Paractivity.WEEPING_ANGEL_ACTIVITY;
    }
}
