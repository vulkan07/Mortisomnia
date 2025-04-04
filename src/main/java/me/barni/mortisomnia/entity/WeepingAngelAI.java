package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.Stack;

public class WeepingAngelAI {
    private static final Random RANDOM = Mortisomnia.RANDOM;
    private static final int VERTICAL_RANGE = 5;
    public static final int MAX_AGGRESSION = 70;

    public static final int V_SCAN = 16;
    public static final int H_SCAN = 32;
    public static final int MAX_SCAN_PER_TICK = 32*16*16;

    public static final int PHASE_DORMANT = 0; // AI does not attack over time, only from external trigger (e.g. damaged by player)
    public static final int PHASE_WARMUP = 1; // Default spawn, waits some time before doing anything
    public static final int PHASE_PEEK = 2; // Looks at player occasionally
    public static final int PHASE_FOLLOW = 3; // Teleports behind player sometimes
    public static final int PHASE_ATTACK = 4; // Teleports frequently, attacks when close enough

    private PlayerEntity targetPlayer;
    private final WeepingAngelEntity angel;

    public boolean pendingLook = false; // set true to make angel look at player as soon as it is not seen
    private int phase; // Determines AI behavior
    private int aggression = 0; // 0-100
    private final int phaseDelay = 120 + RANDOM.nextInt(20); // Depends on timer time
    private BlockPos prevPos = new BlockPos(0,0,0);

    private final Utils.TickTimer updateTimer = new Utils.TickTimer(20);
    private final Utils.TickTimer phaseTimer = new Utils.TickTimer(phaseDelay);
    private final Utils.BlockScanner blockScanner;
    private final Stack<BlockPos> lights = new Stack<>();

    @Nullable
    public PlayerEntity getTargetPlayer() { return targetPlayer; }
    public int getPhase() { return phase; }
    public int getAggression() { return aggression; }

    public void incrementPhase() {
        int old = phase;
        this.phase = Math.min(phase + 1, PHASE_ATTACK);
        if (old != phase) // Only decrease aggression if actual change happened
            this.aggression /= 5;
        setAngelPose();
    }

    public void incrementAggression() {
        if (RANDOM.nextInt(3)==0)
            if (angel.getAngelVariant() == WeepingAngelEntity.DEEPSLATE_VARIANT)
                this.aggression = Math.min(aggression+3, MAX_AGGRESSION);
            else
                this.aggression = Math.min(aggression+1, MAX_AGGRESSION);
    }

    public void onDamaged() {
        this.phase = PHASE_ATTACK;
        this.aggression = Math.min(aggression+MAX_AGGRESSION/4, MAX_AGGRESSION);
        this.pendingLook = true;
        setAngelPose();
    }

    private void setAngelPose() {
        byte pose = WeepingAngelEntity.POSE_WEEPING;
        if (phase > PHASE_WARMUP) pose = WeepingAngelEntity.POSE_LOOKING;
        if (phase >= PHASE_ATTACK) pose = WeepingAngelEntity.POSE_ATTACKING;
        angel.setAngelPose(pose);
    }

    public WeepingAngelAI(WeepingAngelEntity entity) {
        this.angel = entity;
        this.blockScanner = new Utils.BlockScanner(angel.getBoundingBox().expand(H_SCAN,V_SCAN,H_SCAN));
        this.phase = PHASE_WARMUP;
    }
    public void save(NbtCompound nbt) {
        nbt.putInt("Phase", phase);
        nbt.putInt("Aggression", aggression);
    }
    public void load(NbtCompound nbt) {
        if (nbt.contains("Phase")) {
            this.phase = Math.min(nbt.getInt("Phase"), PHASE_ATTACK);
            setAngelPose();
        }
        if (nbt.contains("Aggression")) {
            this.aggression = Math.min(nbt.getInt("Aggression"), MAX_AGGRESSION);
        }
    }

    // NOTE: treats non-solids like torch and grass as solid
    // NOTE: Blocks with higher hitboxes (e.g. fences) break this
    private boolean isValidPos(World world, BlockPos bpos) {
        for (var e : world.getOtherEntities(angel, new Box(bpos).expand(1))) {
            if (e instanceof WeepingAngelEntity) return false;
        }
        boolean valid;
        valid = !world.getBlockState(bpos.down()).isAir();
        valid &= !world.getBlockState(bpos.down()).isOf(Blocks.WATER);
        valid &= !world.getBlockState(bpos.down()).isOf(Blocks.LAVA);
        valid &= world.getBlockState(bpos).isAir();
        valid &= world.getBlockState(bpos.up(1)).isAir();
        valid &= world.getBlockState(bpos.up(2)).isAir();
        return valid;
    }

    private void moveBehindPlayer() {
        float dst = targetPlayer.getPos().distanceTo(angel.getPos()) < 2.3f ? 1.0f : 1.9f; // Move even closer if close

            Vec3d targetPos = targetPlayer.getPos().add(
                0 + Math.cos(Math.toRadians(targetPlayer.getHeadYaw() -90)) * dst,
                0,
                0 + Math.sin(Math.toRadians(targetPlayer.getHeadYaw() -90)) * dst
        );

        BlockPos bpos = new BlockPos(Math.round((float)targetPos.x-.5f), (int)targetPos.y, Math.round((float)targetPos.z-.5f));

        for (int y = VERTICAL_RANGE /2; y > -VERTICAL_RANGE /2; y--)
            if (isValidPos(angel.getWorld(), bpos.up(y))) {
                moveAngel(bpos.up(y));
                return;
            }
        for (int y = VERTICAL_RANGE; y > -VERTICAL_RANGE; y--)
            if (isValidPos(angel.getWorld(), bpos.up(y))) {
                moveAngel(bpos.up(y));
                return;
            }
    }

    public void lookAt(PlayerEntity player, boolean headOnly) {
        // Approximate next player position like it was following and predicting where the player moves
        Vec3d target = player.getSyncedPos().add(player.getMovement().multiply(7,0,7)).add(0,player.getStandingEyeHeight(),0);
        Vec3d vec3d = EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(angel);
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);
        float pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        angel.setAngles(headOnly ? angel.getYaw() : yaw, pitch);
        angel.setHeadYaw(yaw);

    }

    /**
     * Update the targeted player if there is none, or it is dead to the closest one
     */
    private void updatePlayer() {
            float minDist = Float.POSITIVE_INFINITY;
            float d;
            this.targetPlayer = null;
            for (var p : angel.getWorld().getPlayers()) {
                if (!Utils.isPlayerCandidate(p))
                    continue;
                d = (float) angel.squaredDistanceTo(p);
                if (d < minDist) {
                    minDist = d;
                    this.targetPlayer = p;
                }
            }
    }

    /*
     * damage player if possible and within distance, despawn if also killed them
     */
    private boolean attackPlayer() {
        if (!Utils.isPlayerCandidate(this.targetPlayer)) return false;

        if (targetPlayer.getPos().distanceTo(angel.getPos()) < 1.25) {
            boolean damaged = targetPlayer.damage(angel.getWorld().getDamageSources().mobAttack(angel), RANDOM.nextInt(15, 18));
            if (targetPlayer.isDead()) angel.discard(); // Despawn the Angel if killed the player
            return damaged;
        }
        return false;
    }

    /**
    * Called when the entity gets damaged for example
    */
    public void setTargetPlayer(PlayerEntity player) {
        this.targetPlayer = player;
    }

    private boolean isValidLightBlock(BlockState blockState) {
        return  blockState.isOf(Blocks.TORCH) ||
                blockState.isOf(Blocks.WALL_TORCH) ||
                blockState.isOf(Blocks.LANTERN) ||
                blockState.isOf(Blocks.CAMPFIRE) ||
                blockState.isOf(Blocks.JACK_O_LANTERN);
    }

    private void moveAngel(BlockPos bpos) {
        if (bpos.equals(prevPos)) return;
        prevPos = bpos;
        angel.setPosition(bpos.getX() + .5, bpos.getY(), bpos.getZ() + .5);
        blockScanner.setBox(angel.getBoundingBox().expand(H_SCAN, V_SCAN, H_SCAN));
        lights.clear(); // Forces a re-scan of lights
        blockScanner.reset();
    }

    public void update() {
        if (phase == PHASE_DORMANT) return;

        if (Utils.isPlayerCandidate(targetPlayer) && angel.getAngelVariant() == WeepingAngelEntity.DEEPSLATE_VARIANT && phase == PHASE_ATTACK) {
            BlockPos pos;
            if (lights.isEmpty() || blockScanner.hasNext()) {
//                targetPlayer.sendMessage(Text.of(String.valueOf((float) blockScanner.getIndex() / blockScanner.getTotalLength() * 100f)));
                for (int i = 0; i < MAX_SCAN_PER_TICK; i++) {
                    if (!blockScanner.hasNext()) {
                        lights.sort((o1, o2) -> {
                            var a = o1.getSquaredDistance(targetPlayer.getPos());
                            var b = o2.getSquaredDistance(targetPlayer.getPos());
                            if (a == b) return 0;
                            return a < b ? 1 : -1;
                        });
                        break;
                    }
                    pos = blockScanner.getNextPos();
                    if (isValidLightBlock(angel.getWorld().getBlockState(pos)))
                        lights.push(new BlockPos(pos));
                }

            }
        }

        if (updateTimer.tick() && angel.getWorld().isNight()) { // Only progress anything at night
            if (phaseTimer.tick()) incrementPhase(); // AI progresses without a suitable player!

            updatePlayer(); // select who is targeted

            if (!Utils.isPlayerCandidate(this.targetPlayer)) return;
            if (Utils.canAnyPlayersSeeEntity(angel.getWorld(), angel)) {
                if (!lights.isEmpty()) {
                    BlockPos pos;
                    pos = lights.pop();
                    Utils.unlightBlock(angel.getWorld(), pos, true, false, true);
                }
                return;
            }

            // ATTACK
            if (phase == PHASE_ATTACK)
                if (attackPlayer()) return;

            if (phase > PHASE_WARMUP)
                incrementAggression();

            // MOVE
            if (phase >= PHASE_FOLLOW && RANDOM.nextInt(Math.max(1, MAX_AGGRESSION - aggression)) == 0) {
                moveBehindPlayer();
                lookAt(this.targetPlayer, false);
            }

            // LOOK
            if (phase == PHASE_PEEK && RANDOM.nextInt(Math.max(1, MAX_AGGRESSION - aggression)) == 0 || pendingLook) {
                lookAt(this.targetPlayer, false);
                pendingLook = false;
            }

        }
    }
}


