package me.barni.mortisomnia.client;

import me.barni.mortisomnia.datagen.MortisomniaBlocks;
import me.barni.mortisomnia.datagen.MortisomniaParticles;
import me.barni.mortisomnia.mixin.GameRendererSetPostProcessorMixin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class MortisomniaClient implements ClientModInitializer {

    private float time = 0;

    @Override
    public void onInitializeClient() {
        MortisomniaBlocks.clientRegisterBlocks();
        MortisomniaParticles.clientRegisterParticles();
        WorldRenderEvents.END.register(context -> {
            var processor = context.gameRenderer().getPostProcessor();
            var world = context.world();
            if (processor == null)
                ((GameRendererSetPostProcessorMixin) context.gameRenderer()).invokeLoadPostProcessor(
                        Identifier.ofVanilla("shaders/post/plague.json")); //TODO move to mod's namespace
            if (processor != null && world != null) {
                processor.setUniforms("time", this.time);
                this.time += context.tickCounter().getTickDelta(true)*0.01f;
                processor.setUniforms("Height", (float) MinecraftClient.getInstance().player.getY());
            }
                //Identifier.of(Mortisomnia.MOD_ID, "shaders/post/plague.json"));
        });


        /*
        ClientPlayNetworking.registerGlobalReceiver(Mortisomnia.CAMSHAKE_PACKET, (client, handler, buf, responseSender) -> {

            float amplitude = buf.readFloat();
            float attenuation = buf.readFloat();
            float damping = buf.readFloat();
            client.execute(() -> {
                ClientCamShakeManager.getInstance().registerShake(amplitude,attenuation,damping);
            });
        });
         */
    }
}
