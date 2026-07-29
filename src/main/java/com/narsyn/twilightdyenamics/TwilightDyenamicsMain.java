package com.narsyn.twilightdyenamics;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.narsyn.twilightdyenamics.block.DyenamicsOminousCandleBlock;
import com.narsyn.twilightdyenamics.block.DyenamicsSkullCandleBlock;
import com.narsyn.twilightdyenamics.block.DyenamicsWallSkullCandleBlock;
import com.narsyn.twilightdyenamics.components.DyenamicsSkullCandles;
import com.narsyn.twilightdyenamics.dispenser.DyenamicsCandleDispenseBehavior;
import com.narsyn.twilightdyenamics.block.entity.DyenamicsOminousCandleBlockEntity;
import com.narsyn.twilightdyenamics.block.entity.DyenamicsSkullCandleBlockEntity;
import com.narsyn.twilightdyenamics.events.DyenamicsEntityEvents;
import com.narsyn.twilightdyenamics.item.DyenamicsSkullCandleItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import twilightforest.block.OminousCandleBlock;
import cy.jdkdigital.dyenamics.core.init.BlockInit;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Mod(TwilightDyenamicsMain.MODID)
public class TwilightDyenamicsMain {
    public static final String MODID = "twilight_dyenamics";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static Supplier<CreativeModeTab> TWILIGHT_DYENAMICS_TAB;
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    private static final Map<String,DeferredBlock<OminousCandleBlock>> OMINOUS_CANDLES = new HashMap<>();
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<DyenamicsOminousCandleBlockEntity>> CANDLE_ENTITY;
    public static DeferredHolder<DataComponentType<?>, DataComponentType<DyenamicsSkullCandles>> SKULL_CANDLES;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<DyenamicsSkullCandleBlockEntity>> SKULL_CANDLE_ENTITY;
    public static DeferredBlock<DyenamicsSkullCandleBlock> ZOMBIE_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsWallSkullCandleBlock> ZOMBIE_WALL_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsSkullCandleBlock> SKELETON_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsWallSkullCandleBlock> SKELETON_WALL_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsSkullCandleBlock> WITHER_SKELE_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsWallSkullCandleBlock> WITHER_SKELE_WALL_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsSkullCandleBlock> CREEPER_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsWallSkullCandleBlock> CREEPER_WALL_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsSkullCandleBlock> PLAYER_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsWallSkullCandleBlock> PLAYER_WALL_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsSkullCandleBlock> PIGLIN_SKULL_CANDLE;
    public static DeferredBlock<DyenamicsWallSkullCandleBlock> PIGLIN_WALL_SKULL_CANDLE;
    public static DeferredItem<DyenamicsSkullCandleItem> ZOMBIE_SKULL_CANDLE_ITEM;
    public static DeferredItem<DyenamicsSkullCandleItem> SKELETON_SKULL_CANDLE_ITEM;
    public static DeferredItem<DyenamicsSkullCandleItem> WITHER_SKELE_SKULL_CANDLE_ITEM;
    public static DeferredItem<DyenamicsSkullCandleItem> CREEPER_SKULL_CANDLE_ITEM;
    public static DeferredItem<DyenamicsSkullCandleItem> PLAYER_SKULL_CANDLE_ITEM;
    public static DeferredItem<DyenamicsSkullCandleItem> PIGLIN_SKULL_CANDLE_ITEM;

    private static ItemStack create_tab_stack(){
        ItemStack stack=new ItemStack(PLAYER_SKULL_CANDLE_ITEM.get());
        stack.set(SKULL_CANDLES,new DyenamicsSkullCandles("lavender",1,0));
        PropertyMap properties = new PropertyMap();
        properties.put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTc3MTQyNjM3NTY0MywKICAicHJvZmlsZUlkIiA6ICJkYmQyNjczODcxOGI0ZWI1OTI5MTMyMDU4YjY4MmJiMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOYXJyaWtfU3ludGhmb3giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJlZTFkNWNiY2NjZDY2Yjg2MWQxY2I5NjliZTBmYjE0ZjUxMTEyYmQ3NTI4YmQyNmE5YzA0YzllZTQzMTcxIgogICAgfQogIH0KfQ=="));
        stack.set(DataComponents.PROFILE,new ResolvableProfile(Optional.of(""), Optional.of(UUID.randomUUID()),properties));
        return stack;
    }

    public TwilightDyenamicsMain(IEventBus modEventBus, Dist dist) {
        modEventBus.addListener(this::commonSetup);
        for (int i = 0; i < DyenamicDyeColor.dyenamicValues().length; i++) {
            DyenamicDyeColor color = DyenamicDyeColor.dyenamicValues()[i];
            DeferredBlock<OminousCandleBlock> block=BLOCKS.register(color.getSerializedName() + "_ominous_candle",
                    () -> new DyenamicsOminousCandleBlock(
                            BlockInit.DYED_BLOCKS.get(color.getSerializedName()).get("candle").get(),
                            BlockBehaviour.Properties.of()
                                    .mapColor(BlockInit.DYED_BLOCKS.get(color.getSerializedName()).get("candle").get().defaultMapColor())
                                    .noOcclusion()
                                    .strength(0.1F)
                                    .sound(SoundType.CANDLE)
                                    .lightLevel(state -> Math.max(color.getLightValue(),2 * state.getValue(OminousCandleBlock.CANDLES)))
                                    .pushReaction(PushReaction.DESTROY)
                    )
            );
            OMINOUS_CANDLES.put(color.getSerializedName(),block);
        }
        CANDLE_ENTITY=BLOCK_ENTITIES.register("ominous_candle", () ->
                BlockEntityType.Builder.of(DyenamicsOminousCandleBlockEntity::new,OMINOUS_CANDLES.values().stream()
                                .map(DeferredBlock::get)
                                .toArray(Block[]::new)).build(null));
        SKULL_CANDLES=COMPONENTS.register("skull_candles", () -> DataComponentType.<DyenamicsSkullCandles>builder().persistent(DyenamicsSkullCandles.CODEC).networkSynchronized(DyenamicsSkullCandles.STREAM_CODEC).build());
        ZOMBIE_SKULL_CANDLE = BLOCKS.register("zombie_skull_candle", () -> new DyenamicsSkullCandleBlock(SkullBlock.Types.ZOMBIE, BlockBehaviour.Properties.ofFullCopy(Blocks.ZOMBIE_HEAD)));
        ZOMBIE_WALL_SKULL_CANDLE = BLOCKS.register("zombie_wall_skull_candle", () -> new DyenamicsWallSkullCandleBlock(SkullBlock.Types.ZOMBIE, BlockBehaviour.Properties.ofFullCopy(Blocks.ZOMBIE_WALL_HEAD)));
        SKELETON_SKULL_CANDLE = BLOCKS.register("skeleton_skull_candle", () -> new DyenamicsSkullCandleBlock(SkullBlock.Types.SKELETON, BlockBehaviour.Properties.ofFullCopy(Blocks.SKELETON_SKULL)));
        SKELETON_WALL_SKULL_CANDLE = BLOCKS.register("skeleton_wall_skull_candle", () -> new DyenamicsWallSkullCandleBlock(SkullBlock.Types.SKELETON, BlockBehaviour.Properties.ofFullCopy(Blocks.SKELETON_WALL_SKULL)));
        WITHER_SKELE_SKULL_CANDLE = BLOCKS.register("wither_skeleton_skull_candle", () -> new DyenamicsSkullCandleBlock(SkullBlock.Types.WITHER_SKELETON, BlockBehaviour.Properties.ofFullCopy(Blocks.WITHER_SKELETON_SKULL)));
        WITHER_SKELE_WALL_SKULL_CANDLE = BLOCKS.register("wither_skeleton_wall_skull_candle", () -> new DyenamicsWallSkullCandleBlock(SkullBlock.Types.WITHER_SKELETON, BlockBehaviour.Properties.ofFullCopy(Blocks.WITHER_SKELETON_WALL_SKULL)));
        CREEPER_SKULL_CANDLE = BLOCKS.register("creeper_skull_candle", () -> new DyenamicsSkullCandleBlock(SkullBlock.Types.CREEPER, BlockBehaviour.Properties.ofFullCopy(Blocks.CREEPER_HEAD)));
        CREEPER_WALL_SKULL_CANDLE = BLOCKS.register("creeper_wall_skull_candle", () -> new DyenamicsWallSkullCandleBlock(SkullBlock.Types.CREEPER, BlockBehaviour.Properties.ofFullCopy(Blocks.CREEPER_WALL_HEAD)));
        PLAYER_SKULL_CANDLE = BLOCKS.register("player_skull_candle", () -> new DyenamicsSkullCandleBlock(SkullBlock.Types.PLAYER, BlockBehaviour.Properties.ofFullCopy(Blocks.PLAYER_HEAD)));
        PLAYER_WALL_SKULL_CANDLE = BLOCKS.register("player_wall_skull_candle", () -> new DyenamicsWallSkullCandleBlock(SkullBlock.Types.PLAYER, BlockBehaviour.Properties.ofFullCopy(Blocks.PLAYER_WALL_HEAD)));
        PIGLIN_SKULL_CANDLE = BLOCKS.register("piglin_skull_candle", () -> new DyenamicsSkullCandleBlock(SkullBlock.Types.PIGLIN, BlockBehaviour.Properties.ofFullCopy(Blocks.PIGLIN_HEAD)));
        PIGLIN_WALL_SKULL_CANDLE= BLOCKS.register("piglin_wall_skull_candle", () -> new DyenamicsWallSkullCandleBlock(SkullBlock.Types.PIGLIN, BlockBehaviour.Properties.ofFullCopy(Blocks.PIGLIN_WALL_HEAD)));

        SKULL_CANDLE_ENTITY = BLOCK_ENTITIES.register("skull_candle", () ->
                BlockEntityType.Builder.of(DyenamicsSkullCandleBlockEntity::new,
                        ZOMBIE_SKULL_CANDLE.get(), ZOMBIE_WALL_SKULL_CANDLE.get(),
                        SKELETON_SKULL_CANDLE.get(), SKELETON_WALL_SKULL_CANDLE.get(),
                        WITHER_SKELE_SKULL_CANDLE.get(), WITHER_SKELE_WALL_SKULL_CANDLE.get(),
                        CREEPER_SKULL_CANDLE.get(), CREEPER_WALL_SKULL_CANDLE.get(),
                        PLAYER_SKULL_CANDLE.get(), PLAYER_WALL_SKULL_CANDLE.get(),
                        PIGLIN_SKULL_CANDLE.get(), PIGLIN_WALL_SKULL_CANDLE.get()).build(null));
        ZOMBIE_SKULL_CANDLE_ITEM = ITEMS.register("zombie_skull_candle", () -> new DyenamicsSkullCandleItem(ZOMBIE_SKULL_CANDLE.get(), ZOMBIE_WALL_SKULL_CANDLE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        SKELETON_SKULL_CANDLE_ITEM = ITEMS.register("skeleton_skull_candle", () -> new DyenamicsSkullCandleItem(SKELETON_SKULL_CANDLE.get(), SKELETON_WALL_SKULL_CANDLE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        WITHER_SKELE_SKULL_CANDLE_ITEM = ITEMS.register("wither_skeleton_skull_candle", () -> new DyenamicsSkullCandleItem(WITHER_SKELE_SKULL_CANDLE.get(), WITHER_SKELE_WALL_SKULL_CANDLE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        CREEPER_SKULL_CANDLE_ITEM = ITEMS.register("creeper_skull_candle", () -> new DyenamicsSkullCandleItem(CREEPER_SKULL_CANDLE.get(), CREEPER_WALL_SKULL_CANDLE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        PLAYER_SKULL_CANDLE_ITEM = ITEMS.register("player_skull_candle", () -> new DyenamicsSkullCandleItem(PLAYER_SKULL_CANDLE.get(), PLAYER_WALL_SKULL_CANDLE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        PIGLIN_SKULL_CANDLE_ITEM = ITEMS.register("piglin_skull_candle", () -> new DyenamicsSkullCandleItem(PIGLIN_SKULL_CANDLE.get(), PIGLIN_WALL_SKULL_CANDLE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        TWILIGHT_DYENAMICS_TAB = CREATIVE_MODE_TABS.register("twilight_dyenamics", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + MODID+".twilight_dyenamics"))
                .icon(TwilightDyenamicsMain::create_tab_stack)
                .displayItems((params, output) -> {
                    output.accept(SKELETON_SKULL_CANDLE_ITEM.get());
                    output.accept(WITHER_SKELE_SKULL_CANDLE_ITEM.get());
                    output.accept(ZOMBIE_SKULL_CANDLE_ITEM.get());
                    output.accept(CREEPER_SKULL_CANDLE_ITEM.get());
                    output.accept(PIGLIN_SKULL_CANDLE_ITEM.get());
                    output.accept(PLAYER_SKULL_CANDLE_ITEM.get());
                })
                .build()
        );
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        COMPONENTS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(()->{
            for (int i = 0; i < DyenamicDyeColor.dyenamicValues().length; i++) {
                DyenamicDyeColor color = DyenamicDyeColor.dyenamicValues()[i];
                OminousCandleBlock.CANDLE_MAP.put(BlockInit.DYED_BLOCKS.get(color.getSerializedName()).get("candle").get(),OMINOUS_CANDLES.get(color.getSerializedName()));
                DispenserBlock.registerBehavior(BuiltInRegistries.ITEM.get(ResourceLocation.parse("dyenamics:"+color.getSerializedName()+"_candle")), new DyenamicsCandleDispenseBehavior());
            }
            NeoForge.EVENT_BUS.addListener(DyenamicsEntityEvents::createSkullCandle);
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}