package me.barni.mortisomnia.datagen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaSounds {

    public static SoundEvent SOUL_SFX;
    public static SoundEvent USE_CAPTURED_LIGHT;
    public static SoundEvent TORCHBREAK_DRONE;
    public static SoundEvent SPOOK_GENERAL;

    public static void registerSounds() {
        SOUL_SFX = Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "soul1"), SoundEvent.of(Identifier.of(MOD_ID, "soul1")));
        USE_CAPTURED_LIGHT = Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "use_captured_light"), SoundEvent.of(Identifier.of(MOD_ID, "use_captured_light")));
        TORCHBREAK_DRONE = Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "torchbreak_drone"), SoundEvent.of(Identifier.of(MOD_ID, "torchbreak_drone")));
        SPOOK_GENERAL = Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "spook_general"), SoundEvent.of(Identifier.of(MOD_ID, "spook_general")));
    }

}
