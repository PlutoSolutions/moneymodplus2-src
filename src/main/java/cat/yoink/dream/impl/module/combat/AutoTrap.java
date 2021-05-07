package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoTrap extends Module {
	private final Setting blocksPerTick = new Setting.Builder(SettingType.INTEGER).setName("BPT").setModule(this)
			.setIntegerValue(1).setMinIntegerValue(1).setMaxIntegerValue(10).build();

	private final Setting disable = new Setting.Builder(SettingType.BOOLEAN).setName("Disable").setModule(this)
			.setBooleanValue(true).build();

	private final List<Vec3d> positions = new ArrayList<>(Arrays.asList(new Vec3d(0, -1, -1), new Vec3d(1, -1, 0),
			new Vec3d(0, -1, 1), new Vec3d(-1, -1, 0), new Vec3d(0, 0, -1), new Vec3d(1, 0, 0), new Vec3d(0, 0, 1),
			new Vec3d(-1, 0, 0), new Vec3d(0, 1, -1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 1), new Vec3d(-1, 1, 0),
			new Vec3d(0, 2, -1), new Vec3d(0, 2, 1), new Vec3d(0, 2, 0)));

	private boolean finished;

	public AutoTrap(String name, String description, Category category) {
		super(name, description, category);

		addSetting(blocksPerTick);
		addSetting(disable);
	}

	public static boolean isIntercepted(BlockPos pos) {
		for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
			if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox()))
				return true;
		}

		return false;
	}

	public static int getSlot(Block block) {
		for (int i = 0; i < 9; i++) {
			Item item = Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem();

			if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(block)) {
				return i;
			}
		}
		return -1;
	}

	public static void placeBlock(BlockPos pos) {
		for (EnumFacing enumFacing : EnumFacing.values()) {
			if (!Minecraft.getMinecraft().world.getBlockState(pos.offset(enumFacing)).getBlock().equals(Blocks.AIR)
					&& !isIntercepted(pos)) {
				Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) enumFacing.getXOffset() * 0.5D,
						pos.getY() + 0.5D + (double) enumFacing.getYOffset() * 0.5D,
						pos.getZ() + 0.5D + (double) enumFacing.getZOffset() * 0.5D);

				float[] old = new float[] { Minecraft.getMinecraft().player.rotationYaw,
						Minecraft.getMinecraft().player.rotationPitch };

				Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.Rotation(
						(float) Math.toDegrees(Math.atan2((vec.z - Minecraft.getMinecraft().player.posZ),
								(vec.x - Minecraft.getMinecraft().player.posX))) - 90.0F,
						(float) (-Math.toDegrees(Math.atan2(
								(vec.y - (Minecraft.getMinecraft().player.posY
										+ (double) Minecraft.getMinecraft().player.getEyeHeight())),
								(Math.sqrt((vec.x - Minecraft.getMinecraft().player.posX)
										* (vec.x - Minecraft.getMinecraft().player.posX)
										+ (vec.z - Minecraft.getMinecraft().player.posZ)
												* (vec.z - Minecraft.getMinecraft().player.posZ)))))),
						Minecraft.getMinecraft().player.onGround));
				Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(
						Minecraft.getMinecraft().player, CPacketEntityAction.Action.START_SNEAKING));
				Minecraft.getMinecraft().playerController.processRightClickBlock(Minecraft.getMinecraft().player,
						Minecraft.getMinecraft().world, pos.offset(enumFacing), enumFacing.getOpposite(),
						new Vec3d(pos), EnumHand.MAIN_HAND);
				Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
				Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(
						Minecraft.getMinecraft().player, CPacketEntityAction.Action.STOP_SNEAKING));
				Minecraft.getMinecraft().player.connection.sendPacket(
						new CPacketPlayer.Rotation(old[0], old[1], Minecraft.getMinecraft().player.onGround));

				return;
			}
		}
	}

	@Override
	public void onEnable() {
		finished = false;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null)
			return;

		if (finished && disable.getBooleanValue())
			disable();

		int blocksPlaced = 0;

		for (Vec3d position : positions) {
			EntityPlayer closestPlayer = getClosestPlayer();
			if (closestPlayer != null) {
				BlockPos pos = new BlockPos(position.add(getClosestPlayer().getPositionVector()));

				if (Minecraft.getMinecraft().world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
					int oldSlot = Minecraft.getMinecraft().player.inventory.currentItem;
					Minecraft.getMinecraft().player.inventory.currentItem = getSlot(Blocks.OBSIDIAN);
					placeBlock(pos);
					Minecraft.getMinecraft().player.inventory.currentItem = oldSlot;
					blocksPlaced++;

					if (blocksPlaced == blocksPerTick.getIntegerValue())
						return;
				}
			}
		}
		if (blocksPlaced == 0)
			finished = true;
	}

	private EntityPlayer getClosestPlayer() {
		EntityPlayer closestPlayer = null;
		double range = 1000;
		for (EntityPlayer playerEntity : Minecraft.getMinecraft().world.playerEntities) {
			if (!playerEntity.equals(Minecraft.getMinecraft().player)) {
				double distance = Minecraft.getMinecraft().player.getDistance(playerEntity);
				if (distance < range) {
					closestPlayer = playerEntity;
					range = distance;
				}
			}
		}
		return closestPlayer;
	}
}
