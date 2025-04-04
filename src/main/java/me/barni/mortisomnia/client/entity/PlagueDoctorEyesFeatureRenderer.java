package me.barni.mortisomnia.client.entity;

import me.barni.mortisomnia.Mortisomnia;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PlagueDoctorEyesFeatureRenderer extends EyesFeatureRenderer<PlagueDoctorRenderState, PlagueDoctorModel<PlagueDoctorRenderState>> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.of(Mortisomnia.MOD_ID,"textures/entity/plague_doctor_eyes.png"));


    public PlagueDoctorEyesFeatureRenderer(FeatureRendererContext<PlagueDoctorRenderState, PlagueDoctorModel<PlagueDoctorRenderState>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() { return SKIN; }
}

