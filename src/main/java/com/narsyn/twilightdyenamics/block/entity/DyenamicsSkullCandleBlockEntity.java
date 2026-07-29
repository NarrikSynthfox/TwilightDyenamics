package com.narsyn.twilightdyenamics.block.entity;

//Copied from SkullCandleBlockEntity from Twilight Forest, tweaked for Dyenamics candles support.

import com.narsyn.twilightdyenamics.TwilightDyenamicsMain;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;

public class DyenamicsSkullCandleBlockEntity extends SkullBlockEntity {

	private String candleColor;

	private int animationTickCount;
	private boolean isAnimating;

	public DyenamicsSkullCandleBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public DyenamicsSkullCandleBlockEntity(BlockPos pos, BlockState state, String color) {
		super(pos, state);
		this.candleColor = color;
	}

	@Override
	public boolean isValidBlockState(BlockState state) {
		return this.getType().isValid(state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DyenamicsSkullCandleBlockEntity entity) {
		if (level.hasNeighborSignal(pos)) {
			entity.isAnimating = true;
			++entity.animationTickCount;
		} else {
			entity.isAnimating = false;
		}

	}

	@Override
	public BlockEntityType<?> getType() {
		return TwilightDyenamicsMain.SKULL_CANDLE_ENTITY.get();
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
		super.saveAdditional(tag, provider);
		tag.putString("CandleColor", this.candleColor);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
		super.loadAdditional(tag, provider);
		this.candleColor = tag.getString("CandleColor");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag tag = super.getUpdateTag(provider);
		tag.putString("CandleColor", this.candleColor);
		return tag;
	}

	public String getCandleColor() {
		try{
			DyenamicDyeColor color = DyenamicDyeColor.valueOf(this.candleColor.toUpperCase(Locale.ROOT));
			return color.getId() < 16 ? "peach": this.candleColor;
		}catch(IllegalArgumentException e){
			return "peach";
		}
	}

	public void setCandleColor(String colorString) {
		try{
			DyenamicDyeColor color = DyenamicDyeColor.valueOf(colorString.toUpperCase(Locale.ROOT));
			this.candleColor = color.getId() < 16 ? "peach": colorString;
		}catch(IllegalArgumentException e){
			this.candleColor = "peach";
		}
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
	}

	@Override
	public float getAnimation(float partialTick) {
		return this.isAnimating ? (float) this.animationTickCount + partialTick : (float) this.animationTickCount;
	}
}
