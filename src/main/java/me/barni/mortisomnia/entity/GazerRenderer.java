package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.datagen.MortisomniaEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class GazerRenderer extends MobEntityRenderer<GazerEntity, GazerModel<GazerEntity>> {
    public GazerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,new GazerModel<>(ctx.getPart(MortisomniaEntities.GAZER_LAYER)),.5f);
        this.addFeature(new GazerEyesFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(GazerEntity entity) {
        return Identifier.of(Mortisomnia.MOD_ID, "textures/entity/gazer.png");
    }
    
}