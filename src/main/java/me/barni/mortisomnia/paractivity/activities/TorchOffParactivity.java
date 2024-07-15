package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Stack;


public class TorchOffParactivity extends Paractivity {


    private final int SCAN_VERTICAL = 6;
    private final int SCAN_HORIZONTAL = 12;


    public TorchOffParactivity(PlayerEntity player) {
        super(player);
        setMeta(1, ParaController.toControllerTime(0,20), 10,85);
    }



    @Override
    protected ParaResult customInit() {
        if (!Utils.isCompleteNight(world))
            return ParaResult.fail("Not night enough");
        return ParaResult.success();
    }

    private boolean isValidBlock(BlockState blockState) {
        return  blockState.isOf(Blocks.TORCH) ||
                blockState.isOf(Blocks.WALL_TORCH) ||
                blockState.isOf(Blocks.LANTERN) ||
                blockState.isOf(Blocks.CAMPFIRE) ||
                blockState.isOf(Blocks.JACK_O_LANTERN);
    }

    @Override
    public ParaResult tick() {
        var blockScanner = new Utils.BlockScanner(new Box(player.getBlockPos()).expand(SCAN_HORIZONTAL, SCAN_VERTICAL, SCAN_HORIZONTAL));
        BlockPos pos;
        Stack<BlockPos> lights = new Stack<>();

        while (blockScanner.hasNext()) {
            pos = blockScanner.getNextPos();
            if (isValidBlock(world.getBlockState(pos)))
                lights.push(new BlockPos(pos));
        }
        if (lights.isEmpty()) {
            cancel();
            return ParaResult.fail("no lights close to player");
        }

        // Select random light
        pos = lights.get( Mortisomnia.RANDOM.nextInt(lights.size()) );
        Utils.unlightBlock(world, pos, true, false, true);

        return end();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }

    @Override
    public String getName() {
        return Paractivity.TORCH_OFF_ACTIVITY;
    }

}
