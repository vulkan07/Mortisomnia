package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaItems;
import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;


public class DoorToggleParactivity extends Paractivity {

    private final int MAX_TOGGLES = 60;
    private final int TICK_DEALY = 20;
    private final int SCAN_VERTICAL = 6;
    private final int SCAN_HORIZONTAL = 12;

    private Utils.TickTimer timer = new Utils.TickTimer(TICK_DEALY, true);
    private int toggles = 0;
    private BlockPos door;
    private boolean doorState; // true == open

    public DoorToggleParactivity(PlayerEntity player) {
        super(player);
        setMeta(1,6, 30,140);
    }


    @Override
    protected ParaResult customInit() {
        if (!Utils.isCompleteNight(world) && Mortisomnia.RANDOM.nextInt(5)==0) // if not night, 1:5 chance of happpening
            return ParaResult.fail("not night");


        // get all doors in scan area, then select one randomly
        var blockScanner = new Utils.BlockScanner(new Box(player.getBlockPos()).expand(SCAN_HORIZONTAL, SCAN_VERTICAL, SCAN_HORIZONTAL));
        BlockPos pos;
        var doors = new ArrayList<BlockPos>();
        while (blockScanner.hasNext()) {
            pos = blockScanner.getNextPos();
            if (world.getBlockState(pos).isIn(BlockTags.DOORS))
                doors.add(new BlockPos(pos));
        }
        if (doors.isEmpty()) {
            cancel();
            return ParaResult.fail("no doors close to player");
        }

        BlockPos tmp;
        while (!doors.isEmpty()) {
            // Select random door/trapdoor
            tmp = doors.get(Mortisomnia.RANDOM.nextInt(doors.size()));
            doors.remove(tmp);
            if (!player.getBlockPos().isWithinDistance(tmp.toCenterPos(), 4)) {
                door = tmp;
                doorState = world.getBlockState(door).get(Properties.OPEN);
                return ParaResult.success();
            }
        }


        return ParaResult.fail("Doors are too close to player");
    }

    private void endEffect() {
        if (world instanceof ServerWorld w) {
            w.playSound(null, door, MortisomniaSounds.GHOST, SoundCategory.AMBIENT, .65f, 1);
            w.spawnParticles(MortisomniaParticles.ECTOPLASM, door.getX()+.5, door.getY()-.2, door.getZ()+.5, 15, .1, .1, .1, .2);
            w.spawnEntity(new ItemEntity(world, door.getX(), door.getY(), door.getZ(), new ItemStack(MortisomniaItems.ECTO_FRAGMENT, 1)));

        }
    }


    public ParaResult tick() {
        if (timer.tick()) {
            if (toggles > 30) {
                timer.setTime(TICK_DEALY/2, false);
            }
            if (player.getBlockPos().isWithinDistance(door.toCenterPos(), 1.8)) {
                endEffect();
                return ParaResult.end();
            }


            var state = world.getBlockState(door);
            if (state.getBlock() instanceof DoorBlock doorBlock) {
                if (doorState != state.get(Properties.OPEN)) {
                    endEffect();
                    return ParaResult.end();
                }
                doorBlock.setOpen(null, world, state, door, !state.get(Properties.OPEN)); //goofy ass API
                doorState = !doorState;
            }
            else
                return ParaResult.end();


            toggles++;
            if (toggles > MAX_TOGGLES)
                return ParaResult.end();

        }

        return ParaResult.success();
    }

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }

    @Override
    public String getName() {
        return Paractivity.DOOR_TOGGLE;
    }

}
