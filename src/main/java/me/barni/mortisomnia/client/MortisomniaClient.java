package me.barni.mortisomnia.client;

import me.barni.mortisomnia.client.entity.MortisomniaClientEntities;
import me.barni.mortisomnia.datagen.MortisomniaBlocks;
import me.barni.mortisomnia.datagen.MortisomniaParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class MortisomniaClient implements ClientModInitializer {

    private float time = 0;

    @Override
    public void onInitializeClient() {
        MortisomniaBlocks.clientRegisterBlocks();
        MortisomniaParticles.clientRegisterParticles();
        MortisomniaClientEntities.registerClientEntities();
        /* TODO
        WorldRenderEvents.END.register(context -> {
            var processor = context.gameRenderer().getPostProcessorId();

            var world = context.world();
            if (processor == null)
                ((GameRendererSetPostProcessorMixin) context.gameRenderer()).invokeLoadPostProcessor(
                        Identifier.ofVanilla("shaders/post/plague.json")); //TODO move to mod's namespace
            if (processor != null && world != null) {
                processor.setUniforms("time", this.time);
                this.time += context.tickCounter().getTickProgress(true)*0.01f;
                processor.setUniforms("Height", (float) MinecraftClient.getInstance().player.getY());
            }
                //Identifier.of(Mortisomnia.MOD_ID, "shaders/post/plague.json"));
        });

         */


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
