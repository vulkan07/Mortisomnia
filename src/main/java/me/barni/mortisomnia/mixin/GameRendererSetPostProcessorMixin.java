package me.barni.mortisomnia.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public interface GameRendererSetPostProcessorMixin {
    /*
    @Invoker("loadPostProcessor")
    void invokeLoadPostProcessor(Identifier id);
    */
}
