package me.barni.mortisomnia.item;

import me.barni.mortisomnia.Mortisomnia;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PurifiedEctoFragmentItem extends Item {
    public PurifiedEctoFragmentItem(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
/*
        //if(!world.isClient())
        user.sendMessage(Text.literal( WeepingAngelParactivity.canSpawnOnBlock(world,user.getBlockPos().down()) ? "solid below" : "nuh uh"), false);

        if (!world.isClient()){
            // Default particle pos is the player's look direction*1
            Vec3d particlePos = user.getPos().add(user.getRotationVec(1).add(0, 1.7, 0));

            // Try finding the target block with a raycast
            RaycastContext raycastContext = new RaycastContext(
                    user.getCameraPosVec(1),
                    user.getCameraPosVec(1).add(user.getRotationVec(1).multiply(5)),
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    user
            );
            // Override particle pos to the raycast's result if it was successful
            BlockHitResult hitResult = world.raycast(raycastContext);
            if (hitResult.getType() != HitResult.Type.MISS)
                particlePos = hitResult.getPos();

            ((ServerWorld) world).spawnParticles(
                    MortisomniaParticles.ECTOPLASM,
                    particlePos.x, particlePos.y, particlePos.z,
                    16, .05, .1, .05, .5);

            world.playSound(null, particlePos.x, particlePos.y, particlePos.z,
                    SoundEvents.ENTITY_ALLAY_DEATH, SoundCategory.NEUTRAL, .25f, 1.2f);
        }
*/
        user.getStackInHand(hand).decrement(1);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}