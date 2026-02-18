package com.narsyn.twilightdyenamics.item;

//Copied from SkullCandleItem from Twilight Forest, tweaked for Dyenamics candles support.

import com.narsyn.twilightdyenamics.block.DyenamicsAbstractSkullCandleBlock;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class DyenamicsSkullCandleItem extends StandingAndWallBlockItem implements Equipable {

	public DyenamicsSkullCandleItem(DyenamicsAbstractSkullCandleBlock floor, DyenamicsAbstractSkullCandleBlock wall, Properties properties) {
		super(floor, wall, properties, Direction.DOWN);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (data != null && !data.isEmpty()) {
			CompoundTag tag = data.copyTag();
			if (tag.contains("CandleColor") && tag.contains("CandleAmount")) {
				tooltip.add(
					Component.translatable(tag.getInt("CandleAmount") > 1 ?
								"item.twilight_dyenamics.skull_candle.desc.multiple" :
								"item.twilight_dyenamics.skull_candle.desc",
							String.valueOf(tag.getInt("CandleAmount")),
							WordUtils.capitalize(DyenamicDyeColor.byId(tag.getInt("CandleColor")).getSerializedName()
								.replace("\"", "").replace("_", " ")))
						.withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return this.swapWithEquipmentSlot(this, level, player, hand);
	}

	@Override
	public Component getName(ItemStack stack) {
		ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
		return resolvableprofile != null && resolvableprofile.name().isPresent()
			? Component.translatable(this.getDescriptionId() + ".named", resolvableprofile.name().get())
			: super.getName(stack);
	}

	@Override
	public void verifyComponentsAfterLoad(ItemStack stack) {
		ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
		if (resolvableprofile != null && !resolvableprofile.isResolved()) {
			resolvableprofile.resolve().thenAcceptAsync(profile -> stack.set(DataComponents.PROFILE, profile), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
		}
	}
	
}