package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Stack;


public class LightExtinguishParactivity extends Paractivity {


    private final int DELAY_AFTER_SCAN = 80; //
    private final int MIN_LIGHT_BLOCKS = 70; // if number of found blocks is less than this, cancel event
    private final int MAX_SCAN_PER_TICK = 32000;
    private final int SCAN_VERTICAL = 24; // goes in both direcctions, so x will scan 2x+1 blocks
    private final int SCAN_HORIZONTAL = 128; // goes in both direcctions, so x will scan 2x+1 blocks

    private final Utils.TickTimer delayTimer = new Utils.TickTimer(DELAY_AFTER_SCAN, false);
    private final Utils.TickTimer unlightTimer = new Utils.TickTimer(1); // Used to slow the unlighting of last 10 blocks
    private final Stack<BlockPos> lightBlockStack = new Stack<>();
    private BlockPos origin;
    private Utils.BlockScanner blockScanner;
    private int stage = 0;

    public LightExtinguishParactivity(PlayerEntity player) {
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
                lightBlockStack.push(new BlockPos(pos));
        }
    }

    private void handleBlocks() {
        if (delayTimer.time == DELAY_AFTER_SCAN) {
            world.playSound(null,  player.getBlockPos(), MortisomniaSounds.TORCHBREAK_DRONE, SoundCategory.AMBIENT, .3f, 1f );
        }
        if (delayTimer.time == DELAY_AFTER_SCAN-20) {// Shake at beggining
            //ServerPlayNetworking.send((ServerPlayerEntity) player, Mortisomnia.CAMSHAKE_PACKET,
            //        PacketByteBufs.create().writeFloat(.4f).writeFloat(.03f).writeFloat(.3f));
            //TODO MIGRATE
        }

        if (!delayTimer.tick()) return; // Delay for suspense

        for (int i = 0; i < Math.max(1, lightBlockStack.size()/20); i++) {

            if (lightBlockStack.size() < 10)
                if (!unlightTimer.tick()) return;
            unlightTimer.setTime((int)((10-lightBlockStack.size())*1.5), true);

            if (lightBlockStack.isEmpty()) {
                stage++;
                return;
            }
            var pos = lightBlockStack.pop();
            Utils.unlightBlock(world, pos, true, true, true);
        }
    }

    private double distToOrigin(BlockPos pos) {
        double d = (double)origin.getX() + 0.5 - pos.getX();
        double e = (double)origin.getY() + 0.5 - pos.getY();
        double f = (double)origin.getZ() + 0.5 - pos.getZ();
        return d * d + e * e + f * f;
    }
    private void sortBlocksByDistance() {
        lightBlockStack.sort((o1, o2) -> {
            var a =distToOrigin(o1);
            var b =distToOrigin(o2);
            if (a==b) return 0;
            return a>b ? 1 : -1;
        });
        stage++;
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
            case 1 -> sortBlocksByDistance();
            case 2 -> handleBlocks();
            default -> end();
        }

        return ParaResult.success();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return !(other instanceof CapturedLightParactivity);
    }

    @Override
    public String getName() {
        return Paractivity.LIGHT_EXTINGUISH;
    }

}
