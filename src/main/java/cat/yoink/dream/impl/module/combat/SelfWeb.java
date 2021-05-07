package cat.yoink.dream.impl.module.combat;

import java.util.concurrent.TimeUnit;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SelfWeb extends Module {

	BlockPos feet;
	int d;
	public static float yaw;
	public static float pitch;

	private final Setting delay = new Setting.Builder(SettingType.INTEGER).setName("Delay").setModule(this)
			.setIntegerValue(3).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting toggled = new Setting.Builder(SettingType.BOOLEAN).setName("Toggle").setModule(this)
			.setBooleanValue(true).build();

	public SelfWeb(String name, String description, Category category) {
		super(name, description, category);
		addSetting(delay);
		addSetting(toggled);
	}

	public boolean isInBlockRange(Entity target) {
		return (target.getDistance(mc.player) <= 4.0F);
	}

	public static boolean canBeClicked(BlockPos pos) {
		return Minecraft.getMinecraft().world.getBlockState(pos).getBlock()
				.canCollideCheck(Minecraft.getMinecraft().world.getBlockState(pos), false);
	}

	private boolean isStackObby(ItemStack stack) {
		return (stack != null && stack.getItem() == Item.getItemById(30));
	}

	private boolean doesHotbarHaveWeb() {
		for (int i = 36; i < 45; i++) {
			ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
			if (stack != null && isStackObby(stack)) {
				return true;
			}
		}
		return false;
	}

	public static Block getBlock(BlockPos pos) {
		return getState(pos).getBlock();
	}

	public static IBlockState getState(BlockPos pos) {
		return Minecraft.getMinecraft().world.getBlockState(pos);
	}

	public static boolean placeBlockLegit(BlockPos pos) {
		Vec3d eyesPos = new Vec3d(Minecraft.getMinecraft().player.posX,
				Minecraft.getMinecraft().player.posY + Minecraft.getMinecraft().player.getEyeHeight(),
				Minecraft.getMinecraft().player.posZ);
		Vec3d posVec = (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D);
		for (EnumFacing side : EnumFacing.values()) {
			BlockPos neighbor = pos.offset(side);
			if (canBeClicked(neighbor)) {
				Vec3d hitVec = posVec.add((new Vec3d(side.getDirectionVec())).scale(0.5D));
				if (eyesPos.squareDistanceTo(hitVec) <= 36.0D) {
					Minecraft.getMinecraft().playerController.processRightClickBlock(Minecraft.getMinecraft().player,
							Minecraft.getMinecraft().world, neighbor, side.getOpposite(), hitVec, EnumHand.MAIN_HAND);
					Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
					try {
						TimeUnit.MILLISECONDS.sleep(10L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return true;
				}
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null)
			return;
		if (mc.player.isHandActive()) {
			return;
		}
		trap(mc.player);
	}

	public static double roundToHalf(double d) {
		return Math.round(d * 2.0D) / 2.0D;
	}

	public void onEnable() {
		if (mc.player == null) {
			this.disable();
			return;
		}

		this.d = 0;
	}

	private void trap(EntityPlayer player) {
		if (player.moveForward == 0.0D && player.moveStrafing == 0.0D && player.moveForward == 0.0D) {
			this.d++;
		}
		if (player.moveForward != 0.0D || player.moveStrafing != 0.0D || player.moveForward != 0.0D) {
			this.d = 0;
		}
		if (!doesHotbarHaveWeb()) {
			this.d = 0;
		}
		if (this.d == this.delay.getIntegerValue() && doesHotbarHaveWeb()) {
			this.feet = new BlockPos(player.posX, player.posY, player.posZ);

			for (int i = 36; i < 45; i++) {
				ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
				if (stack != null && isStackObby(stack)) {
					int oldSlot = mc.player.inventory.currentItem;
					if (mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
						mc.player.inventory.currentItem = i - 36;
						if (mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
							placeBlockLegit(this.feet);
						}

						mc.player.inventory.currentItem = oldSlot;
						this.d = 0;
						if (toggled.getBooleanValue()) {
							toggle();
						}
						break;
					}
					this.d = 0;
				}
				this.d = 0;
			}
		}
	}

	public void onDisable() {
		this.d = 0;
		yaw = mc.player.rotationYaw;
		pitch = mc.player.rotationPitch;
	}

	public EnumFacing getEnumFacing(float posX, float posY, float posZ) {
		return EnumFacing.getFacingFromVector(posX, posY, posZ);
	}

	public BlockPos getBlockPos(double x, double y, double z) {
		return new BlockPos(x, y, z);
	}

}
