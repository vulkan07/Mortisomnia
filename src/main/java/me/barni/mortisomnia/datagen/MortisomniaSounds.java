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
    public static SoundEvent GHOST;
    public static SoundEvent BULB;
    public static SoundEvent CAVE_MONSTER;

    private static SoundEvent register(String name) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, name), SoundEvent.of(Identifier.of(MOD_ID, name)));
    }

    public static void registerSounds() {
        SOUL_SFX = register("soul1");
        USE_CAPTURED_LIGHT = register("use_captured_light");
        TORCHBREAK_DRONE = register("torchbreak_drone");
        SPOOK_GENERAL = register("spook_general");
        GHOST = register("ghost");
        BULB = register("bulb");
        CAVE_MONSTER = register("cave_monster");
    }

}
