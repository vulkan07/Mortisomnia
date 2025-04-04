package me.barni.mortisomnia.client.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.entity.WeepingAngelEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
 public class WeepingAngelRenderer extends LivingEntityRenderer<WeepingAngelEntity, WeepingAngelRenderState, WeepingAngelModel> {
    private static final Identifier TEXTURE = Identifier.of(Mortisomnia.MOD_ID,"textures/entity/weeping_angel.png"); //TODO
    public WeepingAngelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,new WeepingAngelModel(ctx.getPart(MortisomniaClientEntities.WEEPING_ANGEL_LAYER)),.4f);
    }

    @Override
    public WeepingAngelRenderState createRenderState() {
        return new WeepingAngelRenderState();
    }

    @Override
    public Identifier getTexture(WeepingAngelRenderState state) {
    return TEXTURE;
    }
}