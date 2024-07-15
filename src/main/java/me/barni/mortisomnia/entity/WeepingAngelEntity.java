package me.barni.mortisomnia.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
public class WeepingAngelEntity extends MobEntity {
    private final WeepingAngelAI ai = new WeepingAngelAI(this);

    //TODO THIS IS HORRIBLE!!! FIX THE STUPID ROTATION
    public void setAngelYaw(float yaw) {
        this.setYaw(yaw);
        ai.yaw = yaw;
//        this.lookControl.;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isRemoved() && isAlive())
            ai.update(getWorld().isClient());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("ai_dormant", ai.dormant);
        nbt.putInt("ai_phase", ai.phase);
        nbt.putInt("ai_aggression", ai.aggression);
        return nbt;
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try {
            ai.phase = nbt.getInt("ai_phase");
            ai.dormant = nbt.getBoolean("ai_dormant");
            ai.aggression = nbt.getInt("ai_aggression");
            ai.pitch = getPitch();
            ai.yaw = getYaw();
        } catch (Exception ignored) {}
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_GILDED_BLACKSTONE_STEP;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_DRIPSTONE_BLOCK_BREAK;
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { /* do nothing */ }
    @Override
    protected void playSecondaryStepSound(BlockState state) { /* do nothing */ }

    @Override
    public boolean damage(DamageSource damageSource, float amount) {
        if (    damageSource.isOf(DamageTypes.IN_FIRE) ||
                damageSource.isOf(DamageTypes.ON_FIRE) ||
                damageSource.isOf(DamageTypes.DROWN) ||
                damageSource.isOf(DamageTypes.ARROW) ||
                damageSource.isOf(DamageTypes.MOB_PROJECTILE) ||
                damageSource.isOf(DamageTypes.IN_WALL) ||
                damageSource.isOf(DamageTypes.FALL)
        ) return false;

        if (damageSource.getAttacker() instanceof LivingEntity attacker) {
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 2, true, false));
            if (attacker instanceof PlayerEntity){
                ai.phase = WeepingAngelAI.s_AWAKE;
                ai.aggression += 25;
            }
            ItemStack stack = attacker.getMainHandStack();
            if (stack.isDamageable()) {
//TODO MIGRATE                stack.damage(180, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }

        return super.damage(damageSource, amount);
    }

    public WeepingAngelEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createWeepingAngelAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, .7f);
    }
    @Override
    public boolean collidesWith(Entity other) {
        return true;
    }
    @Override
    public boolean isCollidable() {
        return true;
    }
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }
    @Override
    public int getXpToDrop() {
        return MathHelper.clamp(ai.aggression/4, 2, 15);
    }
    @Override
    public boolean canBeLeashed() {
        return false;
    }
    @Override
    public boolean doesRenderOnFire() {
        return false;
    }
}