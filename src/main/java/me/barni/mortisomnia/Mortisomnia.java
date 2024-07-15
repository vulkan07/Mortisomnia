package me.barni.mortisomnia;

import me.barni.mortisomnia.datagen.*;
import me.barni.mortisomnia.events.MortisomniaEventHandlers;
import me.barni.mortisomnia.paractivity.ParaController;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;


public class Mortisomnia implements ModInitializer {
    public static final String MOD_ID = "mortisomnia";
    public static final Logger LOGGER = LoggerFactory.getLogger("Mortisomnia");
    public static final Random RANDOM = new Random();

    public static final Identifier CAMSHAKE_PACKET = Identifier.of(MOD_ID, "cam_shake");

    @Override
    public void onInitialize() {
        MortisomniaItems.registerItems();
        MortisomniaBlocks.registerBlocks();
        MortisomniaEntities.registerEntities();
        MortisomniaSounds.registerSounds();
        MortisomniaParticles.registerParticles();
        MortisomniaCommands.registerCommands();
        MortisomniaEventHandlers.subscribeToGameEvents();

        ParaController.getInstance().init();

        LOGGER.info("Mortisomnia initialized");
    }
}
