package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class WeepingAngelAI {
    private static final Random RANDOM = Mortisomnia.RANDOM;
    private static final int Y_STEP_RANGE = 6;
   // public static final int s_DORMANT = 0; // Has to be awoken externally, else it doesn't do anything - used for structures
    public static final int s_SPAWN = 0; // When spawns naturally, start as inactive
    public static final int s_LOOK_ONLY = 1;
    public static final int s_AWAKE = 2;

    private final WeepingAngelEntity entity;
    private final Utils.TickTimer updateTimer = new Utils.TickTimer(20);

    private PlayerEntity targetPlayer;
    public float pitch, yaw;
    public boolean dormant = false;



    public int phase;
    public int aggression = 0;
    public int phaseDelay = RANDOM.nextInt(500,1200);

    public WeepingAngelAI(WeepingAngelEntity entity) {
        this.entity = entity;
        this.phase = s_SPAWN;
    }

    private boolean isValidPlaceToMove(World world, BlockPos pos) {
        world.getMoonPhase();
        //dont move too close to other angels
        for (var e : world.getOtherEntities(entity, new Box(pos).expand(1))) {
            if (e instanceof WeepingAngelEntity) return false;
        }
        boolean valid;
        valid = !world.getBlockState(pos.down()).isAir() // block below is not air or fluid
                && !world.getBlockState(pos.down()).isOf(Blocks.WATER)
                && !world.getBlockState(pos.down()).isOf(Blocks.LAVA);
        valid &= world.getBlockState(pos).isAir();
        valid &= world.getBlockState(pos.up()).isAir();
        valid &= world.getBlockState(pos.up(2)).isAir();
        return valid;
    }
    private void moveBehindPlayer() {
        if (targetPlayer.getPos().distanceTo(entity.getPos()) > 5) {
            Vec3d pos = targetPlayer.getPos().add(
                    0 + Math.cos(Math.toRadians(targetPlayer.getHeadYaw() - (87+RANDOM.nextInt(7)))) * RANDOM.nextFloat(1f,2.5f),
                    0,
                    0 + Math.sin(Math.toRadians(targetPlayer.getHeadYaw() - (87+RANDOM.nextInt(7)))) * RANDOM.nextFloat(1f,2.5f)
            );
            Vec3d finalPos = new Vec3d((int)pos.x, (int)pos.y, (int)pos.z);
            BlockPos blockPos = new BlockPos((int)finalPos.x, (int)finalPos.y, (int)finalPos.z);

            for (int y = blockPos.getY();   y < blockPos.getY()+Y_STEP_RANGE;  y++) {
                if (isValidPlaceToMove(entity.getWorld(), blockPos.up(blockPos.getY()-y))){
                    entity.setPosition(finalPos.add(.5, blockPos.getY()-y, .5));
                    entity.prevX = entity.getX();
                    entity.prevY = entity.getY();
                    entity.prevZ = entity.getZ();
                    return;
                }
            }
            for (int y = blockPos.getY()-Y_STEP_RANGE;  y < blockPos.getY();  y++) {
                if (isValidPlaceToMove(entity.getWorld(), blockPos.up(blockPos.getY()-y))){
                    entity.setPosition(finalPos.add(.5, blockPos.getY()-y, .5));
                    entity.prevX = entity.getX();
                    entity.prevY = entity.getY();
                    entity.prevZ = entity.getZ();
                    return;
                }
            }

        } else {
            // move up to player's Y level (emulation of Jump)
            if (targetPlayer.getY() - entity.getY() > .5)
                entity.setPosition(entity.getPos().add(0, targetPlayer.getY() - entity.getY(), 0));
            entity.setVelocity(targetPlayer.getPos().subtract(entity.getPos()).multiply(.2));
        }

    }

    public void update(boolean isClient) {
        if (isClient) return;

        // look at target every frame to prevent the pitch & yaw from resetting TODO fix rotation tick reset
        entity.setPitch(pitch);
        entity.setYaw(yaw);
        entity.setHeadYaw(yaw);

        if (dormant)
            return;


        if (entity.getWorld().isNight())
            if (updateTimer.tick()) {

                /*if (targetPlayer != null) {
                    if (!Utils.canPlayerSeeEntity(targetPlayer, entity))
                        targetPlayer.sendMessage(Text.literal("You can't see the angel"), false);
                    return;
                }*/

                if (entity.age > phaseDelay*(phase+1)) {
                    phase = Math.min(phase + 1, s_AWAKE); // Dont go above s_AWAKE
//                    Mortisomnia.LOGGER.info("[WeepingAngel] Phase is now " + phase);
                }
                // Increment aggression by 1 or 0 every tick if phase is past s_LOOK_ONLY
                if (phase > s_LOOK_ONLY) {
                    aggression += RANDOM.nextInt(31) == 0 ? 1 : 0;
 //                   Mortisomnia.LOGGER.info("[WeepingAngel] aggression is now " + aggression);
                }

                // Return if not active yet or
                // With greater aggression, be more chance of stepping
                // TOTO goofy fix: elevates chances of LOOK_ONLY phase and ignores aggression
                if (phase == s_SPAWN || RANDOM.nextInt(60) > (phase == s_LOOK_ONLY ? 10 : aggression))
                    return;

//                Mortisomnia.LOGGER.info("[WeepingAngel] moved - aggression:" + aggression);

                if (entity.getWorld().getPlayers().isEmpty()) {
                    targetPlayer = null;
                    return;
                }
                targetPlayer = entity.getWorld().getPlayers().getFirst(); // TODO multiplayer support

                if (targetPlayer.isSpectator() || targetPlayer.isCreative())
                    return;

                if (!Utils.canPlayerSeeEntity(targetPlayer, entity)) {

                    // Damage player if close enough
                    if (targetPlayer.getPos().distanceTo(entity.getPos()) < 1.25)
                        targetPlayer.damage(entity.getWorld().getDamageSources().mobAttack(entity), RANDOM.nextInt(15,22));
                    // Or move towards it
                    else if (phase != s_LOOK_ONLY)
                        moveBehindPlayer();

                    // Look at player and save pitch&yaw to prevent resetting it by minecraft
                    Vec3d target = targetPlayer.getPos().add(0, 1.8, 0);
                    Vec3d vec3d = EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(entity);
                    double d = target.x - vec3d.x;
                    double e = target.y - vec3d.y;
                    double f = target.z - vec3d.z;
                    double g = Math.sqrt(d * d + f * f);
                    pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
                    yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);


                }
            }
    }
}
