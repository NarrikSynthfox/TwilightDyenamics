package com.narsyn.twilightdyenamics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.narsyn.twilightdyenamics.block.DyenamicsAbstractSkullCandleBlock;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DyenamicsSkullCandles(String color, int count, int glowType) {
	public static final Codec<DyenamicsSkullCandles> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			Codec.STRING.fieldOf("color").forGetter(DyenamicsSkullCandles::color),
			Codec.intRange(1, 4).fieldOf("count").forGetter(DyenamicsSkullCandles::count),
			Codec.intRange(0, 2).fieldOf("glow_color").forGetter(DyenamicsSkullCandles::glowType)
	).apply(inst, DyenamicsSkullCandles::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, DyenamicsSkullCandles> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, DyenamicsSkullCandles::color,
		ByteBufCodecs.INT, DyenamicsSkullCandles::count,
		ByteBufCodecs.INT, DyenamicsSkullCandles::glowType,
		DyenamicsSkullCandles::new
	);

	public static final DyenamicsSkullCandles DEFAULT = new DyenamicsSkullCandles("peach", 1,0);
}
