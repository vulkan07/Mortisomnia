package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.mixin.TrapdoorBlockInvoker;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Stack;


public class DoorParactivity extends Paractivity {

    private final int SCAN_VERTICAL = 6;
    private final int SCAN_HORIZONTAL = 10;
    private final int ALONE_THRESHOLD = 48;

    public DoorParactivity(PlayerEntity player) {
        super(player);
        setMeta(1,6, 7,100);
    }

    // Returns true if there are no PLAYERS AND VILLAGERS within ALONE_THRESHOLD
    private boolean isPlayerAlone() {
        for (Entity e : player.getWorld().getOtherEntities(player, new Box(player.getBlockPos()).expand(ALONE_THRESHOLD)))
            if (e instanceof  PlayerEntity)
                //if (e instanceof  PlayerEntity || e instanceof VillagerEntity)
                if (player.distanceTo(e) < ALONE_THRESHOLD)
                    return false;
        return true;
    }

    @Override
    protected ParaResult customInit() {
        if (!Utils.isCompleteNight(world) && Mortisomnia.RANDOM.nextInt(5)==0) // if not night, 1:5 chance of happpening
            return ParaResult.fail("not night");
        if (!isPlayerAlone())
            return ParaResult.fail("player not alone");

        return ParaResult.success();
    }

    private boolean isValidBlock(BlockState blockState) {
        return  blockState.isIn(BlockTags.DOORS) ||
                blockState.isIn(BlockTags.TRAPDOORS);
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
            return ParaResult.fail("no doors close to player");
        }

        // Select random door/trapdoor
        pos = lights.get( Mortisomnia.RANDOM.nextInt(lights.size()) );
        var state = world.getBlockState(pos);
        if (state.getBlock() instanceof DoorBlock doorBlock)
            doorBlock.setOpen(null, world, state, pos, !state.get(Properties.OPEN)); //goofy ass API
        if (state.getBlock() instanceof TrapdoorBlock trapdoorBlock)
                ((TrapdoorBlockInvoker) trapdoorBlock).invokeFlip(state, world, pos, null);

        return end();
        /*
        if (niga.tick()) {
            counter ++;
            if (player.isDead())
                return end();
            for (Entity e : world.getOtherEntities(null, new Box(player.getBlockPos()).expand(32))) {
            e.velocityModified = true;
            e.velocityDirty = true;
            if (counter < 2)
                e.addVelocity(0, 2.5, 0);
            if (counter < 7)
                return ParaResult.success();

            e.addVelocity(
                    (Mortisomnia.RANDOM.nextDouble()-.5) * 2,
                    Mortisomnia.RANDOM.nextDouble(.2)+.30,
                    (Mortisomnia.RANDOM.nextDouble()-.5) *2
            );
            e.setVelocity( Math.min(player.getVelocity().x, 1) , Math.min(player.getVelocity().y, 1), Math.min(player.getVelocity().z, 1));
            e.damage(world.getDamageSources().generic(), 2);
            }

        }

        return (counter < 50) ? ParaResult.success() : end();
        */
    }
    /*
    private int counter = 0;
    private Utils.TickTimer niga = new Utils.TickTimer(5);
     */

    @Override
    public boolean permitsParactivity(Paractivity other) {
        return false;
    }

    @Override
    public String getName() {
        return Paractivity.DOOR_ACTIVITY;
    }

}
