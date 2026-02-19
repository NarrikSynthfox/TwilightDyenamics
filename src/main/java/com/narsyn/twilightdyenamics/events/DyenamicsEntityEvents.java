package com.narsyn.twilightdyenamics.events;

//createSkullCandle and makeSkullCandle methods copied from EntityEvents from Twilight Forest, tweaked for Dyenamics candles support.

import com.narsyn.twilightdyenamics.TwilightDyenamicsMain;
import com.narsyn.twilightdyenamics.block.DyenamicsAbstractSkullCandleBlock;
import com.narsyn.twilightdyenamics.block.DyenamicsSkullCandleBlock;
import com.narsyn.twilightdyenamics.entity.DyenamicsSkullCandleBlockEntity;
import cy.jdkdigital.dyenamics.core.init.BlockInit;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import twilightforest.block.LightableBlock;
import twilightforest.config.TFConfig;
import twilightforest.init.TFStats;

public class DyenamicsEntityEvents {

	/**
	 * Checks if the player is attempting to create a skull candle
	 */
	// I wanted to make sure absolutely nothing broke, so I also check against the namespaces of the item to make sure theyre vanilla.
	// Worst case some stupid mod adds their own stuff to the minecraft namespace and breaks this, then you can disable this via config.
    public static void createSkullCandle(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		if (!TFConfig.disableSkullCandles) {
			if (stack.getItem().toString().startsWith("dyenamics:") && stack.getItem().toString().endsWith("candle") && !event.getEntity().isShiftKeyDown()) {
				if (state.getBlock() instanceof AbstractSkullBlock skull && BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals("minecraft")) {
					SkullBlock.Types type = (SkullBlock.Types) skull.getType();
					boolean wall = state.getBlock() instanceof WallSkullBlock;
					switch (type) {
						case SKELETON -> {
							if (wall) makeSkullCandle(event, TwilightDyenamicsMain.SKELETON_WALL_SKULL_CANDLE.get());
							else makeSkullCandle(event, TwilightDyenamicsMain.SKELETON_SKULL_CANDLE.get());
						}
						case WITHER_SKELETON -> {
							if (wall) makeSkullCandle(event, TwilightDyenamicsMain.WITHER_SKELE_WALL_SKULL_CANDLE.get());
							else makeSkullCandle(event, TwilightDyenamicsMain.WITHER_SKELE_SKULL_CANDLE.get());
						}
						case PLAYER -> {
							if (wall) makeSkullCandle(event, TwilightDyenamicsMain.PLAYER_WALL_SKULL_CANDLE.get());
							else makeSkullCandle(event, TwilightDyenamicsMain.PLAYER_SKULL_CANDLE.get());
						}
						case ZOMBIE -> {
							if (wall) makeSkullCandle(event, TwilightDyenamicsMain.ZOMBIE_WALL_SKULL_CANDLE.get());
							else makeSkullCandle(event, TwilightDyenamicsMain.ZOMBIE_SKULL_CANDLE.get());
						}
						case CREEPER -> {
							if (wall) makeSkullCandle(event, TwilightDyenamicsMain.CREEPER_WALL_SKULL_CANDLE.get());
							else makeSkullCandle(event, TwilightDyenamicsMain.CREEPER_SKULL_CANDLE.get());
						}
						case PIGLIN -> {
							if (wall) makeSkullCandle(event, TwilightDyenamicsMain.PIGLIN_WALL_SKULL_CANDLE.get());
							else makeSkullCandle(event, TwilightDyenamicsMain.PIGLIN_SKULL_CANDLE.get());
						}
						default -> {
							return;
						}
					}
					stack.consume(1, event.getEntity());
					event.getEntity().swing(event.getHand());
					if (event.getEntity() instanceof ServerPlayer)
						event.getEntity().awardStat(TFStats.SKULL_CANDLES_MADE.get());
					//this is to prevent anything from being placed afterwords
					event.setCanceled(true);
				}
			}
		}
	}

	private static void makeSkullCandle(PlayerInteractEvent.RightClickBlock event, Block newBlock) {
		ResolvableProfile profile = null;
		Level level = event.getLevel();
		String colorString=DyenamicsAbstractSkullCandleBlock.candleToCandleColor(event.getItemStack().getItem()).getSerializedName();
		DyenamicsAbstractSkullCandleBlock.GlowingColors glowColor=switch(colorString){
			case "cherenkov" -> DyenamicsAbstractSkullCandleBlock.GlowingColors.CHERENKOV;
			case "fluorescent" -> DyenamicsAbstractSkullCandleBlock.GlowingColors.FLUORESCENT;
			default -> DyenamicsAbstractSkullCandleBlock.GlowingColors.NONE;
		};
		if (level.getBlockEntity(event.getPos()) instanceof SkullBlockEntity skull)
			profile = skull.getOwnerProfile();
		level.playSound(null, event.getPos(), SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.setBlockAndUpdate(event.getPos(), newBlock.withPropertiesOf(level.getBlockState(event.getPos()))
			.setValue(DyenamicsAbstractSkullCandleBlock.LIGHTING, LightableBlock.Lighting.NONE).setValue(DyenamicsAbstractSkullCandleBlock.GLOWCOLOR,glowColor));
		level.setBlockEntity(new DyenamicsSkullCandleBlockEntity(event.getPos(),
			newBlock.withPropertiesOf(level.getBlockState(event.getPos()))
				.setValue(DyenamicsAbstractSkullCandleBlock.LIGHTING, LightableBlock.Lighting.NONE).setValue(DyenamicsAbstractSkullCandleBlock.GLOWCOLOR,glowColor),
				colorString));
		if (level.getBlockEntity(event.getPos()) instanceof DyenamicsSkullCandleBlockEntity sc) sc.setOwner(profile);
	}
}
