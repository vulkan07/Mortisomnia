package me.barni.mortisomnia.datagen;

import me.barni.mortisomnia.block.UnlitTorchBlock;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static me.barni.mortisomnia.Mortisomnia.MOD_ID;

public class MortisomniaBlocks {

    public static final Block DEAD_LEAVES = registerBlock("dead_leaves", ItemGroups.NATURAL, (settings) -> new UntintedParticleLeavesBlock(0.02F, ParticleTypes.PALE_OAK_LEAVES, settings), AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).strength(0.2F).sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).burnable().pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never));
    public static final Block DEAD_LOG = registerBlock("dead_log", ItemGroups.NATURAL, PillarBlock::new, Blocks.createLogSettings(MapColor.BROWN, MapColor.BROWN, BlockSoundGroup.WOOD));

    public static final Block SAPPHIRE_ORE = registerBlock("sapphire_ore", ItemGroups.NATURAL, settings -> new ExperienceDroppingBlock(UniformIntProvider.create(1,3),settings), AbstractBlock.Settings.copy(Blocks.IRON_ORE));
    public static final RegistryKey<PlacedFeature> SAPPHIRE_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(MOD_ID,"sapphire_ore"));

    public static final Block UNLIT_TORCH = registerBlock("unlit_torch", null, UnlitTorchBlock::new, AbstractBlock.Settings.create().noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY));
    public static final Block UNLIT_WALL_TORCH = registerBlock("unlit_wall_torch", null, (settings) -> new WallTorchBlock(ParticleTypes.FLAME, settings), AbstractBlock.Settings.create().noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY));
    public static final Block UNLIT_LANTERN = registerBlock("unlit_lantern", ItemGroups.FUNCTIONAL, LanternBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).solid().strength(3.5F).sounds(BlockSoundGroup.LANTERN).nonOpaque().pistonBehavior(PistonBehavior.DESTROY));

    public static void registerBlocks() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, SAPPHIRE_ORE_PLACED_KEY);
    }

    private static Block registerBlock(String path, @Nullable RegistryKey<ItemGroup> group, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Identifier id = Identifier.of(MOD_ID,path);
        Block block = factory.apply(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));


        if (group != null) { // Only register item if an inventory item group is given
            var itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, path));
            Registry.register(Registries.ITEM, itemKey, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
            ItemGroupEvents.modifyEntriesEvent(group).register((itemGroup) ->  itemGroup.add(block.asItem()) );
        }
        return Registry.register(Registries.BLOCK, id, block);
    }


    public static void clientRegisterBlocks() {
        // makes these blocks cutout (translucent) because they are not complete cubes
        BlockRenderLayerMap.INSTANCE.putBlock(MortisomniaBlocks.UNLIT_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MortisomniaBlocks.UNLIT_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MortisomniaBlocks.UNLIT_LANTERN, RenderLayer.getCutout());
    }
}
