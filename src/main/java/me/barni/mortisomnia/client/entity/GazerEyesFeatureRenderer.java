package me.barni.mortisomnia.client.entity;

import me.barni.mortisomnia.Mortisomnia;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GazerEyesFeatureRenderer extends EyesFeatureRenderer<GazerRenderState, GazerModel<GazerRenderState>> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.of(Mortisomnia.MOD_ID, "textures/entity/gazer_glow.png"));

    public GazerEyesFeatureRenderer(FeatureRendererContext<GazerRenderState, GazerModel<GazerRenderState>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}

