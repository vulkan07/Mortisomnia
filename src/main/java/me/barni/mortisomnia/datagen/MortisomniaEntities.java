package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.entity.GazerEntity;
import me.barni.mortisomnia.entity.PlagueDoctorEntity;
import me.barni.mortisomnia.entity.WeepingAngelEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaEntities {
    public static final EntityType<WeepingAngelEntity> WEEPING_ANGEL = Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "weeping_angel"), EntityType.Builder.create(WeepingAngelEntity::new, SpawnGroup.MISC).dimensions(0.75f, 2.2f).build());
    public static final EntityType<GazerEntity> GAZER = Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "gazer"), EntityType.Builder.create(GazerEntity::new, SpawnGroup.MISC).dimensions(0.6f, 1.9f).build());
    public static final EntityType<PlagueDoctorEntity> PLAGUE_DOCTOR = Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "plague_doctor"), EntityType.Builder.create(PlagueDoctorEntity::new, SpawnGroup.MISC).dimensions(0.6f, 1.9f).build());


    public static void registerEntities() {
        FabricDefaultAttributeRegistry.register(WEEPING_ANGEL, WeepingAngelEntity.createWeepingAngelAttributes());
        FabricDefaultAttributeRegistry.register(GAZER, GazerEntity.createGazerAttributes());
        FabricDefaultAttributeRegistry.register(PLAGUE_DOCTOR, PlagueDoctorEntity.createLivingAttributes());
    }
}
