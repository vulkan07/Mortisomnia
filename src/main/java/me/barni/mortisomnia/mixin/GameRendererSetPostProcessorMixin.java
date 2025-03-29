package me.barni.mortisomnia.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererSetPostProcessorMixin {
    @Invoker("loadPostProcessor")
    void invokeLoadPostProcessor(Identifier id);
}
