package com.narsyn.twilightdyenamics.client;

//Partially copied from ISTER from Twilight Forest, only needed the Skull Candle ISTER, modified to work with Dyenamics candles.

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.narsyn.twilightdyenamics.TwilightDyenamicsMain;
import com.narsyn.twilightdyenamics.block.DyenamicsAbstractSkullCandleBlock;
import com.narsyn.twilightdyenamics.components.DyenamicsSkullCandles;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class DyenamicsSkullCandleISTER extends BlockEntityWithoutLevelRenderer {

	public static final Supplier<DyenamicsSkullCandleISTER> INSTANCE = Suppliers.memoize(DyenamicsSkullCandleISTER::new);
	public static final IClientItemExtensions CLIENT_ITEM_EXTENSION = Util.make(() -> new IClientItemExtensions() {
		@Override
		public BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return INSTANCE.get();
		}
	});

	private Map<SkullBlock.Type, SkullModelBase> skulls = SkullBlockRenderer.createSkullRenderers(Minecraft.getInstance().getEntityModels());

	// Use the cached INSTANCE.get instead
	private DyenamicsSkullCandleISTER() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		this.skulls = SkullBlockRenderer.createSkullRenderers(Minecraft.getInstance().getEntityModels());

		TwilightDyenamicsMain.LOGGER.debug("Reloaded ISTER!");
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext camera, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			Minecraft minecraft = Minecraft.getInstance();
			if (block instanceof DyenamicsAbstractSkullCandleBlock candleBlock) {
				ResolvableProfile profile = stack.get(DataComponents.PROFILE);

				if (profile != null && !profile.isResolved()) {
					stack.remove(DataComponents.PROFILE);
					profile.resolve().thenAcceptAsync(p_329787_ -> stack.set(DataComponents.PROFILE, p_329787_), minecraft);

					return;
				}

				SkullBlock.Type type = candleBlock.getType();
				SkullModelBase base = this.skulls.get(type);
				RenderType renderType = DyenamicsSkullCandleRenderer.getRenderType(type, profile);
				DyenamicsSkullCandleRenderer.renderSkull(null, 180.0F, 0.0F, pose, buffers, light, base, renderType);

				//we put the candle
				pose.translate(0.0F, 0.5F, 0.0F);

				DyenamicsSkullCandles skullCandles = stack.getOrDefault(TwilightDyenamicsMain.SKULL_CANDLES, DyenamicsSkullCandles.DEFAULT);

				minecraft.getBlockRenderer().renderSingleBlock(
						DyenamicsAbstractSkullCandleBlock.candleColorToCandle(DyenamicDyeColor.valueOf(skullCandles.color().toUpperCase(Locale.ROOT)))
								.defaultBlockState().setValue(CandleBlock.CANDLES, skullCandles.count()), pose, buffers, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
			}
		}
	}
}
