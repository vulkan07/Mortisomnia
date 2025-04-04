package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.item.CapturedLightItem;
import me.barni.mortisomnia.item.EctoFragmentItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaItems {
    public static final Item CAPTURED_LIGHT = register("captured_light", CapturedLightItem::new, new Item.Settings().maxCount(1));
    public static final Item ECTO_FRAGMENT = register("ecto_fragment", EctoFragmentItem::new, new Item.Settings().maxCount(16));
    public static final Item PURIFIED_ECTO_FRAGMENT = register("purified_ecto_fragment", EctoFragmentItem::new, new Item.Settings().maxCount(16));
    public static final Item SAPPHIRE = register("sapphire", Item::new, new Item.Settings());

    public static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, path));
        return Items.register(registryKey, factory, settings);
    }

    public static void registerItems() {

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(CAPTURED_LIGHT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(ECTO_FRAGMENT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(PURIFIED_ECTO_FRAGMENT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(SAPPHIRE));

    }
}
