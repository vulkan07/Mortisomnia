package me.barni.mortisomnia.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class MortisomniaEventHandlers {

    public static void subscribeToGameEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register(OnEntityDeathHandler::onEntityAfterDeath);
        UseBlockCallback.EVENT.register(OnUseBlockHandler::onUseBlock);
    }
}
