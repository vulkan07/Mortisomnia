package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Stack;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;


// TODO: STAGE 1 summon a scarecrow around the farm first which waits some time for the player to look at it, then disappears, breaking the crops
// TODO: STAGE 2 for a few seconds, all crop farmblocks should emit ectoplasm before breaking?
public class ScareCrowParactivity extends Paractivity {

    private static final int MIN_CROPS = 20;
    private static final int MAX_CROPS_TO_BREAK = 200;

    private static final int SEARCH_HORIZONTAL = 32;
    private static final int SEARCH_VERTICAL = 8;


    private Stack<BlockPos> crops;

    public ScareCrowParactivity(PlayerEntity player) {
        super(player);
        setMeta(40, ParaController.toControllerTime(10,0),25,120);
    }

    @Override
    protected ParaResult customInit() {
        Utils.BlockScanner blockScanner = new Utils.BlockScanner(new Box(player.getBlockPos()).expand(SEARCH_HORIZONTAL, SEARCH_VERTICAL, SEARCH_HORIZONTAL));
        BlockPos pos;
        Stack<BlockPos> tempStack = new Stack<>();
        while (blockScanner.hasNext()) {
            pos = blockScanner.getNextPos();
            if ( world.getBlockState(pos).isIn(BlockTags.CROPS) )
                tempStack.push(new BlockPos(pos));
        }

        if (tempStack.size() < MIN_CROPS)
            return ParaResult.fail("not enough crops around player");

            // randomize order (by copying into random locations in crops stack)
            // Keep 0..7 crops randomly, and cap breaks at MAX_CROPS_TO_BREAK
        int size = Math.min(tempStack.size() - RANDOM.nextInt(5), MAX_CROPS_TO_BREAK);
        crops = new Stack<>();

        BlockPos pos2;
        int index;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < tempStack.size(); j++) {
                index = RANDOM.nextInt(tempStack.size());
                pos2 = tempStack.get(index);
                if (pos2 != null) {
                    crops.push(new BlockPos(pos2));
                    tempStack.remove(index);
                    break;
                }
            }
        }

        return ParaResult.success();
    }

    boolean delay = false;

    @Override
    public ParaResult tick() {

        if(crops.isEmpty())
            return end();

        if (!delay) {
            BlockPos pos = crops.pop();
            world.setBlockState(pos, world.getBlockState(pos).withIfExists(Properties.AGE_7, 3)); // don't give wheat
            world.breakBlock(pos, RANDOM.nextInt(5) == 0);
        }
        delay = !delay;


        return ParaResult.success();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }

    @Override
    public String getName() {
        return Paractivity.SCARECROW;
    }
}
