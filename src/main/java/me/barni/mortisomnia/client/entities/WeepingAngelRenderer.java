package me.barni.mortisomnia.client.entities;

import me.barni.mortisomnia.entity.WeepingAngelEntity;
import me.barni.mortisomnia.entity.WeepingAngelModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WeepingAngelRenderer extends MobEntityRenderer<WeepingAngelEntity, WeepingAngelModel<WeepingAngelEntity>> {
    public WeepingAngelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,new WeepingAngelModel<>(ctx.getPart(MortisomniaClientEntities.WEEPING_ANGEL_LAYER)),.4f);
    }

    @Override
    public Identifier getTexture(WeepingAngelEntity entity) {
        return entity.getTexture();
    }
}