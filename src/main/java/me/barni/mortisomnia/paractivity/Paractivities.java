package me.barni.mortisomnia.paractivity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.paractivity.activities.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class Paractivities {

    private static final ArrayList<String> activityNames = new ArrayList<>();
    public static ArrayList<String> getActivityNames() { return activityNames; }


    public static final RegistryKey<Registry<ParactivityFactory>> PARACTIVITY = RegistryKey.ofRegistry(Identifier.of(Mortisomnia.MOD_ID,"paractivity"));
    public static final Registry<ParactivityFactory> REGISTRY = FabricRegistryBuilder.createSimple(PARACTIVITY).buildAndRegister();

    public static final ParactivityFactory CAPTURED_LIGHT = Paractivities.register(CapturedLightParactivity::create, CapturedLightParactivity.id);
    public static final ParactivityFactory SPOOK_SOUND = Paractivities.register(SpookSoundParactivity::create, SpookSoundParactivity.id);
    public static final ParactivityFactory TORCH_OFF = Paractivities.register(TorchOffParactivity::create, TorchOffParactivity.id);
    public static final ParactivityFactory LIGHT_EXTINGUISH = Paractivities.register(LightExtinguishParactivity::create, LightExtinguishParactivity.id);
    public static final ParactivityFactory LIGHT_FLICKER = Paractivities.register(LightFlickerParactivity::create, LightFlickerParactivity.id);
    public static final ParactivityFactory WEEPING_ANGEL = Paractivities.register(WeepingAngelParactivity::create, WeepingAngelParactivity.id);
    public static final ParactivityFactory DOOR = Paractivities.register(DoorParactivity::create, DoorParactivity.id);
    public static final ParactivityFactory DOOR_TOGGLE = Paractivities.register(DoorToggleParactivity::create, DoorToggleParactivity.id);
    public static final ParactivityFactory SCARECROW = Paractivities.register(ScareCrowParactivity::create, ScareCrowParactivity.id);
    public static final ParactivityFactory FERTILIZER = Paractivities.register(FertilizerCapsuleParactivity::create, FertilizerCapsuleParactivity.id);
    public static final ParactivityFactory KILL_FOLIAGE = Paractivities.register(KillFoliageParactivity::create, KillFoliageParactivity.id);
    public static final ParactivityFactory GAZER = Paractivities.register(GazerParactivity::create, GazerParactivity.id);
    public static final ParactivityFactory CREEPER = Paractivities.register(CreeperParactivity::create, CreeperParactivity.id);
    public static final ParactivityFactory CAVE_SPOOK = Paractivities.register(CaveSpookParactivity::create, CaveSpookParactivity.id);

    public static ParactivityFactory register(ParactivityFactory entry, String id) {
        Mortisomnia.LOGGER.info("Registering Paractivity: {}", id);
        activityNames.add(id);
        return Registry.register(REGISTRY,Identifier.of(Mortisomnia.MOD_ID,id),entry);
    }
    public static void init() {
    }
    private Paractivities() {}
}
