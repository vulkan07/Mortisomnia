package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Stack;


public class CapturedLightParactivity extends Paractivity {

    private final int MAX_SCAN_PER_TICK = 32000;
    private final int SCAN_VERTICAL = 12; // goes in both direcctions, so x will scan 2x+1 blocks
    private final int SCAN_HORIZONTAL = 48; // goes in both direcctions, so x will scan 2x+1 blocks

    private final Stack<BlockPos> lightBlockStack = new Stack<>();
    private BlockPos origin; // Set to player's position at beggining
    private Utils.BlockScanner blockScanner;

    public CapturedLightParactivity(PlayerEntity player) {
        super(player);
        setMeta(0, 0, 0, 0);
    }

    private boolean scanBlocks() {
        for (int i = 0; i < MAX_SCAN_PER_TICK; i++) {
            var pos = blockScanner.getNextPos();
            if (pos == null) {
                sortBlocksByDistance();
                return false;
            }

            if (Utils.isBlockUnlitLightSource(world.getBlockState(pos)))
                lightBlockStack.push(new BlockPos(pos));
        }
       return true;
    }

    private boolean handleBlocks() {
        for (int i = 0; i < 10; i++) {
            if (lightBlockStack.isEmpty()) return false;
            var pos = lightBlockStack.pop();
            Utils.relightBlock(world, pos, i%3==0);
        }
        return true;
    }

    private double distToOrigin(BlockPos pos) {
        double d = (double)origin.getX() + 0.5 - pos.getX();
        double e = (double)origin.getY() + 0.5 - pos.getY();
        double f = (double)origin.getZ() + 0.5 - pos.getZ();
        return d * d + e * e + f * f;
    }
    private boolean sorted = false;
    private void sortBlocksByDistance() {
        if (this.sorted) return;
        this.sorted = true;
        lightBlockStack.sort((o1, o2) -> {
            var a =distToOrigin(o1);
            var b =distToOrigin(o2);
            if (a==b) return 0;
            return a>b ? -1 : 1;
        });
    }


    @Override
    protected ParaResult customInit() {
        origin = player.getBlockPos();
        blockScanner = new Utils.BlockScanner(new Box(origin).expand(SCAN_HORIZONTAL, SCAN_VERTICAL, SCAN_HORIZONTAL));
        return ParaResult.success();
    }

    private final Utils.TickTimer postTimer = new Utils.TickTimer(20);
    @Override
    public ParaResult tick() {
        if (scanBlocks()) return ParaResult.success();
        if (handleBlocks()) return ParaResult.success();
        if (!postTimer.tick()) return  ParaResult.success();

        return end();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return other instanceof FertilizerCapsuleParactivity;
    }

    @Override
    public String getName() {
        return Paractivity.CAPTURED_LIGHT;
    }

}
