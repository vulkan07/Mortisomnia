package me.barni.mortisomnia.client.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.entity.GazerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GazerRenderer extends LivingEntityRenderer<GazerEntity, GazerRenderState, GazerModel<GazerRenderState>> {
    private static final Identifier TEXTURE = Identifier.of(Mortisomnia.MOD_ID,"textures/entity/gazer.png");
    public GazerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,new GazerModel<>(ctx.getPart(MortisomniaClientEntities.GAZER_LAYER)),.5f);
//        this.addFeature(new GazerEyesFeatureRenderer(this)); TODO
    }

    @Override
    public GazerRenderState createRenderState() {
        return new GazerRenderState();
    }

    @Override
    public Identifier getTexture(GazerRenderState state) {
        return TEXTURE;
    }
}