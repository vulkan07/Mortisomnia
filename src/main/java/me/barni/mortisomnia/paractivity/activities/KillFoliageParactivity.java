package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;


public class KillFoliageParactivity extends Paractivity {

    private static final int SCAN_VERTICAL = 32; // goes in both direcctions, so x will scan 2x+1 blocks
    private static final int SCAN_HORIZONTAL = 96; // goes in both direcctions, so x will scan 2x+1 blocks
    private static final int START_FALLOFF = 70; // where the spherical falloff start
    public  static final int MIN_BLOCKS = 200; // we need this many foliage blocks at least to start
    public  static final int MAX_SPAWNERS = 4;

    private record BlockLoc(BlockPos pos, int dist) {}

    private int totalBlocks = 0;
    private int placedSpawners = 0;
    private final ArrayList<BlockLoc> blocks = new ArrayList<>();
    private BlockPos origin; // Set to player's position at beggining

    public KillFoliageParactivity(PlayerEntity player) {
        super(player);
        setMeta(5, 5, 50, 200);
    }

    private void scanBlocks() {
        Utils.BlockScanner blockScanner = new Utils.BlockScanner(new Box(origin).expand(SCAN_HORIZONTAL, SCAN_VERTICAL, SCAN_HORIZONTAL));
        BlockPos pos;
        while (blockScanner.hasNext()) {
            pos = blockScanner.getNextPos();

            if (Utils.isBlockKillableFoliage(world.getBlockState(pos))) {
                int dist = (int)distToOrigin(pos);

               // if (dist < START_FALLOFF || Mortisomnia.RANDOM.nextInt(Math.max(1,dist/1200))<1)
                 if (dist < SCAN_HORIZONTAL && (dist < START_FALLOFF || Mortisomnia.RANDOM.nextInt((dist-START_FALLOFF)/2+1)<1))
                    blocks.add(new BlockLoc(new BlockPos(pos.getX(), pos.getY(), pos.getZ()),dist));
            }
        }
        totalBlocks = blocks.size();
    }

    private boolean handleBlocks() {
        for (int i = 0; i < 15; i++) {
            if (totalBlocks <= 1) return false; // DO NOT SET TO 0 OR IT WILL HANG THE SERVER
            int r;
            BlockLoc pos;

            do {
                r = Mortisomnia.RANDOM.nextInt(blocks.size() - 1);
                pos = blocks.get(r);
            } while (pos == null); // BE FUCKING CAREFUL THIS EASILY CAN HANG THE SERVER THREAD

            totalBlocks--;
            blocks.set(r, null);

            // if haven't placed MAX_SPAWNERS yet, air is above, and 1:401 chance -> place a spawner
            if (placedSpawners<MAX_SPAWNERS && Mortisomnia.RANDOM.nextInt(400)==0 && world.getBlockState(pos.pos.up()).isOf(Blocks.AIR)) {
                world.setBlockState(pos.pos.up(), Blocks.SPAWNER.getDefaultState());
                ((MobSpawnerBlockEntity)world.getBlockEntity(pos.pos.up())).setEntityType(EntityType.SPIDER, world.getRandom());
                placedSpawners++;
                continue;
            }

            if (Mortisomnia.RANDOM.nextInt(30)!=0)
                Utils.killFoliageBlock(world, pos.pos, false);
        }
        return true;
    }

    private double distToOrigin(BlockPos pos) {
        double d = (double)origin.getX() + 0.5 - pos.getX();
        double e = (double)origin.getY() + 0.5 - pos.getY();
        double f = (double)origin.getZ() + 0.5 - pos.getZ();
        return Math.sqrt(d * d + e * e + f * f);
    }

    @Override
    protected ParaResult customInit() {
        origin = player.getBlockPos();

        scanBlocks();
        if (blocks.size() < MIN_BLOCKS)
            return ParaResult.fail("Not enough foliage nearby! (<" + MIN_BLOCKS + ")");

        return ParaResult.success();
    }

    @Override
    public ParaResult tick() {
        return handleBlocks() ? ParaResult.success() : ParaResult.end();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return !(other instanceof FertilizerCapsuleParactivity) && !(other instanceof KillFoliageParactivity);
    }

    @Override
    public String getName() {
        return Paractivity.KILL_FOLIAGE;
    }

}
