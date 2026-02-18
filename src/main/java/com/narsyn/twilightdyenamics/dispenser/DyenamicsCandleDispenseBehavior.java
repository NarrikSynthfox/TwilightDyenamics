package com.narsyn.twilightdyenamics.dispenser;

//Copied from CandleDispenseBehavior from Twilight Forest, tweaked for Dyenamics candles support.

import com.narsyn.twilightdyenamics.TwilightDyenamicsMain;
import com.narsyn.twilightdyenamics.block.DyenamicsAbstractSkullCandleBlock;
import com.narsyn.twilightdyenamics.block.DyenamicsSkullCandleBlock;
import com.narsyn.twilightdyenamics.block.DyenamicsWallSkullCandleBlock;
import com.narsyn.twilightdyenamics.entity.DyenamicsSkullCandleBlockEntity;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import twilightforest.block.CandelabraBlock;
import twilightforest.block.LightableBlock;
import twilightforest.block.entity.CandelabraBlockEntity;

import java.util.Locale;

public class DyenamicsCandleDispenseBehavior extends OptionalDispenseItemBehavior {

	public DyenamicsCandleDispenseBehavior() {
	}

	@Override
	protected ItemStack execute(BlockSource source, ItemStack stack) {
		ServerLevel level = source.level();
		if (!level.isClientSide()) {
			BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
			this.setSuccess(tryAddCandle(level, blockpos, stack.getItem()) || tryCreateSkullCandle(level, blockpos, stack.getItem()));
			if (this.isSuccess()) {
				stack.shrink(1);
			}
		}

		return stack;
	}

	private static boolean tryAddCandle(ServerLevel level, BlockPos pos, Item candle) {
		if (level.getBlockEntity(pos) instanceof DyenamicsSkullCandleBlockEntity sc) {
			if (candle == DyenamicsAbstractSkullCandleBlock.candleColorToCandle(DyenamicDyeColor.valueOf(sc.getCandleColor().toUpperCase(Locale.ROOT))).asItem()) {
				BlockState state = level.getBlockState(pos);
				int candles = state.getValue(BlockStateProperties.CANDLES);
				if (candles < 4) {
					level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.CANDLES, candles + 1));

					level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.getLightEngine().checkBlock(pos);
					level.sendBlockUpdated(pos, state, state, 1);
					return true;
				}
			}
		} else if (level.getBlockEntity(pos) instanceof CandelabraBlockEntity candelabra) {
			if (!(candle instanceof BlockItem block)) return false;
			BlockState state = level.getBlockState(pos);
			for (int i = 0; i < 3; i++) {
				Block storedCandle = candelabra.getCandle(i);
				if (storedCandle != Blocks.AIR) continue;
				level.setBlockAndUpdate(pos, state.setValue(CandelabraBlock.CANDLES.get(i), true));
				candelabra.setCandle(i, block.getBlock());

				level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.getLightEngine().checkBlock(pos);
				level.sendBlockUpdated(pos, state, state, 1);
				return true;
			}
		}
		return false;
	}

	private static boolean tryCreateSkullCandle(ServerLevel level, BlockPos pos, Item candle) {
		BlockState blockstate = level.getBlockState(pos);
		if (blockstate.getBlock() instanceof AbstractSkullBlock skull) {
			SkullBlock.Types type = (SkullBlock.Types) skull.getType();
			boolean wall = blockstate.getBlock() instanceof WallSkullBlock;
			switch (type) {

				case SKELETON -> {
					if (wall) makeWallSkull(level, pos, TwilightDyenamicsMain.SKELETON_WALL_SKULL_CANDLE.get(), candle);
					else makeFloorSkull(level, pos, TwilightDyenamicsMain.SKELETON_SKULL_CANDLE.get(), candle);
				}
				case WITHER_SKELETON -> {
					if (wall) makeWallSkull(level, pos, TwilightDyenamicsMain.WITHER_SKELE_WALL_SKULL_CANDLE.get(), candle);
					else makeFloorSkull(level, pos, TwilightDyenamicsMain.WITHER_SKELE_SKULL_CANDLE.get(), candle);
				}
				case PLAYER -> {
					if (wall) makeWallSkull(level, pos, TwilightDyenamicsMain.PLAYER_WALL_SKULL_CANDLE.get(), candle);
					else makeFloorSkull(level, pos, TwilightDyenamicsMain.PLAYER_SKULL_CANDLE.get(), candle);
				}
				case ZOMBIE -> {
					if (wall) makeWallSkull(level, pos, TwilightDyenamicsMain.ZOMBIE_WALL_SKULL_CANDLE.get(), candle);
					else makeFloorSkull(level, pos, TwilightDyenamicsMain.ZOMBIE_SKULL_CANDLE.get(), candle);
				}
				case CREEPER -> {
					if (wall) makeWallSkull(level, pos, TwilightDyenamicsMain.CREEPER_WALL_SKULL_CANDLE.get(), candle);
					else makeFloorSkull(level, pos, TwilightDyenamicsMain.CREEPER_SKULL_CANDLE.get(), candle);
				}
				default -> {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	private static void makeFloorSkull(Level level, BlockPos pos, Block newBlock, Item candle) {
		ResolvableProfile profile = null;
		if (level.getBlockEntity(pos) instanceof SkullBlockEntity skull) profile = skull.getOwnerProfile();
		level.setBlockAndUpdate(pos, newBlock.defaultBlockState()
			.setValue(DyenamicsAbstractSkullCandleBlock.LIGHTING, LightableBlock.Lighting.NONE)
			.setValue(DyenamicsSkullCandleBlock.ROTATION, level.getBlockState(pos).getValue(SkullBlock.ROTATION)));
		level.setBlockEntity(new DyenamicsSkullCandleBlockEntity(pos,
			newBlock.defaultBlockState()
				.setValue(DyenamicsAbstractSkullCandleBlock.LIGHTING, LightableBlock.Lighting.NONE)
				.setValue(DyenamicsSkullCandleBlock.ROTATION, level.getBlockState(pos).getValue(SkullBlock.ROTATION)),
				DyenamicsAbstractSkullCandleBlock.candleToCandleColor(candle).getSerializedName()));
		if (level.getBlockEntity(pos) instanceof DyenamicsSkullCandleBlockEntity sc) sc.setOwner(profile);
	}

	private static void makeWallSkull(Level level, BlockPos pos, Block newBlock, Item candle) {
		ResolvableProfile profile = null;
		if (level.getBlockEntity(pos) instanceof SkullBlockEntity skull) profile = skull.getOwnerProfile();
		level.setBlockAndUpdate(pos, newBlock.defaultBlockState()
			.setValue(DyenamicsAbstractSkullCandleBlock.LIGHTING, LightableBlock.Lighting.NONE)
			.setValue(DyenamicsWallSkullCandleBlock.FACING, level.getBlockState(pos).getValue(WallSkullBlock.FACING)));
		level.setBlockEntity(new DyenamicsSkullCandleBlockEntity(pos,
			newBlock.defaultBlockState()
				.setValue(DyenamicsAbstractSkullCandleBlock.LIGHTING, LightableBlock.Lighting.NONE)
				.setValue(DyenamicsWallSkullCandleBlock.FACING, level.getBlockState(pos).getValue(WallSkullBlock.FACING)),
				DyenamicsAbstractSkullCandleBlock.candleToCandleColor(candle).getSerializedName()));
		if (level.getBlockEntity(pos) instanceof DyenamicsSkullCandleBlockEntity sc) sc.setOwner(profile);
	}
}
