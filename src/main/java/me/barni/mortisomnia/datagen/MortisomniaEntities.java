package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.entity.*;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaEntities {
    public static final EntityModelLayer WEEPING_ANGEL_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "weeping_angel"), "main");
    public static final EntityType<WeepingAngelEntity> WEEPING_ANGEL = Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "weeping_angel"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, WeepingAngelEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2.2f)).build());

    public static final EntityModelLayer GAZER_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "gazer"), "main");
    public static final EntityType<GazerEntity> GAZER = Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "gazer"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, GazerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.9f)).build());

    public static void registerEntities() {
        EntityRendererRegistry.register(WEEPING_ANGEL, WeepingAngelRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(WEEPING_ANGEL_LAYER, WeepingAngelModel::getTexturedModelData);
        FabricDefaultAttributeRegistry.register(WEEPING_ANGEL, WeepingAngelEntity.createWeepingAngelAttributes());

        EntityRendererRegistry.register(GAZER, GazerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GAZER_LAYER, GazerModel::getTexturedModelData);
        FabricDefaultAttributeRegistry.register(GAZER, GazerEntity.createGazerAttributes());
    }
}
