package me.barni.mortisomnia.client.entities;

import me.barni.mortisomnia.entity.WeepingAngelEntity;
import me.barni.mortisomnia.entity.WeepingAngelModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WeepingAngelRenderer extends LivingEntityRenderer<WeepingAngelEntity, WeepingAngelModel<WeepingAngelEntity>> {
    public WeepingAngelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,new WeepingAngelModel<>(ctx.getPart(MortisomniaClientEntities.WEEPING_ANGEL_LAYER)),.4f);
    }

    @Override
    public Identifier getTexture(WeepingAngelEntity entity) { return entity.getTexture(); }

    @Override
    protected boolean hasLabel(WeepingAngelEntity entity) { return entity.isCustomNameVisible(); }
}