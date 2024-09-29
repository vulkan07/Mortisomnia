package me.barni.mortisomnia;


import me.barni.mortisomnia.datagen.MortisomniaBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;


public abstract class Utils {
    private static final Random RANDOM = Mortisomnia.RANDOM;

    public static boolean isFullMoon(World world) {
        return world.getMoonPhase()==0;
    }

    public static NbtCompound getPlayerPersistentData(PlayerEntity player) {
        return ((IEntityNBTSaver) player).mortisomnia$getPersistentData();
    }
    public static Vec3d randomPointOnCircle(float radius) {
        double angle = 2* Math.PI * RANDOM.nextDouble();
        double x = radius * Math.cos(angle);
        double y = radius * Math.sin(angle);
       return new Vec3d(x,0,y);
    }

    public static Vec3d randomPointInSphere(float maxRadius) {
        // Generate a random radius within the specified bounds
        double radius = RANDOM.nextDouble(maxRadius);

        // Generate random angles
        double azimuthalAngle = 2 * Math.PI * RANDOM.nextDouble()+1; // Azimuthal angle (0 to 2*pi)
        double polarAngle = Math.PI * RANDOM.nextDouble()+1; // Polar angle (0 to pi)
        // Convert spherical coordinates to Cartesian coordinates
        double x = radius * Math.sin(polarAngle) * Math.cos(azimuthalAngle);
        double y = radius * Math.sin(polarAngle) * Math.sin(azimuthalAngle);
        double z = radius * Math.cos(polarAngle);
        return new Vec3d(x,y,z);
    }

    public static boolean canPlayerSeeEntity(PlayerEntity e1, Entity e2) {
        if (e1.getWorld() != e2.getWorld())
            return false;

        float dist = e1.distanceTo(e2);

        if (dist > 96.0)
            return false;

        double angleH = (Math.toDegrees(Math.atan2(e2.getZ() - e1.getZ(), e2.getX() - e1.getX())) -e1.getYaw() + 360) % 360;
        int angleCorrection = dist < 1 ? 10 : 0; // if entity is closer than 1, 'shrink' acceptably FOV
        if ((angleH < 350 - angleCorrection && angleH > 190 + angleCorrection) && e1.getPitch() < 50 && e1.getPitch() > -50)
            return false;

        Vec3d[] eyeSpots = {
                e2.getPos().add(-.5, 0,  .5),
                e2.getPos().add( .5, 0, -.5),
                e2.getPos().add(-.5, 0, -.5),
                e2.getPos().add( .5, 0,  .5),
                e2.getPos().add( 0, e2.getHeight()/2,  0),
                e2.getPos().add(-.5, e2.getHeight(),  .5),
                e2.getPos().add( .5, e2.getHeight(), -.5),
                e2.getPos().add(-.5, e2.getHeight(), -.5),
                e2.getPos().add( .5, e2.getHeight(),  .5),
        };

        for (Vec3d pos : eyeSpots) {
            BlockHitResult hitResult = e1.getWorld().raycast(new RaycastContext(e1.getEyePos(), pos, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, e1));

            if (hitResult.getType() == HitResult.Type.MISS)
                return true;
            if (hitResult.getType() == HitResult.Type.BLOCK)
                if (!e1.getWorld().getBlockState(hitResult.getBlockPos()).isOpaque())
                    return true;
        }
        return false;
    }

    /**
     * @return {@code true} if the sky is dark (sun has set) in vanilla rendering.
     * <br><b>Note:</b> The purpose of this is to prevent night-only events happening when the sky is not dark yet.
     */
    public static boolean isCompleteNight(World world) {
        return (world.getTimeOfDay()%24000) > 13500 && (world.getTimeOfDay()%24000) < 22500;
    }
    public static boolean isNightTimeEnoughFor(World world, long seconds) {
        return isCompleteNight(world) && (world.getTimeOfDay()+seconds*20) < 22500;
    }

    public static boolean isBlockLightSource(BlockState blockState) {
        return
                blockState.isOf(Blocks.TORCH) ||
                blockState.isOf(Blocks.WALL_TORCH) ||
                blockState.isOf(Blocks.LANTERN) ||
                blockState.isOf(Blocks.CANDLE) ||
                blockState.isOf(Blocks.CAMPFIRE) ||
                blockState.isOf(Blocks.LAVA) ||
                blockState.isOf(Blocks.GLOW_LICHEN) ||
                blockState.isOf(Blocks.FIRE) ||
                blockState.isOf(Blocks.REDSTONE_LAMP) ||
                blockState.isOf(Blocks.GLOWSTONE) ||
                blockState.isOf(Blocks.END_ROD) ||
                blockState.isOf(Blocks.JACK_O_LANTERN);
    }
    public static boolean isBlockUnlitLightSource(BlockState blockState) {
        return
                blockState.isOf(MortisomniaBlocks.UNLIT_TORCH) ||
                blockState.isOf(MortisomniaBlocks.UNLIT_WALL_TORCH) ||
                blockState.isOf(MortisomniaBlocks.UNLIT_LANTERN) ||
                blockState.isOf(Blocks.CANDLE) ||
                blockState.isOf(Blocks.CAMPFIRE) ||
                blockState.isOf(Blocks.REDSTONE_LAMP) ||
                blockState.isOf(Blocks.CARVED_PUMPKIN);
    }

    public static boolean isBlockKillableFoliage(BlockState blockState) {
        return
                blockState.isOf(Blocks.GRASS_BLOCK) ||
                blockState.isOf(Blocks.SHORT_GRASS) ||
                blockState.isOf(Blocks.TALL_GRASS) ||
                blockState.isOf(Blocks.FERN) ||
                blockState.isOf(Blocks.LARGE_FERN) ||
                blockState.isIn(BlockTags.LEAVES) ||
                blockState.isIn(BlockTags.FLOWERS) ||
                blockState.isIn(BlockTags.LOGS);
    }


    @Nullable
    public static BlockState getKilledFoliageEquivalent(BlockState block) {
        if (block.isOf(Blocks.GRASS_BLOCK))
            return Mortisomnia.RANDOM.nextInt(2)==0 ? Blocks.DIRT.getDefaultState() : Blocks.COARSE_DIRT.getDefaultState();

        if (block.isOf(Blocks.SHORT_GRASS) || block.isOf(Blocks.TALL_GRASS) || block.isOf(Blocks.FERN) || block.isOf(Blocks.LARGE_FERN)) {
            int r = Mortisomnia.RANDOM.nextInt(10);
            if (r < 2)
                return Blocks.COBWEB.getDefaultState();
            if (r < 5)
                return Blocks.DEAD_BUSH.getDefaultState();
            return Blocks.AIR.getDefaultState();
        }
        if (block.isIn(BlockTags.LEAVES) || block.isOf(Blocks.CHERRY_LEAVES))
            return MortisomniaBlocks.DEAD_LEAVES.getDefaultState();

        if (block.isIn(BlockTags.LOGS))
            return MortisomniaBlocks.DEAD_LOG.getDefaultState().with(Properties.AXIS, block.get(Properties.AXIS));

        if (block.isIn(BlockTags.FLOWERS))
            return Mortisomnia.RANDOM.nextInt(4) == 0 ? Blocks.WITHER_ROSE.getDefaultState() : Blocks.AIR.getDefaultState();

        return null;
    }

    public static void killFoliageBlock(World world, BlockPos pos, boolean soundEffect) {
        BlockState newBlock = Utils.getKilledFoliageEquivalent(world.getBlockState(pos)); // Get unlit version
        if (newBlock == null) return;

        world.setBlockState(pos, newBlock); // Set to unlit block

        if (soundEffect)
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, .6f, 2f); // Extinguish sound
    }


    /**
     * Returns a BlockState which represents the unlit version of the block.
     * Might be the same block type or a new one. <br>
     * Keeps the direction and hanging properties. <br><br>
     * <b>Returns null</b> if the block is not replaceable
     **/
    @Nullable
    public static BlockState getUnlitEquivalent(BlockState block) {
        if (block.isOf(Blocks.TORCH))
            return MortisomniaBlocks.UNLIT_TORCH.getDefaultState();

        if (block.isOf(Blocks.WALL_TORCH))
            return MortisomniaBlocks.UNLIT_WALL_TORCH.getDefaultState().with(Properties.HORIZONTAL_FACING, block.get(Properties.HORIZONTAL_FACING));

        if (block.isOf(Blocks.JACK_O_LANTERN))
            return Blocks.CARVED_PUMPKIN.getDefaultState().with(Properties.HORIZONTAL_FACING, block.get(Properties.HORIZONTAL_FACING));

        if (block.isOf(Blocks.LANTERN))
            return MortisomniaBlocks.UNLIT_LANTERN.getDefaultState().with(Properties.HANGING, block.get(Properties.HANGING));

        if (block.isOf(Blocks.CANDLE) || block.isOf(Blocks.CAMPFIRE))
            return block.with(Properties.LIT, false);

        if (block.isOf(Blocks.LAVA))
            return Blocks.COBBLESTONE.getDefaultState();

        if (block.isOf(Blocks.REDSTONE_LAMP))
            return block.with(Properties.LIT, false);

        if (block.isOf(Blocks.GLOWSTONE) || block.isOf(Blocks.END_ROD) || block.isOf(Blocks.FIRE) || block.isOf(Blocks.GLOW_LICHEN))
            return Blocks.AIR.getDefaultState();

        return null;
    }

    @Nullable
    public static BlockState getLitEquivalent(BlockState block) {
        if (block.isOf(MortisomniaBlocks.UNLIT_TORCH))
            return Blocks.TORCH.getDefaultState();

        if (block.isOf(MortisomniaBlocks.UNLIT_WALL_TORCH))
            return Blocks.WALL_TORCH.getDefaultState().with(Properties.HORIZONTAL_FACING, block.get(Properties.HORIZONTAL_FACING));

        if (block.isOf(Blocks.CARVED_PUMPKIN))
            return Blocks.JACK_O_LANTERN.getDefaultState().with(Properties.HORIZONTAL_FACING, block.get(Properties.HORIZONTAL_FACING));

        if (block.isOf(MortisomniaBlocks.UNLIT_LANTERN))
            return Blocks.LANTERN.getDefaultState().with(Properties.HANGING, block.get(Properties.HANGING));

        if (block.isOf(Blocks.CANDLE) || block.isOf(Blocks.CAMPFIRE))
            return block.with(Properties.LIT, true);

        if (block.isOf(Blocks.REDSTONE_LAMP))
            return block.with(Properties.LIT, true);

        return null;
    }

    /**
     * Replaces a block at {@code pos} in {@code world} with an unlit version if possible.
     * Plays extinguish sound and imitates block break effect in addition.
     * @param soundEffect whether to play sound effect or not
     * @param breakEffect whether to do a block break effect
     * @param allowBreak if true, the block gets destroyed if there's no unlit variant
     * @return Returns whether the replacement was successful (depends on the block's type)
     */
    public static boolean unlightBlock(World world, BlockPos pos, boolean soundEffect, boolean breakEffect, boolean allowBreak) {
        BlockState newBlock = Utils.getUnlitEquivalent(world.getBlockState(pos)); // Get unlit version
        if (newBlock == null) return false;
        if (newBlock.isOf(Blocks.AIR) && !allowBreak) return false;

        if (breakEffect && newBlock.isOf(Blocks.AIR)) // Only if block was actually broken
            world.breakBlock(pos, false); // Imitate block break effect

        world.setBlockState(pos, newBlock); // Set to unlit block

        if (soundEffect)
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, .6f, 2f); // Extinguish sound

        return true;
    }

    public static boolean relightBlock(World world, BlockPos pos, boolean soundEffect) {
        BlockState oldBlock = world.getBlockState(pos);
        BlockState newBlock = Utils.getLitEquivalent(world.getBlockState(pos)); // Get unlit version
        if (newBlock == null) return false;

        world.setBlockState(pos, newBlock); // Set to unlit block

        if (soundEffect && oldBlock != newBlock)
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.NEUTRAL, .6f, 1f); // Extinguish sound

        return true;
    }

    /**
     * Returns how likely the block's type is to be on overworld's surface
     * Returns negative if the type indicates not surface block
     * @param block The block to check
     * @return {@code int in [-20;5]}
     */
    public static int getBlockSurfaceWeight(BlockState block) {
        if (block.isOf(Blocks.GRASS_BLOCK)) return 5;
        if (block.isIn(BlockTags.LEAVES)) return 5;
        if (block.isOf(Blocks.SAND)) return 2;

        if (block.isOf(Blocks.STONE)) return -1;

        if (block.isOf(Blocks.MOSS_BLOCK)) return -2;
        if (block.isOf(Blocks.ANDESITE)) return -2;
        if (block.isOf(Blocks.DIORITE)) return -2;
        if (block.isOf(Blocks.GRANITE)) return -2;

        if (block.isOf(Blocks.DEEPSLATE)) return -20;
        return 0;
    }

    public static boolean isPlayerAlone(PlayerEntity player, int threshold, boolean checkOnlyPlayers) {
        if (checkOnlyPlayers) {
            for (PlayerEntity other : player.getWorld().getPlayers())
                if (!player.equals(other) && player.distanceTo(other) < threshold)
                    return false;
        } else {
            for (Entity e : player.getWorld().getOtherEntities(player, new Box(player.getBlockPos()).expand(threshold)))
                if (player.distanceTo(e) < threshold)
                    return false;
        }
        return true;
    }

    public static float getAverageLightnessAroundPlayer(PlayerEntity player) {
        World world = player.getWorld();
        BlockPos origin = player.getBlockPos();

        int x,y,z;
        final int SCAN = 50;

        var values = new ArrayList<Integer>();
        for (int i = 0; i < SCAN; i++) {
            x = RANDOM.nextInt(20)-10;
            y = RANDOM.nextInt(20);
            z = RANDOM.nextInt(20)-10;
            BlockPos pos = origin.add(x,y,z);
            if (world.getBlockState(pos).isOf(Blocks.AIR))
                values.add(
                        Math.max(world.getLightLevel(LightType.BLOCK,pos),
                                world.getLightLevel(LightType.SKY,pos)));
        }
        if (values.isEmpty())
            return 0;

        int sum = 0;
        for (int i : values)
            sum += i;
        return (float)sum/values.size();
    }

    /**
     * Returns how likely the block's type is to be in a cave.
     * Returns negative if the type indicates not in a cave (i.e. grass).
     * The {@code yLevel} is used for {@code stone}, because it increases it's value based on height.
     * @param block The block to check
     * @param yLevel The Y level of the block
     * @return {@code int in [-2;10]}
     */
    public static int getBlockCaveWeight(BlockState block, int yLevel) {
        // NOT CAVE BLOCKS
        if (block.isOf(Blocks.SAND)) return -2;
        if (block.isOf(Blocks.GRASS_BLOCK)) return -2;
        // NEUTRAL BLOCKS
        if (block.isOf(Blocks.CLAY)) return 1;
        if (block.isOf(Blocks.GRAVEL)) return 1;
        if (block.isOf(Blocks.DIRT)) return 1;
        // PROBABLY CAVE BLOCKS
        if (block.isOf(Blocks.MOSS_BLOCK)) return 2;
        if (block.isOf(Blocks.ANDESITE)) return 2;
        if (block.isOf(Blocks.DIORITE)) return 2;
        if (block.isOf(Blocks.GRANITE)) return 2;
        // CERTAINLY CAVE BLOCKS
        if (block.isOf(Blocks.STONE)) return yLevel > 20 ? 2 : 4; // Stone gets stronger if Y < 20
        if (block.isOf(Blocks.DEEPSLATE)) return 10;
        if (block.isOf(Blocks.CAVE_AIR)) return 10;
        return 0;
    }

    /**
     * @return A non-negative integer:<br>
     * <code>i = 0</code> Player is definitely not in a cave.<br>
     * <code>i < 100</code> Player is unlikely to be in a cave.<br>
     * <code>i > 100</code> Player is probably in a cave.<br>
     * <code>i > 250</code> Player is certainly in a cave.<br>
     * <code>i > 400</code> Player is probably in deepslate cave.
     */
    public static int isPlayerInCave(PlayerEntity player) {
        if (player.getY() > 90) return 0; // If Y>80 it is very unlikely to be a cave so the check gets skipped


        World world = player.getWorld();
        BlockPos pos = player.getBlockPos();
        int lightScore = 0, surroundScore = 0, verticalScore = 0;
        int certainty = 50;
        int x,y,z;
        for (int i = 0; i < 30; i++) {
            x = RANDOM.nextInt(20)-10;
            y = RANDOM.nextInt(20);
            z = RANDOM.nextInt(20)-10;
            if (world.getLightLevel(LightType.SKY,pos.add(x,y,z)) > 0)
                return 0;
            lightScore -= world.getLightLevel(LightType.BLOCK, pos.add(x,y,z));
        }
        BlockState state;
        for (int i = 0; i < 30; i++) {
            y = (RANDOM.nextInt(11)-10)*2;
            state = world.getBlockState(pos.add(
                    (RANDOM.nextInt(11)-10)*2, y, (RANDOM.nextInt(11)-10)*2
            ));
            surroundScore += getBlockCaveWeight(state, y);

        }

        for (int i = 10; i < 50; i+=2) {
            state = world.getBlockState(pos.add(0, i, 0));
            if (state.isAir())
                verticalScore -= 5;
            else
                verticalScore += getBlockCaveWeight(state, pos.getY()+i);
        }

        // Half weight of the light score (due to glow berries and lava giving false negatives)
        lightScore *= .5;

        // Under -20Y, the chance starts increasing with depth
        if (player.getY() < -20 ) certainty -= (int) player.getY()*2;

        Mortisomnia.LOGGER.info("light: " + lightScore + ", surround: " + surroundScore + ", vertical: " + verticalScore + ",    Total: " + (certainty+lightScore+surroundScore+verticalScore));
        return Math.max(certainty+lightScore+surroundScore+verticalScore, 0);
    }

    public static class TickTimer {
        public int time, max;
        public boolean reset;
        public TickTimer(int time) {
            this.max = Math.max(time, 1);
            this.time = this.max;
            this.reset = true;
        }
        public TickTimer(int time, boolean reset) {
            this.max = Math.max(time, 1);
            this.time = this.max;
            this.reset = reset;
        }

        public void setTime(int time, boolean reset) {
            this.max = time;
            if (reset) this.time = this.max;
        }

        /** Returns true if timer is firing **/
        public boolean tick() {
            this.time--;
            if (this.time < 1) {
                if (reset)
                    this.time = this.max;
                return true;
            }
            return  false;
        }

        public void reset() {this.time = max;}
    }

    public static class BlockScanner {
        private final BlockPos.Mutable pos = new BlockPos.Mutable();
        private int index;
        private final int startX, startY, startZ, endX, endY, endZ, lenX, lenY, lenZ, lenTotal;

        public BlockScanner(Box box) {
            startX = (int) box.minX;
            startY = (int) box.minY;
            startZ = (int) box.minZ;
            endX = (int) box.maxX;
            endY = (int) box.maxY;
            endZ = (int) box.maxZ;

            lenX = endX - startX + 1;
            lenY = endY - startY + 1;
            lenZ = endZ - startZ + 1;
            lenTotal = lenX*lenY*lenZ;
        }

        public int getTotalLength() {
            return lenTotal;
        }
        public int getIndex() {
            return index;
        }

        public boolean hasNext() {
            return (index < lenTotal);
        }

        public BlockPos.Mutable getNextPos() {
            if (index == lenX*lenY*lenZ) return null;

            int xShift = index % lenX;
            int m = index / lenX;
            int yShift = m % lenY;
            int zShift = m / lenY;
            this.index++;
            return this.pos.set(startX + xShift, startY + yShift, startZ + zShift);
        }
    }

}