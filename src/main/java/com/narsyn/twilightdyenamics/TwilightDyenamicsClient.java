package com.narsyn.twilightdyenamics;

import com.google.common.eventbus.Subscribe;
import com.narsyn.twilightdyenamics.client.DyenamicsSkullCandleISTER;
import com.narsyn.twilightdyenamics.client.DyenamicsSkullCandleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import twilightforest.client.renderer.block.OminousCandleRenderer;


@Mod(value = TwilightDyenamicsMain.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TwilightDyenamicsMain.MODID, value = Dist.CLIENT)
public class TwilightDyenamicsClient {
    private static boolean firstTitleScreenShown = false;
    public TwilightDyenamicsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    @SubscribeEvent
    private static void handleGameBootup(ScreenEvent.Init.Post event) {
        if (firstTitleScreenShown || !(event.getScreen() instanceof TitleScreen)) return;

        if (Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager resourceManager) {
            resourceManager.registerReloadListener(DyenamicsSkullCandleISTER.INSTANCE.get());
            TwilightDyenamicsMain.LOGGER.debug("Registered Skull Candle ISTER listener");
        }

        firstTitleScreenShown = true;
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
    @SubscribeEvent
    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(DyenamicsSkullCandleISTER.CLIENT_ITEM_EXTENSION,
                TwilightDyenamicsMain.CREEPER_SKULL_CANDLE_ITEM.get(),
                TwilightDyenamicsMain.PIGLIN_SKULL_CANDLE_ITEM.get(),
                TwilightDyenamicsMain.PLAYER_SKULL_CANDLE_ITEM.get(),
                TwilightDyenamicsMain.SKELETON_SKULL_CANDLE_ITEM.get(),
                TwilightDyenamicsMain.WITHER_SKELE_SKULL_CANDLE_ITEM.get(),
                TwilightDyenamicsMain.ZOMBIE_SKULL_CANDLE_ITEM.get());
    }
}
