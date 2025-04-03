package me.barni.mortisomnia.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MagicParticle extends SpriteBillboardParticle {
    private double ix,iz,phase,rx,rz,nx,nz;
    private boolean collided = false;
    public MagicParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteProvider sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);
        setBoundingBoxSpacing(0.15f,0.15f);
        this.setPos(x, y, z);
        this.velocityX = 0;
        this.velocityZ = 0;
        this.velocityY = 0.05f;
        this.ix = x+motionX;
        this.iz = z+motionZ;
        this.nx = 0;
        this.nz = 0;
        this.rx = (random.nextFloat()-0.5)/40.0;
        this.rz = (random.nextFloat()-0.5)/40.0;
        this.phase = Math.toRadians(motionY);
        this.alpha = 0f;
        this.scale = (this.random.nextFloat()) * 0.22f;

        this.maxAge = 150;
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1f;

        this.setSpriteForAge(sprite);
    }


    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.prevAngle = this.angle;
        this.y += this.velocityY;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        if (maxAge-age < 55)
            this.velocityY+=0.05f;
        if (maxAge-age < 100)
            this.alpha = Math.max(0,alpha-0.01f);
        else if (alpha < 1 && age > 5)
            this.alpha = Math.min(alpha+.2f,1f);

        if (!collided) {
            this.angle += age * .01f;
            this.x = ix + Math.sin(age * age / 1200.0 + phase) * 1.3 * (age * 0.025 + 1.5) + nx;
            this.z = iz + Math.cos(age * age / 1200.0 + phase) * 1.3 * (age * 0.025 + 1.5) + nz;
            this.nx += rx;
            this.nz += rz;
            if (this.collidesWithWorld) {
                Vec3d p = new Vec3d(x - prevPosX, y - prevPosY, z - prevPosZ);
                Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(x - prevPosX, y - prevPosY, z - prevPosZ), this.getBoundingBox(), this.world, List.of());
                float f = this.spacingXZ / 2.0f;
                float g = this.spacingY;
                this.setBoundingBox(new Box(x - (double) f, y, z - (double) f, x + (double) f, y + (double) g, z + (double) f));
                //System.out.printf("%s - %s\n",vec3d,p);
//            System.out.printf("%.2f %.2f %.2f\n",vec3d.x-p.x, vec3d.y-p.y, vec3d.z-p.z);
                if (vec3d.x != p.x || vec3d.y != p.y || vec3d.z != p.z) {
                    this.collided = true;
                    this.velocityY += random.nextFloat()*0.6f;
                }
            }
        }
    }
    @Override
    public int getBrightness(float tint) {
        return 15728880; // Max brightness value (full glow effect)
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        //The factory used in a particle's registry
        private final SpriteProvider spriteProvider;
        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velX, double velY, double velZ) {
            return new MagicParticle(clientWorld, x, y, z, velX, velY, velZ, this.spriteProvider);
        }
    }
}
