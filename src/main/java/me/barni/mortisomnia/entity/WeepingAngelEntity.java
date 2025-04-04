package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WeepingAngelEntity extends LivingEntity {
    private static final ItemStack stack = ItemStack.EMPTY; // Hand

    private static final TrackedData<Byte> POSE = DataTracker.registerData(WeepingAngelEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> VARIANT = DataTracker.registerData(WeepingAngelEntity.class, TrackedDataHandlerRegistry.BYTE);

    public static final byte UNSET_VARIANT = 0;
    public static final byte STONE_VARIANT = 1;
    public static final byte DEEPSLATE_VARIANT = 2;

    public static final byte POSE_WEEPING = 0;
    public static final byte POSE_LOOKING = 1;
    public static final byte POSE_ATTACKING = 2;

    private byte prevPose,pose;

    private final WeepingAngelAI ai = new WeepingAngelAI(this);

    public WeepingAngelEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(POSE, POSE_WEEPING);
        builder.add(VARIANT, UNSET_VARIANT);
    }

    public byte getAngelVariant() {
        return dataTracker.get(VARIANT);
    }
    public byte getAngelPose() {
        return dataTracker.get(POSE);
    }
    public void setAngelPose(byte pose) {
        this.pose = pose;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isRemoved() && isAlive() && !getWorld().isClient()) {
            this.ai.update();

            if (this.pose != this.prevPose && !Utils.canPlayerSeeEntity(ai.getTargetPlayer(),this)) {
                this.prevPose = this.pose;
                dataTracker.set(POSE,pose);
            }

            if (dataTracker.get(VARIANT) == UNSET_VARIANT) { // stupid ahh
                System.out.println("Y POS: "+ getY());
                dataTracker.set(VARIANT, getY() < -5 ? DEEPSLATE_VARIANT : STONE_VARIANT);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("Variant", dataTracker.get(VARIANT));
        ai.save(nbt); // Angel pose is set by the AI's phase so it's not saved separately
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        try {
            if (nbt.contains("Variant"))
                dataTracker.set(VARIANT, nbt.getByte("Variant").get());
            ai.load(nbt);

        } catch (Exception e) {
            Mortisomnia.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void updatePotionVisibility() {
        super.updatePotionVisibility(); // nevermind, we dont need this thankfully (i hope)
    }

    public Identifier getTexture() {
        return switch (this.dataTracker.get(VARIANT)) {
            case DEEPSLATE_VARIANT -> Identifier.of(Mortisomnia.MOD_ID, "textures/entity/weeping_angel_deepslate.png");
            default -> Identifier.of(Mortisomnia.MOD_ID, "textures/entity/weeping_angel.png");
        };
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource damageSource, float amount) {
        if (    damageSource.isOf(DamageTypes.PLAYER_ATTACK) ||
                damageSource.isOf(DamageTypes.PLAYER_EXPLOSION) ||
                damageSource.isOf(DamageTypes.GENERIC_KILL) ||
                damageSource.isOf(DamageTypes.OUT_OF_WORLD) ||
                damageSource.isOf(DamageTypes.CRAMMING) ||
                damageSource.isOf(DamageTypes.LIGHTNING_BOLT)
        ) {
            if (damageSource.getAttacker() instanceof LivingEntity attacker) {
                attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 255, false, true));
                if (!getWorld().isClient()) {
                    if (attacker instanceof PlayerEntity player) {
                        ai.onDamaged();
                        ai.setTargetPlayer(player);
                    }
                }
            }
            return super.damage(world, damageSource, amount);
        }
        else return false;
    }

    // Prevent death animation and death smoke
    @Override
    protected void updatePostDeath() {
        if (!this.getWorld().isClient() && !this.isRemoved()) {
            //this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!getWorld().isClient()) {
            getWorld().playSound(this, getBlockPos(), MortisomniaSounds.SOUL_SFX, SoundCategory.HOSTILE, .27f, 1);
            ((ServerWorld) getWorld()).spawnParticles(MortisomniaParticles.ECTOPLASM, getX(), getY()+1.2, getZ(), 25, 0, .4, 0, .7);
        }

        BlockState block = dataTracker.get(VARIANT) == DEEPSLATE_VARIANT ? Blocks.DEEPSLATE.getDefaultState() : Blocks.STONE.getDefaultState();
        if (getWorld().isClient())
            for (int i = 0; i < 18; i++)
                for (int j = 0; j < 3; j++)
                    MinecraftClient.getInstance().particleManager.addParticle(new BlockDustParticle(
                            (ClientWorld)getWorld(),
                            getX()+random.nextFloat()-.5,
                            getY()+i*.1,
                            getZ()+random.nextFloat()-.5,
                            random.nextFloat()-.5,
                            0.9,
                            random.nextFloat()-.5,
                            block,
                            getBlockPos()));
    }
    public static DefaultAttributeContainer.Builder createWeepingAngelAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 100)
            .add(EntityAttributes.KNOCKBACK_RESISTANCE, .92f);
    }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.BLOCK_GILDED_BLACKSTONE_STEP; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_DEEPSLATE_BREAK; }
    @Override public Arm getMainArm() { return null; }
    @Override public ItemStack getEquippedStack(EquipmentSlot slot) { return stack; }
    @Override public void equipStack(EquipmentSlot slot, ItemStack stack) { }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { /* do nothing */ }
    @Override protected void playSecondaryStepSound(BlockState state) { /* do nothing */ }
    @Override public boolean collidesWith(Entity other) { return true; }
    @Override public boolean isCollidable() { return true; }
    @Override protected boolean canStartRiding(Entity entity) { return false; }
    @Override protected int getExperienceToDrop(ServerWorld world) { return ai.getPhase()*3+ai.getAggression()+3; }
    @Override public boolean doesRenderOnFire() { return false; }
}