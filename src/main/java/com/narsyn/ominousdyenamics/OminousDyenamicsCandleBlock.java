package com.narsyn.ominousdyenamics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OminousDyenamicsCandleBlock extends twilightforest.block.OminousCandleBlock {
    public OminousDyenamicsCandleBlock (Block candle, Properties properties) {
        super(candle, properties);
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OminousDyenamicsCandleBlockEntity(pos, state);
    }

}
