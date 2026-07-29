package com.narsyn.twilightdyenamics.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.narsyn.twilightdyenamics.block.entity.DyenamicsOminousCandleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import twilightforest.block.OminousCandleBlock;


public class DyenamicsOminousCandleBlock extends OminousCandleBlock {
    public static final MapCodec<OminousCandleBlock> CODEC = RecordCodecBuilder.mapCodec(
            app -> app.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("candle").forGetter(block -> block.candle), propertiesCodec())
                    .apply(app, OminousCandleBlock::new)
    );
    public DyenamicsOminousCandleBlock(Block candle, Properties properties) {
        super(candle, properties);
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DyenamicsOminousCandleBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(this.candle);
    }
}
