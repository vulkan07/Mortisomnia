package me.barni.mortisomnia.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class PlagueParticle extends SpriteBillboardParticle {
    private final SpriteProvider sprite;
    public PlagueParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteProvider sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = motionX *  0.4;
        this.velocityY = 0;
        this.velocityZ = motionZ *  0.4;
        this.velocityMultiplier = 0.8f;
        this.gravityStrength = .1f;

        this.maxAge = Math.max(1, 100 + (this.random.nextInt(40) - 20));
        this.collidesWithWorld = true;

        this.scale *= (1.1f + this.random.nextFloat()/2);
        this.setSprite(sprite);
        this.sprite = sprite;
    }

    @Override
    public void tick() {
        super.tick();
        this.gravityStrength *= 1.07f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        //The factory used in a particle's registry
        private final SpriteProvider spriteProvider;
        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velX, double velY, double velZ) {
            return new PlagueParticle(clientWorld, x, y, z, velX, velY, velZ, this.spriteProvider);
        }
    }
}
