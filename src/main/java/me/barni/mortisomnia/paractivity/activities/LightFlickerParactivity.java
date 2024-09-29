package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;


public class LightFlickerParactivity extends Paractivity {


    private final int FLICKERS_PER_TICK = 8;
    private final int FLICKER_DURATION = 15*20; // 10 seconds
    private final int MIN_LIGHT_BLOCKS = 40; // if number of found blocks is less than this, cancel event
    private final int MAX_SCAN_PER_TICK = 32000;
    private final int SCAN_VERTICAL = 24; // goes in both direcctions, so x will scan 2x+1 blocks
    private final int SCAN_HORIZONTAL = 64; // goes in both direcctions, so x will scan 2x+1 blocks

    private final Utils.TickTimer flickerTimer = new Utils.TickTimer(FLICKER_DURATION);
    private final ArrayList<BlockPos> lightBlockStack = new ArrayList<>();
    private BlockPos origin;
    private Utils.BlockScanner blockScanner;
    private int stage = 0;


    public LightFlickerParactivity(PlayerEntity player) {
        super(player);
        setMeta(5, ParaController.toControllerTime(10,0), 50,160);
    }

    private void scanBlocks() {
        for (int i = 0; i < MAX_SCAN_PER_TICK; i++) {
            var pos = blockScanner.getNextPos();
            if (pos == null) { // scan ended
                if (lightBlockStack.size() < MIN_LIGHT_BLOCKS) {
                    cancel();
                }
                stage++;
                return;
            }

            if (Utils.isBlockLightSource(world.getBlockState(pos)))
                lightBlockStack.add(new BlockPos(pos));
        }
    }

    private void resetBlocks() {
        for (var pos : lightBlockStack) {
            if (Utils.isBlockUnlitLightSource(world.getBlockState(pos)))
                Utils.relightBlock(world, pos, false);
        }
        stage++;
    }

    private void handleBlocks() {
        if (flickerTimer.tick()) stage++; // Delay for suspense

        for (int i = 0; i < FLICKERS_PER_TICK; i++) {

            var pos =  lightBlockStack.get(RANDOM.nextInt(lightBlockStack.size()-1));
            if (Utils.isBlockLightSource(world.getBlockState(pos)))
                Utils.unlightBlock(world, pos, false, false, false);
            else if (Utils.isBlockUnlitLightSource(world.getBlockState(pos))) {
                Utils.relightBlock(world, pos, false);
                if (RANDOM.nextInt(10)==0) {
                    world.playSound(null, pos, MortisomniaSounds.BULB, SoundCategory.AMBIENT, .25f, (float) (RANDOM.nextFloat(.1f) + 1f));
                    ((ServerWorld)world).spawnParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY(), player.getZ(), 4, 7, 5, 7, .1);
                }
            }
        }
    }


    @Override
    protected ParaResult customInit() {
        if (!Utils.isNightTimeEnoughFor(world,30))
            return ParaResult.fail("Not night enough");

        origin = player.getBlockPos();
        blockScanner = new Utils.BlockScanner(new Box(origin).expand(SCAN_HORIZONTAL, SCAN_VERTICAL, SCAN_HORIZONTAL));

        return ParaResult.success();
    }

    @Override
    public ParaResult tick() {

        switch (stage) {
            case 0 -> scanBlocks();
            case 1 -> handleBlocks();
            case 2 -> resetBlocks();
            default -> end();
        }

        return ParaResult.success();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }

    @Override
    public String getName() {
        return Paractivity.LIGHT_FLICKER;
    }

}
