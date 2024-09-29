package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PlagueDoctorEyesFeatureRenderer<T extends LivingEntity>
            extends EyesFeatureRenderer<T, PlagueDoctorModel<T>> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.of(Mortisomnia.MOD_ID, "textures/entity/plague_doctor_eyes.png"));

    public PlagueDoctorEyesFeatureRenderer(FeatureRendererContext<T, PlagueDoctorModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}

