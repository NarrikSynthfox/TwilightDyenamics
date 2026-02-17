package com.narsyn.twilightdyenamics;

import com.narsyn.twilightdyenamics.client.DyenamicsSkullCandleRenderer;
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


@Mod(value = TwilightDyenamicsMain.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TwilightDyenamicsMain.MODID, value = Dist.CLIENT)
public class TwilightDyenamicsClient {
    public TwilightDyenamicsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        TwilightDyenamicsMain.LOGGER.info("Twilight Dyenamics Client Setup");
    }

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        TwilightDyenamicsMain.LOGGER.info("Registering BlockEntity Renderer");
        event.registerBlockEntityRenderer(TwilightDyenamicsMain.CANDLE_ENTITY.get(), OminousCandleRenderer::new);
        event.registerBlockEntityRenderer(TwilightDyenamicsMain.SKULL_CANDLE_ENTITY.get(), DyenamicsSkullCandleRenderer::new);
    }
}
