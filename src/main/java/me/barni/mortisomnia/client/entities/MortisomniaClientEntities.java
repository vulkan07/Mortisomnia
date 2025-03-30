package me.barni.mortisomnia.client.entities;

import me.barni.mortisomnia.entity.GazerModel;
import me.barni.mortisomnia.entity.PlagueDoctorModel;
import me.barni.mortisomnia.entity.WeepingAngelModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;
import static me.barni.mortisomnia.datagen.MortisomniaEntities.*;

@Environment(value=EnvType.CLIENT)
public class MortisomniaClientEntities {
    public static final EntityModelLayer WEEPING_ANGEL_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "weeping_angel"), "main");
    public static final EntityModelLayer GAZER_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "gazer"), "main");
    public static final EntityModelLayer PLAGUE_DOCTOR_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "plague_doctor"), "main");

    public static void registerClientEntities() {
        EntityRendererRegistry.register(WEEPING_ANGEL, WeepingAngelRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(WEEPING_ANGEL_LAYER, WeepingAngelModel::getTexturedModelData);

        EntityRendererRegistry.register(GAZER, GazerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GAZER_LAYER, GazerModel::getTexturedModelData);

        EntityRendererRegistry.register(PLAGUE_DOCTOR, PlagueDoctorRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PLAGUE_DOCTOR_LAYER, PlagueDoctorModel::getTexturedModelData);
    }
}
