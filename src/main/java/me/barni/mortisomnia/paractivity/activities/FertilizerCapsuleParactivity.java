package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Stack;


public class FertilizerCapsuleParactivity extends Paractivity {

    private final int SCAN_VERTICAL = 12; // goes in both direcctions, so x will scan 2x+1 blocks
    private final int SCAN_HORIZONTAL = 24; // goes in both direcctions, so x will scan 2x+1 blocks
    private final int MAX_CROPS_TO_ADD = 32;

    private final Stack<BlockPos> farmBlocks = new Stack<>();
    private BlockPos origin; // Set to player's position at beggining
    private int placedCrops = 0;

    public FertilizerCapsuleParactivity(PlayerEntity player) {
        super(player);
        setMeta(0, 0, 0, 0);
    }

    private void scanBlocks() {
        Utils.BlockScanner blockScanner = new Utils.BlockScanner(new Box(origin).expand(SCAN_HORIZONTAL, SCAN_VERTICAL, SCAN_HORIZONTAL));
        BlockPos pos;
        while (blockScanner.hasNext()) {
            pos = blockScanner.getNextPos();

            if (world.getBlockState(pos).isOf(Blocks.FARMLAND) && world.getBlockState(pos.up()).isOf(Blocks.AIR))
                farmBlocks.push(new BlockPos(pos));
        }
    }

    private void placeCrop(BlockPos pos) {
        if (!world.getBlockState(pos).isOf(Blocks.FARMLAND))
            return;
        if (!world.getBlockState(pos.up()).isOf(Blocks.AIR))
            return;
        BlockState crop = Blocks.WHEAT.getDefaultState();
        switch (Mortisomnia.RANDOM.nextInt(10)) {
            case 0 -> crop = Blocks.CARROTS.getDefaultState();
            case 1 -> crop = Blocks.POTATOES.getDefaultState();
            case 2 -> crop = Blocks.BEETROOTS.getDefaultState();
        }
        crop = crop.withIfExists(Properties.AGE_7, Mortisomnia.RANDOM.nextInt(4));
        world.setBlockState(pos.up(), crop, 3);
        ((ServerWorld) world).spawnParticles(
                ParticleTypes.HAPPY_VILLAGER, pos.getX()+.5, pos.getY()+1.05, pos.getZ()+.5, 4, .25, .05, .25, 0);
        if (farmBlocks.size() % 2 == 0)
            world.playSound(null, pos.up(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.PLAYERS, 1, 1);
        placedCrops++;
    }

    private boolean handleBlocks() {
        for (int i = 0; i < 2; i++) {
            if (farmBlocks.isEmpty() || placedCrops > MAX_CROPS_TO_ADD) return false;
            var pos = farmBlocks.pop();
            placeCrop(pos);
        }
        return true;
    }

    private double distToOrigin(BlockPos pos) {
        double d = (double)origin.getX() + 0.5 - pos.getX();
        double e = (double)origin.getY() + 0.5 - pos.getY();
        double f = (double)origin.getZ() + 0.5 - pos.getZ();
        return d * d + e * e + f * f;
    }
    private void sortBlocksByDistance() {
        farmBlocks.sort((o1, o2) -> {
            var a =distToOrigin(o1);
            var b =distToOrigin(o2);
            if (a==b) return 0;
            return a>b ? -1 : 1;
        });
    }


    @Override
    protected ParaResult customInit() {
        origin = player.getBlockPos();

        scanBlocks();
        if (farmBlocks.isEmpty())
            return ParaResult.fail("No empty farms nearby!");
        sortBlocksByDistance();

        return ParaResult.success();
    }

    @Override
    public ParaResult tick() {
        return handleBlocks() ? ParaResult.success() : ParaResult.end();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return !(other instanceof  ScareCrowParactivity) && !(other instanceof FertilizerCapsuleParactivity);
    }

    @Override
    public String getName() {
        return Paractivity.FERTILIZER_CAPSULE_PARACTIVITY;
    }

}
