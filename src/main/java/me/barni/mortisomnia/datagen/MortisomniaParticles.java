package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.client.particles.EctoplasmParticle;
import me.barni.mortisomnia.client.particles.MagicParticle;
import me.barni.mortisomnia.client.particles.PlagueParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaParticles {

    public static final SimpleParticleType ECTOPLASM = FabricParticleTypes.simple();
    public static final SimpleParticleType PLAGUE = FabricParticleTypes.simple();
    public static final SimpleParticleType MAGIC = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "ectoplasm"), ECTOPLASM);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "plague"), PLAGUE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "magic"), MAGIC);
    }

    public static void clientRegisterParticles() {
        ParticleFactoryRegistry.getInstance().register(ECTOPLASM, EctoplasmParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(PLAGUE, PlagueParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(MAGIC, MagicParticle.Factory::new);
   }
}
