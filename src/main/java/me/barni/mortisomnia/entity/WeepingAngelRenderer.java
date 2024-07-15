package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.datagen.MortisomniaEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class WeepingAngelRenderer extends MobEntityRenderer<WeepingAngelEntity, WeepingAngelModel<WeepingAngelEntity>> {
    public WeepingAngelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,new WeepingAngelModel<>(ctx.getPart(MortisomniaEntities.WEEPING_ANGEL_LAYER)),.4f);
    }

    @Override
    public Identifier getTexture(WeepingAngelEntity entity) {
        return Identifier.of(Mortisomnia.MOD_ID, "textures/entity/weeping_angel.png");
    }
}