package com.narsyn.ominousdyenamics;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import twilightforest.block.OminousCandleBlock;
import cy.jdkdigital.dyenamics.core.init.BlockInit;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import twilightforest.block.entity.OminousCandleBlockEntity;

import java.util.HashMap;
import java.util.Map;

@Mod(OminousDyenamicsCandles.MODID)
public class OminousDyenamicsCandles {
    public static final String MODID = "ominous_dyenamics_candles";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final Map<String,DeferredBlock<OminousCandleBlock>> OMINOUS_CANDLES = new HashMap<>();
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,OminousDyenamicsCandles.MODID);
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<OminousDyenamicsCandleBlockEntity>> CANDLE_ENTITY;
    public OminousDyenamicsCandles(IEventBus modEventBus, Dist dist) {
        modEventBus.addListener(this::commonSetup);
        for (int i = 0; i < DyenamicDyeColor.dyenamicValues().length; i++) {
            DyenamicDyeColor color = DyenamicDyeColor.dyenamicValues()[i];
            DeferredBlock<OminousCandleBlock> block=BLOCKS.register(color.getSerializedName() + "_ominous_candle",
                    () -> new OminousDyenamicsCandleBlock(
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
                BlockEntityType.Builder.of(OminousDyenamicsCandleBlockEntity::new,OMINOUS_CANDLES.values().stream()
                                .map(DeferredBlock::get)
                                .toArray(Block[]::new)).build(null));
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        for (int i = 0; i < DyenamicDyeColor.dyenamicValues().length; i++) {
            DyenamicDyeColor color = DyenamicDyeColor.dyenamicValues()[i];
            OminousCandleBlock.CANDLE_MAP.put(BlockInit.DYED_BLOCKS.get(color.getSerializedName()).get("candle").get(),OMINOUS_CANDLES.get(color.getSerializedName()));
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}