package cat.yoink.dream.impl.module.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import cat.yoink.dream.api.util.BlockUtils;
import cat.yoink.dream.mixin.mixins.accessor.IMinecraft;

public class SelfTrap extends Module {

	private final Setting rotate = new Setting.Builder(SettingType.BOOLEAN).setName("Rotate").setModule(this)
			.setBooleanValue(true).build();

	private final Setting bps = new Setting.Builder(SettingType.INTEGER).setName("BPS").setModule(this)
			.setIntegerValue(5).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting delay = new Setting.Builder(SettingType.INTEGER).setName("Delay").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting toggle = new Setting.Builder(SettingType.BOOLEAN).setName("Toggle").setModule(this)
			.setBooleanValue(true).build();

	private EntityPlayer closestTarget;
	private String lastTickTargetName;
	private int playerHotbarSlot = -1;
	private int lastHotbarSlot = -1;
	private int delayStep = 0;
	private boolean isSneaking = false;
	private int offsetStep = 0;
	private boolean firstRun;

	public SelfTrap(String name, String description, Category category) {
		super(name, description, category);
		addSetting(rotate);
		addSetting(bps);
		addSetting(delay);
		addSetting(toggle);
	}

	public void onEnable() {

		if (mc.player == null) {
			this.disable();
			return;
		}
		firstRun = true;
		playerHotbarSlot = mc.player.inventory.currentItem;
		lastHotbarSlot = -1;
	}

	@Override
	public void onDisable() {

		if (mc.player == null) {
			return;
		}

		if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
			mc.player.inventory.currentItem = playerHotbarSlot;
		}

		if (isSneaking) {
			mc.player.connection
					.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			isSneaking = false;
		}

		playerHotbarSlot = -1;
		lastHotbarSlot = -1;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {

		if (mc.player == null) {
			return;
		}

		if (!firstRun) {
			if (delayStep < delay.getIntegerValue()) {
				delayStep++;
				return;
			} else {
				delayStep = 0;
			}
		}

		findClosestTarget();

		if (closestTarget == null) {
			if (firstRun) {
				firstRun = false;
			}
			return;
		}

		if (firstRun) {
			firstRun = false;
			lastTickTargetName = closestTarget.getName();

		} else if (!lastTickTargetName.equals(closestTarget.getName())) {
			lastTickTargetName = closestTarget.getName();
			offsetStep = 0;
		}

		List<Vec3d> placeTargets = new ArrayList<>();
		Collections.addAll(placeTargets, SelfTrap.Offsets.TRAPSIMPLE);

		int blocksPlaced = 0;

		while (blocksPlaced < bps.getIntegerValue()) {

			if (offsetStep >= placeTargets.size()) {
				offsetStep = 0;
				break;
			}

			BlockPos offsetPos = new BlockPos(placeTargets.get(offsetStep));
			BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).down().add(offsetPos.getX(),
					offsetPos.getY(), offsetPos.getZ());

			if (placeBlockInRange(targetPos)) {
				blocksPlaced++;
			}

			offsetStep++;
		}

		if (blocksPlaced > 0) {

			if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
				mc.player.inventory.currentItem = playerHotbarSlot;
				lastHotbarSlot = playerHotbarSlot;
			}

			if (isSneaking) {
				mc.player.connection
						.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
				isSneaking = false;
			}
		}

		if (toggle.getBooleanValue())
			this.disable();
	}

	private boolean placeBlockInRange(BlockPos pos) {

		// check if block is already placed
		Block block = mc.world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
			return false;
		}

		// check if entity blocks placing
		for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
			if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
				return false;
			}
		}

		EnumFacing side = BlockUtils.getPlaceableSide(pos);

		// check if we have a block adjacent to blockpos to click at
		if (side == null) {
			return false;
		}

		BlockPos neighbour = pos.offset(side);
		EnumFacing opposite = side.getOpposite();

		// check if neighbor can be right clicked
		if (!BlockUtils.canBeClicked(neighbour)) {
			return false;
		}

		Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
		Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

		int obiSlot = findObiInHotbar();

		if (obiSlot == -1) {
			this.disable();
		}

		if (lastHotbarSlot != obiSlot) {
			mc.player.inventory.currentItem = obiSlot;
			lastHotbarSlot = obiSlot;
		}

		if (!isSneaking && BlockUtils.blackList.contains(neighbourBlock)
				|| BlockUtils.shulkerList.contains(neighbourBlock)) {
			mc.player.connection
					.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			isSneaking = true;
		}

		if (rotate.getBooleanValue()) {
			BlockUtils.faceVectorPacketInstant(hitVec);
		}

		mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec,
				EnumHand.MAIN_HAND);
		mc.player.swingArm(EnumHand.MAIN_HAND);
		((IMinecraft) mc).setRightClickDelayTimer(4);

		return true;
	}

	private int findObiInHotbar() {

		// search blocks in hotbar
		int slot = -1;
		for (int i = 0; i < 9; i++) {

			// filter out non-block items
			ItemStack stack = mc.player.inventory.getStackInSlot(i);

			if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
				continue;
			}

			Block block = ((ItemBlock) stack.getItem()).getBlock();
			if (block instanceof BlockObsidian) {
				slot = i;
				break;
			}

		}
		return slot;
	}

	private void findClosestTarget() {
		List<EntityPlayer> playerList = mc.world.playerEntities;
		closestTarget = null;

		for (EntityPlayer target : playerList) {

			if (target == mc.player) {
				closestTarget = target;
			}
		}
	}

	private static class Offsets {

		private static final Vec3d[] TRAPSIMPLE = { new Vec3d(-1, 0, 0), new Vec3d(1, 0, 0), new Vec3d(0, 0, -1),
				new Vec3d(0, 0, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, -1), new Vec3d(0, 1, 1), new Vec3d(-1, 1, 0),
				new Vec3d(-1, 2, 0), new Vec3d(-1, 3, 0), new Vec3d(0, 3, 0) };
	}

}
