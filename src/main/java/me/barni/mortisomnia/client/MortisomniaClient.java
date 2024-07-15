package me.barni.mortisomnia.client;

import me.barni.mortisomnia.datagen.MortisomniaBlocks;
import me.barni.mortisomnia.datagen.MortisomniaParticles;
import net.fabricmc.api.ClientModInitializer;

public class MortisomniaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MortisomniaBlocks.clientRegisterBlocks();
        MortisomniaParticles.clientRegisterParticles();
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
