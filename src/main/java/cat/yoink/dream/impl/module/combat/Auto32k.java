package cat.yoink.dream.impl.module.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.CommandUtil;

public class Auto32k extends Module {

	private static final DecimalFormat df = new DecimalFormat("#.#");

	private final Setting placeRange = new Setting.Builder(SettingType.INTEGER).setName("Place Range").setModule(this)
			.setIntegerValue(4).setMinIntegerValue(1).setMaxIntegerValue(6).build();

	private final Setting yOffset = new Setting.Builder(SettingType.INTEGER).setName("Y Hopper Offset").setModule(this)
			.setIntegerValue(1).setMinIntegerValue(1).setMaxIntegerValue(4).build();

	private final Setting moveToHotbar = new Setting.Builder(SettingType.BOOLEAN).setName("Move To Hotbar")
			.setModule(this).setBooleanValue(true).build();

	private final Setting placeCloseToEnemy = new Setting.Builder(SettingType.BOOLEAN).setName("Place close to enemy")
			.setModule(this).setBooleanValue(false).build();

	private final Setting placeObiOnTop = new Setting.Builder(SettingType.BOOLEAN).setName("Place Obi on Top")
			.setModule(this).setBooleanValue(true).build();

	private final Setting debug = new Setting.Builder(SettingType.BOOLEAN).setName("Debug Messages").setModule(this)
			.setBooleanValue(true).build();

	private int swordSlot;
	private static boolean isSneaking;

	private static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);

	private static final List<Block> blacklist = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST);

	public Auto32k(String name, String description, Category category) {
		super(name, description, category);
		addSetting(placeRange);
		addSetting(yOffset);
		addSetting(moveToHotbar);
		addSetting(placeCloseToEnemy);
		addSetting(placeObiOnTop);
		addSetting(debug);
		// TODO Auto-generated constructor stub
	}

	public BlockPos getPlayerPos() {
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}

	public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
		List<BlockPos> circleblocks = new ArrayList<>();
		int cx = loc.getX();
		int cy = loc.getY();
		int cz = loc.getZ();
		for (int x = cx - (int) r; x <= cx + r; x++) {
			for (int z = cz - (int) r; z <= cz + r; z++) {
				for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
						BlockPos l = new BlockPos(x, y + plus_y, z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}

	@Override
	public void onEnable() {
		df.setRoundingMode(RoundingMode.CEILING);
		int hopperSlot = -1;
		int shulkerSlot = -1;
		int obiSlot = -1;
		swordSlot = -1;

		for (int i = 0; i < 9; i++) {

			if (hopperSlot != -1 && shulkerSlot != -1 && obiSlot != -1) {
				break;
			}

			ItemStack stack = mc.player.inventory.getStackInSlot(i);

			if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
				continue;
			}

			Block block = ((ItemBlock) stack.getItem()).getBlock();

			if (block == Blocks.HOPPER) {
				hopperSlot = i;
			} else if (shulkerList.contains(block)) {
				shulkerSlot = i;
			} else if (block == Blocks.OBSIDIAN) {
				obiSlot = i;
			}

		}

		if (hopperSlot == -1) {
			if (debug.getBooleanValue()) {
				CommandUtil.sendChatMessage("[Auto32k] Hopper missing, disabling.");
			}
			this.disable();
			return;
		}

		if (shulkerSlot == -1) {
			if (debug.getBooleanValue()) {
				CommandUtil.sendChatMessage("[Auto32k] Shulker missing, disabling.");
			}
			this.disable();
			return;
		}

		int range = (int) Math.ceil(placeRange.getIntegerValue());

		List<BlockPos> placeTargetList = getSphere(getPlayerPos(), range, range, false, true, 0);
		Map<BlockPos, Double> placeTargetMap = new HashMap<>();

		BlockPos placeTarget = null;
		boolean useRangeSorting = false;

		for (BlockPos placeTargetTest : placeTargetList) {
			for (Entity entity : mc.world.loadedEntityList) {

				if (!(entity instanceof EntityPlayer)) {
					continue;
				}

				if (entity == mc.player) {
					continue;
				}

				if (yOffset.getIntegerValue() != 0) {
					if (Math.abs(mc.player.getPosition().getY() - placeTargetTest.getY()) > Math
							.abs(yOffset.getIntegerValue())) {
						continue;
					}
				}

				if (isAreaPlaceable(placeTargetTest)) {
					double distanceToEntity = entity.getDistance(placeTargetTest.getX(), placeTargetTest.getY(),
							placeTargetTest.getZ());
					// Add distance to Map Value of placeTarget Key
					placeTargetMap.put(placeTargetTest,
							placeTargetMap.containsKey(placeTargetTest)
									? placeTargetMap.get(placeTargetTest) + distanceToEntity
									: distanceToEntity);
					useRangeSorting = true;
				}

			}
		}

		if (placeTargetMap.size() > 0) {

			placeTargetMap.forEach((k, v) -> {
				if (!isAreaPlaceable(k)) {
					placeTargetMap.remove(k);
				}
			});

			if (placeTargetMap.size() == 0) {
				useRangeSorting = false;
			}

		}

		if (useRangeSorting) {

			if (placeCloseToEnemy.getBooleanValue()) {

				// Get Key with lowest Value (closest to enemies)
				placeTarget = Collections.min(placeTargetMap.entrySet(), Map.Entry.comparingByValue()).getKey();
			} else {

				// Get Key with highest Value (furthest away from enemies)
				placeTarget = Collections.max(placeTargetMap.entrySet(), Map.Entry.comparingByValue()).getKey();
			}

		} else {

			// Use any place target position if no enemies are around
			for (BlockPos pos : placeTargetList) {
				if (isAreaPlaceable(pos)) {
					placeTarget = pos;
					break;
				}
			}

		}

		if (placeTarget == null) {
			if (debug.getBooleanValue()) {
				CommandUtil.sendChatMessage("[Auto32k] No valid position in range to place!");
			}
			this.disable();
			return;
		}

		if (debug.getBooleanValue()) {
			CommandUtil.sendChatMessage("[Auto32k] Place Target: " + placeTarget.getX() + " " + placeTarget.getY() + " "
					+ placeTarget.getZ() + " Distance: "
					+ df.format(mc.player.getPositionVector().distanceTo(new Vec3d(placeTarget))));
		}

		mc.player.inventory.currentItem = hopperSlot;
		placeBlock(new BlockPos(placeTarget));

		mc.player.inventory.currentItem = shulkerSlot;
		placeBlock(new BlockPos(placeTarget.add(0, 1, 0)));

		if (placeObiOnTop.getBooleanValue() && obiSlot != -1) {
			mc.player.inventory.currentItem = obiSlot;
			placeBlock(new BlockPos(placeTarget.add(0, 2, 0)));
		}

		if (isSneaking) {
			mc.player.connection
					.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			isSneaking = false;
		}

		mc.player.inventory.currentItem = shulkerSlot;
		BlockPos hopperPos = new BlockPos(placeTarget);
		mc.player.connection.sendPacket(
				new CPacketPlayerTryUseItemOnBlock(hopperPos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0, 0, 0));
		swordSlot = shulkerSlot + 32;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null)
			return;

		if (!(mc.currentScreen instanceof GuiContainer)) {
			return;
		}

		if (!moveToHotbar.getBooleanValue()) {
			this.disable();
			return;
		}

		if (swordSlot == -1) {
			return;
		}

		boolean swapReady = true;

		if (((GuiContainer) mc.currentScreen).inventorySlots.getSlot(0).getStack().isEmpty()) {
			swapReady = false;
		}

		if (!((GuiContainer) mc.currentScreen).inventorySlots.getSlot(swordSlot).getStack().isEmpty()) {
			swapReady = false;
		}

		if (swapReady) {
			mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 0,
					swordSlot - 32, ClickType.SWAP, mc.player);

			this.disable();
		}
	}

	private boolean isAreaPlaceable(BlockPos blockPos) {

		for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos))) {
			if (entity instanceof EntityLivingBase) {
				return false; // entity on block
			}
		}

		if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
			return false; // no space for hopper
		}

		if (!mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial().isReplaceable()) {
			return false; // no space for shulker
		}

		if (mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() instanceof BlockAir) {
			return false; // air below hopper
		}

		if (mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() instanceof BlockLiquid) {
			return false; // liquid below hopper
		}

		if (mc.player.getPositionVector().distanceTo(new Vec3d(blockPos)) > placeRange.getIntegerValue()) {
			return false; // out of range
		}

		Block block = mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock();
		if (blacklist.contains(block) || shulkerList.contains(block)) {
			return false; // would need sneak
		}

		return !(mc.player.getPositionVector().distanceTo(new Vec3d(blockPos).add(0, 1, 0)) > placeRange
				.getIntegerValue()); // out of range

	}

	private void placeBlock(BlockPos pos) {

		if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
			return;
		}

		// check if we have a block adjacent to blockpos to click at
		if (!checkForNeighbours(pos)) {
			return;
		}

		for (EnumFacing side : EnumFacing.values()) {

			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();

			if (!mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
				continue;
			}

			Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));

			Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
			if (blacklist.contains(neighborPos) || shulkerList.contains(neighborPos)) {
				mc.player.connection
						.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
				isSneaking = true;
			}

			faceVectorPacketInstant(hitVec);
			mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec,
					EnumHand.MAIN_HAND);
			mc.player.swingArm(EnumHand.MAIN_HAND);

			((cat.yoink.dream.mixin.mixins.accessor.IMinecraft) mc).setRightClickDelayTimer(4);
			// mc.rightClickDelayTimer = 4;

			return;

		}

	}

	private float[] getLegitRotations(Vec3d vec) {
		Vec3d eyesPos = getEyesPos();

		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new float[] { mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
				mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
	}

	private Vec3d getEyesPos() {
		return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
	}

	public void faceVectorPacketInstant(Vec3d vec) {
		float[] rotations = getLegitRotations(vec);

		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
	}

	public boolean checkForNeighbours(BlockPos blockPos) {
		// check if we don't have a block adjacent to blockpos
		if (!hasNeighbour(blockPos)) {
			// find air adjacent to blockpos that does have a block adjacent to it, let's
			// fill this first as to form a bridge between the player and the original
			// blockpos. necessary if the player is going diagonal.
			for (EnumFacing side : EnumFacing.values()) {
				BlockPos neighbour = blockPos.offset(side);
				if (hasNeighbour(neighbour)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public boolean hasNeighbour(BlockPos blockPos) {
		for (EnumFacing side : EnumFacing.values()) {
			BlockPos neighbour = blockPos.offset(side);
			if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
				return true;
			}
		}
		return false;
	}

}
