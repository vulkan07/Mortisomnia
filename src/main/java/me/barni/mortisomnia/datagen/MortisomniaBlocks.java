package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.block.UnlitTorchBlock;
import me.barni.mortisomnia.block.UnlitWallTorchBlock;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.jetbrains.annotations.Nullable;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaBlocks {

    public static final Block DEAD_LEAVES = registerBlock("dead_leaves", ItemGroups.NATURAL, new LeavesBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS).strength(0.05F).breakInstantly().nonOpaque().suffocates(Blocks::never).blockVision(Blocks::never).pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never)));
    public static final Block DEAD_LOG = registerBlock("dead_log", ItemGroups.NATURAL, new PillarBlock(AbstractBlock.Settings.create().strength(0.4F).sounds(BlockSoundGroup.WOOD)));

    public static final Block SAPPHIRE_ORE = registerBlock("sapphire_ore", ItemGroups.NATURAL, new Block(AbstractBlock.Settings.create().strength(3, 6).requiresTool()));
    public static final RegistryKey<PlacedFeature> SAPPHIRE_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(MOD_ID,"sapphire_ore"));

    public static final Block UNLIT_TORCH = registerBlock("unlit_torch", null, new UnlitTorchBlock(AbstractBlock.Settings.create().noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block UNLIT_WALL_TORCH = registerBlock("unlit_wall_torch", null, new UnlitWallTorchBlock(AbstractBlock.Settings.create().noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block UNLIT_LANTERN = registerBlock("unlit_lantern", ItemGroups.FUNCTIONAL, new LanternBlock(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).solid().requiresTool().strength(3.5f).sounds(BlockSoundGroup.LANTERN).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)));

    public static void registerBlocks() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, SAPPHIRE_ORE_PLACED_KEY);
    }

    private static Block registerBlock(String path, @Nullable RegistryKey<ItemGroup> group, Block block) {
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, path), block);
        if (group != null) { // Only register item if an inventory itemgroup is given
            Registry.register(Registries.ITEM, Identifier.of(MOD_ID, path), new BlockItem(block, new Item.Settings()));
            ItemGroupEvents.modifyEntriesEvent(group).register(content -> content.add(block));
        }
        return block;
    }


    public static void clientRegisterBlocks() {
        // makes these blocks cutout (translucent) because they are not complete cubes
        BlockRenderLayerMap.INSTANCE.putBlock(MortisomniaBlocks.UNLIT_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MortisomniaBlocks.UNLIT_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MortisomniaBlocks.UNLIT_LANTERN, RenderLayer.getCutout());
    }
}
