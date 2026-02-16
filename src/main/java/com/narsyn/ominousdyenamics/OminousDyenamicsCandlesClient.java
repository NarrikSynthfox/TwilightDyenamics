package com.narsyn.ominousdyenamics;

import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import twilightforest.client.renderer.block.OminousCandleRenderer;

@Mod(value = OminousDyenamicsCandles.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OminousDyenamicsCandles.MODID, value = Dist.CLIENT)
public class OminousDyenamicsCandlesClient {
    public OminousDyenamicsCandlesClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        OminousDyenamicsCandles.LOGGER.info("Ominous Dyenamics Candles Client Setup");
    }

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        OminousDyenamicsCandles.LOGGER.info("Registering BlockEntity Renderer");
        event.registerBlockEntityRenderer(OminousDyenamicsCandles.CANDLE_ENTITY.get(), OminousCandleRenderer::new);
    }
}
