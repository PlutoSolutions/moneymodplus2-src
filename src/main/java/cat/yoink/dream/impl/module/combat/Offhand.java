package cat.yoink.dream.impl.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Offhand extends Module {

	private final Setting stopInGUI = new Setting.Builder(SettingType.BOOLEAN).setName("Stop In Gui").setModule(this)
			.setBooleanValue(true).build();

	private final Setting swordGap = new Setting.Builder(SettingType.BOOLEAN).setName("Sword Gap").setModule(this)
			.setBooleanValue(true).build();

	private final Setting soft = new Setting.Builder(SettingType.BOOLEAN).setName("Soft").setModule(this)
			.setBooleanValue(true).build();

	private final Setting minHealth = new Setting.Builder(SettingType.INTEGER).setName("Min Health").setModule(this)
			.setIntegerValue(16).setMinIntegerValue(1).setMaxIntegerValue(36).build();

	private final Setting delay = new Setting.Builder(SettingType.INTEGER).setName("Delay").setModule(this)
			.setIntegerValue(1).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting mode = new Setting.Builder(SettingType.ENUM).setName("Mode").setModule(this)
			.setEnumValue("Crystal").setEnumValues(new ArrayList<>(Arrays.asList("Crystal", "Gapple", "Bed", "Totem")))
			.build();

	private final Setting packetFix = new Setting.Builder(SettingType.BOOLEAN).setName("Packet Fix").setModule(this)
			.setBooleanValue(true).build();

	public Offhand(String name, String description, Category category) {
		super(name, description, category);
		addSetting(stopInGUI);
		addSetting(swordGap);
		addSetting(soft);
		addSetting(minHealth);
		addSetting(mode);
		addSetting(packetFix);
	}

	private int previtemSlot = -6;

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null || mc.world == null)
			return;
		if (stopInGUI.getBooleanValue() && mc.currentScreen != null)
			return;

		int itemSlot = getItemSlot();
		if (itemSlot == -1)
			return;

		if (itemSlot == previtemSlot)
			return;

		previtemSlot = itemSlot;
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, itemSlot, 0, ClickType.PICKUP,
				mc.player);
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, itemSlot, 0, ClickType.PICKUP,
				mc.player);
	}

	private int getItemSlot() {
		Item itemToSearch = Items.TOTEM_OF_UNDYING;

		if (!(mc.player.getHealth() + mc.player.getAbsorptionAmount() <= minHealth.getIntegerValue())) {
			if (swordGap.getBooleanValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
				itemToSearch = Items.GOLDEN_APPLE;
			} else {
				switch (mode.getEnumValue()) {
				case "Crystal":
					itemToSearch = Items.END_CRYSTAL;
					break;
				case "Gapple":
					itemToSearch = Items.GOLDEN_APPLE;
					break;
				case "Bed":
					itemToSearch = Items.BED;
					break;
				}
			}
		}

		if (mc.player.inventory.getStackInSlot(45).getItem() == itemToSearch)
			return -1;

		for (int i = 9; i < 36; i++) {
			if (mc.player.inventory.getStackInSlot(i).getItem() == itemToSearch) {
				return i < 9 ? i + 36 : i; // not needed but used as a safety measure.
			}
		}

		return -1;
	}

}
