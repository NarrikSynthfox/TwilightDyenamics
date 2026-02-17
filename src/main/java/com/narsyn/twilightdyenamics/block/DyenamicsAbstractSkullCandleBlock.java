package com.narsyn.twilightdyenamics.block;

//Copied from AbstractSkullCandleBlock from Twilight Forest, tweaked for Dyenamics candles support.

import com.narsyn.twilightdyenamics.TwilightDyenamicsMain;
import com.narsyn.twilightdyenamics.entity.DyenamicsSkullCandleBlockEntity;
import cy.jdkdigital.dyenamics.core.init.BlockInit;
import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;
import twilightforest.block.LightableBlock;
import twilightforest.components.item.SkullCandles;
import twilightforest.init.TFDataComponents;

import java.util.*;

public abstract class DyenamicsAbstractSkullCandleBlock extends BaseEntityBlock implements LightableBlock {
	public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
	private final SkullBlock.Type type;

	public DyenamicsAbstractSkullCandleBlock(SkullBlock.Type type, Properties properties) {
		super(properties);
		this.type = type;
		this.registerDefaultState(this.getStateDefinition().any().setValue(LIGHTING, Lighting.NONE).setValue(CANDLES, 1));
	}

	public SkullBlock.Type getType() {
		return this.type;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {
		return switch (state.getValue(LIGHTING)) {
			case NORMAL -> 3 * state.getValue(CANDLES);
			case OMINOUS -> 2 * state.getValue(CANDLES);
			case DIM -> state.getValue(CANDLES);
			default -> 0;
		};
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		if (ItemAbilities.FIRESTARTER_LIGHT == itemAbility) {
			if (this.canBeLit(state)) {
				return state.setValue(LIGHTING, Lighting.NORMAL);
			}
		}
		return super.getToolModifiedState(state, context, itemAbility, simulate);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DyenamicsSkullCandleBlockEntity(pos, state, 0);
	}

	//input one of the enum names to convert it into a candle block
	public static Block candleColorToCandle(DyenamicDyeColor color) {
		return Objects.requireNonNull(BlockInit.DYED_BLOCKS.get(color.getSerializedName()).get("candle").get());
	}

	//inverse of above
	public static DyenamicDyeColor candleToCandleColor(Item candle) {
			String colorString=candle.toString().replace("dyenamics:","").replace("_candle","");
			return DyenamicDyeColor.valueOf(colorString.toUpperCase(Locale.ROOT));
	}

	@Override
	@SuppressWarnings("deprecation") // Fine for override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof DyenamicsSkullCandleBlockEntity sc) {
			SkullCandles skullCandles = stack.getOrDefault(TFDataComponents.SKULL_CANDLES, SkullCandles.DEFAULT);
			sc.setCandleColor(skullCandles.color());

			if (this.type == SkullBlock.Types.PLAYER && stack.has(DataComponents.PROFILE)) {
				sc.setOwner(stack.get(DataComponents.PROFILE));
			}
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder);
		Optional<ItemStack> skullStack = drops.stream().filter(item -> item.is(ItemTags.SKULLS) && !item.is(this.asItem())).findFirst();
		if (skullStack.isPresent()) {
			BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
			if (blockEntity instanceof DyenamicsSkullCandleBlockEntity sc) {
				if (!builder.getParameter(LootContextParams.TOOL).isEmpty() && builder.getParameter(LootContextParams.TOOL).getEnchantmentLevel(sc.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SILK_TOUCH)) > 0) {
					ItemStack newStack = new ItemStack(this);

					newStack.set(TFDataComponents.SKULL_CANDLES, new SkullCandles(sc.getCandleColor(), state.getValue(CANDLES)));

					if (this.type == SkullBlock.Types.PLAYER && sc.getOwnerProfile() != null)
						newStack.set(DataComponents.PROFILE, sc.getOwnerProfile());

					drops.remove(skullStack.get());
					drops.add(newStack);
				} else {
					drops.add(new ItemStack(candleColorToCandle(DyenamicDyeColor.byId(sc.getCandleColor())), state.getValue(CANDLES)));
				}
			}
		}

		return drops;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		ItemStack newStack = new ItemStack(this);
		TwilightDyenamicsMain.LOGGER.info("ItemStack is "+newStack);
		if (level.getBlockEntity(pos) instanceof DyenamicsSkullCandleBlockEntity sc) {
			newStack.set(TFDataComponents.SKULL_CANDLES, new SkullCandles(sc.getCandleColor(), state.getValue(CANDLES)));

			if (this.type == SkullBlock.Types.PLAYER && sc.getOwnerProfile() != null)
				newStack.set(DataComponents.PROFILE, sc.getOwnerProfile());
		}

		return newStack;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (level.getBlockEntity(pos) instanceof DyenamicsSkullCandleBlockEntity sc) {
			if (stack.is(ItemTags.CANDLES)
				&& stack.is(candleColorToCandle(DyenamicDyeColor.byId(sc.getCandleColor())).asItem())
				&& !player.isShiftKeyDown()) {
				int candles = state.getValue(CANDLES);
				if (candles < 4) {
					level.setBlockAndUpdate(pos, state.setValue(CANDLES, candles + 1));

					level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
					stack.consume(1, player);
					level.getLightEngine().checkBlock(pos);
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				}
			}
		}
		return this.tryLightCandles(stack, state, level, pos, player);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof DyenamicsSkullCandleBlockEntity sc && player.isSecondaryUseActive() && state.getValue(CANDLES) > 0) {
			int newCandleAmount = state.getValue(CANDLES) - 1;
			if (newCandleAmount > 0) {
				level.setBlockAndUpdate(pos, state.setValue(CANDLES, newCandleAmount));
			} else {
				boolean wall = state.getBlock() instanceof DyenamicsWallSkullCandleBlock;
				Block newBlock = getNoCandleSkull(wall);
				if (newBlock != null) {
					ResolvableProfile profile = sc.getOwnerProfile();
                    BlockState newState;
                    if (wall) {
                        newState = newBlock.defaultBlockState().setValue(WallSkullBlock.FACING, state.getValue(DyenamicsWallSkullCandleBlock.FACING));
                    } else {
                        newState = newBlock.defaultBlockState().setValue(SkullBlock.ROTATION, state.getValue(DyenamicsSkullCandleBlock.ROTATION));
                    }
                    level.setBlockAndUpdate(pos, newState);
                    level.setBlockEntity(new SkullBlockEntity(pos, newState));
                    if (level.getBlockEntity(pos) instanceof SkullBlockEntity sc1) sc1.setOwner(profile);
				}
			}
			level.playSound(null, pos, SoundEvents.CANDLE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.getLightEngine().checkBlock(pos);
			ItemStack candle = new ItemStack(candleColorToCandle(DyenamicDyeColor.byId(sc.getCandleColor())));
			if (player.hasInfiniteMaterials()) {
				if (!player.getInventory().contains(candle)) {
					player.getInventory().add(candle);
				}
			} else {
				if (!player.getInventory().add(candle)) {
					player.drop(candle, false);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	private Block getNoCandleSkull(boolean wall) {
		Block newBlock;
		switch ((SkullBlock.Types) this.getType()) {
			case SKELETON -> {
				if (wall) newBlock = Blocks.SKELETON_WALL_SKULL;
				else newBlock = Blocks.SKELETON_SKULL;
			}
			case WITHER_SKELETON -> {
				if (wall) newBlock = Blocks.WITHER_SKELETON_WALL_SKULL;
				else newBlock = Blocks.WITHER_SKELETON_SKULL;
			}
			case PLAYER -> {
				if (wall) newBlock = Blocks.PLAYER_WALL_HEAD;
				else newBlock = Blocks.PLAYER_HEAD;
			}
			case ZOMBIE -> {
				if (wall) newBlock = Blocks.ZOMBIE_WALL_HEAD;
				else newBlock = Blocks.ZOMBIE_HEAD;
			}
			case CREEPER -> {
				if (wall) newBlock = Blocks.CREEPER_WALL_HEAD;
				else newBlock = Blocks.CREEPER_HEAD;
			}
			case PIGLIN -> {
				if (wall) newBlock = Blocks.PIGLIN_WALL_HEAD;
				else newBlock = Blocks.PIGLIN_HEAD;
			}
			default -> newBlock = null;
		}
		return newBlock;
	}

	@Override
	public void onProjectileHit(Level level, BlockState state, BlockHitResult result, Projectile projectile) {
		this.lightCandlesWithProjectile(level, state, result, projectile);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (state.getValue(LIGHTING) != Lighting.NONE) {
			this.getParticleOffsets(state, level, pos).forEach(offset -> {
				Vec3 trueOffset = offset.add(pos.getX(), pos.getY(), pos.getZ());
				this.addParticlesAndSound(level, trueOffset.x(), trueOffset.y(), trueOffset.z(), rand, state.getValue(LIGHTING));
			});
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIGHTING, CANDLES);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, TwilightDyenamicsMain.SKULL_CANDLE_ENTITY.get(), DyenamicsSkullCandleBlockEntity::tick);
	}
}
