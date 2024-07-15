package me.barni.mortisomnia.events;

import me.barni.mortisomnia.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OnUseBlockHandler {
    public static ActionResult onUseBlock(PlayerEntity user, World world, Hand hand, BlockHitResult hitResult) {

        if (user.getStackInHand(hand).isOf(Items.WOODEN_HOE)) {
            if (world.isClient())
                return ActionResult.PASS;
            return Utils.unlightBlock(world, hitResult.getBlockPos(), true, true, false) ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        if (user.getStackInHand(hand).isOf(Items.FLINT_AND_STEEL)) {
            BlockPos pos = hitResult.getBlockPos();

            if (world.isClient()) {
                return Utils.isBlockUnlitLightSource(world.getBlockState(pos)) ? ActionResult.SUCCESS : ActionResult.PASS;
            }

            boolean success = Utils.relightBlock(world, hitResult.getBlockPos(), true);

            if (success) {
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.NEUTRAL, .6f, 1f);

                if (!user.isCreative())
                    user.getStackInHand(hand).damage(1, user, LivingEntity.getSlotForHand(hand));
            }
            return success ? ActionResult.SUCCESS : ActionResult.PASS;

        }

        return ActionResult.PASS;
    }
}
