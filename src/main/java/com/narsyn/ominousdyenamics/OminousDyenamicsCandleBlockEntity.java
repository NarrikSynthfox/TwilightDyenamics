package com.narsyn.ominousdyenamics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class OminousDyenamicsCandleBlockEntity extends twilightforest.block.entity.OminousCandleBlockEntity {
    public OminousDyenamicsCandleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public OminousDyenamicsCandleBlockEntity(BlockPos pos, BlockState state) {
        this(OminousDyenamicsCandles.CANDLE_ENTITY.get(), pos, state);
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
