package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.item.CapturedLightItem;
import me.barni.mortisomnia.item.EctoFragmentItem;
import me.barni.mortisomnia.item.PurifiedEctoFragmentItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaItems {
    public static final Item CAPTURED_LIGHT = new CapturedLightItem(new Item.Settings().maxCount(1));
    public static final Item ECTO_FRAGMENT = new EctoFragmentItem(new Item.Settings().maxCount(16));
    public static final Item PURIFIED_ECTO_FRAGMENT = new PurifiedEctoFragmentItem(new Item.Settings().maxCount(16));
    public static final Item SAPPHIRE = new Item(new Item.Settings());
    /*
    public static final Item DEAD_LEAVES = new Item(new Item.Settings());
    public static final Item DEAD_LOG = new Item(new Item.Settings());
    public static final Item UNLIT_LANTERN = new Item(new Item.Settings());
     */

    public static void registerItems() {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "captured_light"), CAPTURED_LIGHT);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(CAPTURED_LIGHT));

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "ecto_fragment"), ECTO_FRAGMENT);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(ECTO_FRAGMENT));

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "purified_ecto_fragment"), PURIFIED_ECTO_FRAGMENT);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(PURIFIED_ECTO_FRAGMENT));

        //Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "sapphire_ore"), new BlockItem(MortisomniaBlocks.SAPPHIRE_ORE, new Item.Settings()));
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> content.add(MortisomniaBlocks.SAPPHIRE_ORE));

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "sapphire"), SAPPHIRE);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(SAPPHIRE));
       /*

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "unlit_lantern"), UNLIT_LANTERN);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> content.add(UNLIT_LANTERN));

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "dead_leaves"), DEAD_LEAVES);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> content.add(DEAD_LEAVES));

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "dead_log"), DEAD_LOG);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> content.add(DEAD_LOG));
*/
    }
}
