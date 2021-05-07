package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;

public class BedAura extends Module {

	private final Setting range = new Setting.Builder(SettingType.INTEGER).setName("Range").setModule(this)
			.setIntegerValue(4).setMinIntegerValue(0).setMaxIntegerValue(6).build();

	private final Setting rotate = new Setting.Builder(SettingType.BOOLEAN).setName("Rotate").setModule(this)
			.setBooleanValue(true).build();

	private final Setting dimensionCheck = new Setting.Builder(SettingType.BOOLEAN).setName("DimensionCheck")
			.setModule(this).setBooleanValue(true).build();

	private final Setting refill = new Setting.Builder(SettingType.BOOLEAN).setName("Hotbar Refill").setModule(this)
			.setBooleanValue(false).build();

	boolean moving = false;

	public BedAura(String name, String description, Category category) {
		super(name, description, category);
		addSetting(range);
		addSetting(rotate);
		addSetting(dimensionCheck);
		addSetting(refill);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null) {
			return;
		}

		if (refill.getBooleanValue()) {
			// search for empty hotbar slots
			int slot = -1;
			for (int i = 0; i < 9; i++) {
				if (mc.player.inventory.getStackInSlot(i) == ItemStack.EMPTY) {
					slot = i;
					break;
				}
			}

			if (moving && slot != -1) {
				mc.playerController.windowClick(0, slot + 36, 0, ClickType.PICKUP, mc.player);
				moving = false;
				slot = -1;
			}

			if (slot != -1 && !(mc.currentScreen instanceof GuiContainer)
					&& mc.player.inventory.getItemStack().isEmpty()) {
				// search for beds in inventory
				int t = -1;
				for (int i = 0; i < 45; i++) {
					if (mc.player.inventory.getStackInSlot(i).getItem() == Items.BED && i >= 9) {
						t = i;
						break;
					}
				}
				// click bed item
				if (t != -1) {
					mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, mc.player);
					moving = true;
				}
			}
		}

		mc.world.loadedTileEntityList.stream().filter(e -> e instanceof TileEntityBed)
				.filter(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()) <= range
						.getIntegerValue())
				.sorted(Comparator
						.comparing(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ())))
				.forEach(bed -> {

					if (dimensionCheck.getBooleanValue() && mc.player.dimension == 0)
						return;

//                    if(rotate.getValue()) BlockUtils.faceVectorPacketInstant(new Vec3d(bed.getPos().add(0.5, 0.5, 0.5)));
					mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bed.getPos(), EnumFacing.UP,
							EnumHand.MAIN_HAND, 0, 0, 0));

				});
	}
}
