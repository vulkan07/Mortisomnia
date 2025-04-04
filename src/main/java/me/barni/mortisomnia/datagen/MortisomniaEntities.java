package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.entity.GazerEntity;
import me.barni.mortisomnia.entity.PlagueDoctorEntity;
import me.barni.mortisomnia.entity.WeepingAngelEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaEntities {
    public static final EntityType<WeepingAngelEntity> WEEPING_ANGEL = register("weeping_angel", EntityType.Builder.create(WeepingAngelEntity::new, SpawnGroup.MISC).dimensions(0.75f, 2.2f));
    public static final EntityType<GazerEntity> GAZER = register("gazer", EntityType.Builder.create(GazerEntity::new, SpawnGroup.MISC).dimensions(0.6f, 1.9f));
    public static final EntityType<PlagueDoctorEntity> PLAGUE_DOCTOR = register("plague_doctor", EntityType.Builder.create(PlagueDoctorEntity::new, SpawnGroup.MISC).dimensions(0.6f, 1.9f));

    //CHICKEN = register("chicken", EntityType.Builder.create(ChickenEntity::new, SpawnGroup.CREATURE).dimensions(0.4F, 0.7F).eyeHeight(0.644F).passengerAttachments(new Vec3d((double)0.0F, 0.7, -0.1)).maxTrackingRange(10));

    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static void registerEntities() {
        FabricDefaultAttributeRegistry.register(WEEPING_ANGEL, WeepingAngelEntity.createWeepingAngelAttributes());
        FabricDefaultAttributeRegistry.register(GAZER, GazerEntity.createGazerAttributes());
        FabricDefaultAttributeRegistry.register(PLAGUE_DOCTOR, PlagueDoctorEntity.createLivingAttributes());
    }
}
