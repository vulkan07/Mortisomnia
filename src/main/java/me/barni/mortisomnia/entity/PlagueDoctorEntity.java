package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.datagen.MortisomniaParticles;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlagueDoctorEntity extends LivingEntity {
    private static final ItemStack stack = ItemStack.EMPTY;

    public PlagueDoctorEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        setCustomNameVisible(false);
    }

    @Override
    protected float getMaxRelativeHeadRotation() {
        return 40;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }
    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return stack;
    }
    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }
    @Override
    public Arm getMainArm() {
        return null;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        var attacker = source.getAttacker();
        if (attacker instanceof PlayerEntity) {
            attacker.damage(attacker.getWorld().getDamageSources().mobAttack(this), 15);
        }
        return super.damage(source,amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return !damageSource.isOf(DamageTypes.GENERIC_KILL);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public void tick() {
        var world = getWorld();
        if (world instanceof ServerWorld && random.nextInt(3)==0) {
            ((ServerWorld) world).spawnParticles(MortisomniaParticles.PLAGUE, getX(), getY()+1.3, getZ(), 1, 0.20, 0.30, 0.20 ,.2);
        }
        super.tick();
    }
    @Override
    public void tickMovement() {
        PlayerEntity targetPlayer = getWorld().getClosestPlayer(this,32);
        if (targetPlayer == null) return;

        Vec3d target = targetPlayer.getPos().add(0,1.5,0);
        Vec3d source = EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(this);

        double d = target.x - source.x;
        double e = target.y - source.y;
        double f = target.z - source.z;
        double g = Math.sqrt(d * d + f * f);
        float targPitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float targYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);

        this.setPitch((float)MathHelper.lerp(.3, getPitch(), targPitch));
        this.setYaw((float)MathHelper.lerpAngleDegrees(.3, getHeadYaw(), targYaw));
        this.setHeadYaw(getYaw());

    }
}
