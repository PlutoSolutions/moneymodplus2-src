package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.InteractUtil;
import cat.yoink.dream.api.util.TimerUtil;
import cat.yoink.dream.mixin.mixins.accessor.IMinecraft;

import java.util.Iterator;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Burrow extends Module {

	private final Setting rotate = new Setting.Builder(SettingType.BOOLEAN).setName("Rotate").setModule(this)
			.setBooleanValue(true).build();

	private final Setting delay = new Setting.Builder(SettingType.INTEGER).setName("Place Delay").setModule(this)
			.setIntegerValue(3).setMinIntegerValue(0).setMaxIntegerValue(20).build();

	int lastHotbarSlot;

	int playerHotbarSlot;

	boolean isSneaking;

	TimerUtil timer = new TimerUtil();

	public Burrow(String name, String description, Category category) {
		super(name, description, category);
		addSetting(rotate);
		addSetting(delay);
	}

	public void onEnable() {
		if (mc.player == null) {
			this.disable();
		} else {
			this.playerHotbarSlot = mc.player.inventory.currentItem;
			this.lastHotbarSlot = -1;
			mc.player.jump();
			this.timer.reset();
		}
	}

	public void onDisable() {
		if (mc.player != null) {
			if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
				mc.player.inventory.currentItem = this.playerHotbarSlot;
			}

			if (this.isSneaking) {
				Minecraft.getMinecraft().player.connection
						.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
				this.isSneaking = false;
			}

			this.playerHotbarSlot = -1;
			this.lastHotbarSlot = -1;
		}
	}

	private boolean placeBlock(BlockPos var1) {
		Block var2 = mc.world.getBlockState(var1).getBlock();
		if (!(var2 instanceof BlockAir) && !(var2 instanceof BlockLiquid)) {
			return false;
		} else {
			Iterator var3 = mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(var1))
					.iterator();

			while (var3.hasNext()) {
				Entity var4 = (Entity) var3.next();
				if (!(var4 instanceof EntityItem) && !(var4 instanceof EntityXPOrb)) {
					return false;
				}
			}

			EnumFacing var9 = InteractUtil.getPlaceableSide(var1);
			if (var9 == null) {
				return false;
			} else {
				BlockPos var10 = var1.offset(var9);
				EnumFacing var5 = var9.getOpposite();
				if (!InteractUtil.canBeClicked(var10)) {
					return false;
				} else {
					Vec3d var6 = (new Vec3d(var10)).add(0.5D, 0.5D, 0.5D)
							.add((new Vec3d(var5.getDirectionVec())).scale(0.5D));
					Block var7 = mc.world.getBlockState(var10).getBlock();
					int var8 = this.findObiInHotbar();
					if (var8 == -1) {
						this.disable();
					}

					if (this.lastHotbarSlot != var8) {
						mc.player.inventory.currentItem = var8;
						this.lastHotbarSlot = var8;
					}

					if (!this.isSneaking && InteractUtil.blackList.contains(var7)
							|| InteractUtil.shulkerList.contains(var7)) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
						this.isSneaking = true;
					}

					if ((Boolean) this.rotate.getBooleanValue()) {
						InteractUtil.faceVectorPacketInstant(var6);
					}

					mc.playerController.processRightClickBlock(mc.player, mc.world, var10, var5, var6,
							EnumHand.MAIN_HAND);
					mc.player.swingArm(EnumHand.MAIN_HAND);
					((IMinecraft) mc).setRightClickDelayTimer(4);
					return true;
				}
			}
		}
	}

	public int findObiInHotbar() {
		int var1 = -1;

		for (int var2 = 0; var2 < 9; ++var2) {
			ItemStack var3 = mc.player.inventory.getStackInSlot(var2);
			if (var3 != ItemStack.EMPTY && var3.getItem() instanceof ItemBlock) {
				Block var4 = ((ItemBlock) var3.getItem()).getBlock();
				if (var4 instanceof BlockObsidian) {
					var1 = var2;
					break;
				}
			}
		}
		return var1;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.isSingleplayer()) {
			String text = "[" + ChatFormatting.GREEN + "moneymod+2" + ChatFormatting.RESET + "]" + ChatFormatting.RED
					+ " DOESNT WORK IN SINGLEPLAYER!";
			mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(text), 5936);
			disable();
			return;
		}
		if (this.timer.hasReached(Long.valueOf(((this.delay.getIntegerValue()) * 50)))) {
			BlockPos var1 = new BlockPos(0, -1, 0);
			BlockPos var2 = (new BlockPos(mc.player.getPositionVector())).add(var1.getX(), var1.getY(), var1.getZ());
			if (this.placeBlock(var2)) {
				if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
					mc.player.inventory.currentItem = this.playerHotbarSlot;
					this.lastHotbarSlot = this.playerHotbarSlot;
				}

				if (this.isSneaking) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
					this.isSneaking = false;
				}

				mc.player.onGround = false;
				mc.player.motionY = 20.0D;
			}

			this.disable();
		}

	}
}
