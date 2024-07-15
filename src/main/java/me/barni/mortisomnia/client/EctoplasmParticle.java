package me.barni.mortisomnia.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class EctoplasmParticle extends SpriteBillboardParticle {
    private final SpriteProvider sprite;
    public EctoplasmParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteProvider sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = motionX * 0.1;
        this.velocityY = 0.01f;
        this.velocityZ = motionZ * 0.1;

        this.maxAge = Math.max(1, 100 + (this.random.nextInt(40) - 20));
        this.collidesWithWorld = false;

        this.scale *= .8f;
        this.setSpriteForAge(sprite);
        this.sprite = sprite;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(sprite);
        if (this.age > this.maxAge-25)
            this.scale *= 0.80f;
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
            return new EctoplasmParticle(clientWorld, x, y, z, velX, velY, velZ, this.spriteProvider);
        }
    }
}
