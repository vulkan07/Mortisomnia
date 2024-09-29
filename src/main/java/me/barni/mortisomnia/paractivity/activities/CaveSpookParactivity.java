package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;


public class CaveSpookParactivity extends Paractivity {

    private static final int DELAY = (25*20)-13; //24.5s

    private final int SCAN_VERTICAL = 32;
    private final int SCAN_HORIZONTAL = 48;

    private Utils.TickTimer timer = new Utils.TickTimer(DELAY, false);
    private Utils.BlockScanner blockScanner;
    private final ArrayList<BlockPos> lightBlocks = new ArrayList<>();


    public CaveSpookParactivity(PlayerEntity player) {
        super(player);
        setMeta(1, ParaController.toControllerTime(10,0), 40,220);
    }

    @Override
    protected ParaResult customInit() {
        if (Utils.isPlayerInCave(player) < 110) {
            return ParaResult.fail("Player not in cave");
        }

        return ParaResult.success();
    }


    @Override
    public ParaResult tick() {
        // First tick -> play sound
        if (timer.time == DELAY) {
            player.playSoundToPlayer(MortisomniaSounds.CAVE_MONSTER, SoundCategory.MASTER, 1, 1);
            player.sendMessage(Text.literal("Press Shift to run").formatted(Formatting.DARK_RED), true); // ? fun but unsure to keep

        }

        // Last tick (hissing sound) -> break lights
        if (timer.tick()) {

            // scan for lights around player
            blockScanner = new Utils.BlockScanner(new Box(player.getBlockPos()).expand(SCAN_HORIZONTAL, SCAN_VERTICAL, SCAN_HORIZONTAL));
            BlockPos pos;

            while (blockScanner.hasNext()) {
                pos = blockScanner.getNextPos();
                if (Utils.isBlockLightSource(world.getBlockState(pos)))
                    Utils.unlightBlock(world,pos,false,true, true);
            }

            end();
        }



        return ParaResult.success();
    }

    @Override
    public String getName() {
        return Paractivity.CAVE_SPOOK;
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }
}
