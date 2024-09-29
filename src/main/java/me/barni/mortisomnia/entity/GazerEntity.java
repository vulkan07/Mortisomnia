package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GazerEntity extends MobEntity {

    private boolean disappearFlag = false;
    private boolean triggered = false;
    private int triggerTimer = 0;

    public void setDisappear() {
        this.disappearFlag = true;
    }

    private void disappear() {
        World world = this.getWorld();
        if (world instanceof ServerWorld serverWorld) {

            serverWorld.spawnParticles(ParticleTypes.SMOKE, this.getPos().x, this.getPos().y+1f, this.getPos().z, 12, .25, 1, .25, 0);
//            serverWorld.spawnParticles(MortisomniaParticles.ECTOPLASM, this.getPos().x, this.getPos().y+1f, this.getPos().z, 12, .1, 1, .1, 1);
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, .7f, 1f);

            this.discard();
        }
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

        boolean shaking = triggerTimer > 0;

        this.setHeadYaw((float)MathHelper.lerpAngleDegrees(.5, getHeadYaw(), targYaw) + Mortisomnia.RANDOM.nextFloat() * (shaking ? 4 : 0) );
        this.setPitch((float)MathHelper.lerp(.5, getPitch(), targPitch) + Mortisomnia.RANDOM.nextFloat() * (shaking ? 4 : 0) );
        this.setBodyYaw(getHeadYaw());

    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        this.setDisappear();
    }

    private double levitateVelocity = 0;

    @Override
    public void tick() {
        super.tick();
        if (disappearFlag) disappear();
        if (triggered) {
            triggerTimer++;
            levitateVelocity -= .010;
            if (triggerTimer > 40 && triggerTimer < 110) {
                levitateVelocity += .020;
            }
            levitateVelocity = MathHelper.clamp(levitateVelocity, 0, .1);

            this.setPosition(getX(), getY()+levitateVelocity, getZ());

            if (triggerTimer > 180) setDisappear();
        }
    }


    @Override
    public boolean damage(DamageSource damageSource, float amount) {
        triggered = true;
        return super.damage(damageSource,amount);
    }
    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return !damageSource.isOf(DamageTypes.GENERIC_KILL);
    }

    public GazerEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createGazerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }
}
