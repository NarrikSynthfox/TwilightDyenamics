package com.narsyn.twilightdyenamics.block.entity;

import com.narsyn.twilightdyenamics.TwilightDyenamicsMain;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.block.entity.OminousCandleBlockEntity;

public class DyenamicsOminousCandleBlockEntity extends OminousCandleBlockEntity {

    public DyenamicsOminousCandleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public DyenamicsOminousCandleBlockEntity(BlockPos pos, BlockState state) {
        this(TwilightDyenamicsMain.CANDLE_ENTITY.get(), pos, state);
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
