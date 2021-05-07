package cat.yoink.dream.impl.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NewOffhand extends Module {

	private boolean switching = false;
	private int last_slot;
	private Map<String, Item> itemMap;

	private final Setting item = new Setting.Builder(SettingType.ENUM).setName("Item").setModule(this)
			.setEnumValue("Crystal").setEnumValues(new ArrayList<>(Arrays.asList("Totem", "Crystal", "Gapple")))
			.build();

	private final Setting totemHp = new Setting.Builder(SettingType.INTEGER).setName("Totem HP").setModule(this)
			.setIntegerValue(16).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	private final Setting gappleInHole = new Setting.Builder(SettingType.BOOLEAN).setName("Gap In Hole").setModule(this)
			.setBooleanValue(false).build();

	private final Setting gappleInHoleHP = new Setting.Builder(SettingType.INTEGER).setName("Gap Hole HP")
			.setModule(this).setIntegerValue(16).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	private final Setting delay = new Setting.Builder(SettingType.BOOLEAN).setName("Delay").setModule(this)
			.setBooleanValue(false).build();

	public NewOffhand(String name, String description, Category category) {
		super(name, description, category);
		addSetting(item);
		addSetting(totemHp);
		addSetting(gappleInHole);
		addSetting(gappleInHoleHP);
		addSetting(delay);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {

		if (mc.player == null)
			return;

		if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {

			if (switching) {
				swap_items(last_slot, 2);
				return;
			}

			float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();

			if (hp > totemHp.getIntegerValue()) {
				if (gappleInHole.getBooleanValue() && hp > gappleInHoleHP.getIntegerValue() && is_in_hole()) {
					swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.getBooleanValue() ? 1 : 0);
					return;
				}
				switch (item.getEnumValue()) {
				case "Crystal":
					swap_items(get_item_slot(Items.END_CRYSTAL), 0);
					break;
				case "Gapple":
					swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.getBooleanValue() ? 1 : 0);
					break;
				case "Totem":
					swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getBooleanValue() ? 1 : 0);
					break;
				}
//	                if (item.in("Totem")) {
//	                    swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getBooleanValue() ? 1 : 0);
//	                    return;
//	                }
//	                if (item.in("Gapple")) {
//	                    swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.getBooleanValue() ? 1 : 0);
//	                    return;
//	                }
//	                if (item.in("Crystal") && !ModuleManager.getModule("AutoCrystal").isEnabled()) {
//	                    swap_items(get_item_slot(Items.TOTEM_OF_UNDYING),0);
//	                    return;
//	                }
			} else {
				swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getBooleanValue() ? 1 : 0);
				return;
			}

			if (mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
				swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getBooleanValue() ? 1 : 0);
			}

		}

	}

	public void swap_items(int slot, int step) {
		if (slot == -1)
			return;
		if (step == 0) {
			mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
			mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
			mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
		}
		if (step == 1) {
			mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
			switching = true;
			last_slot = slot;
		}
		if (step == 2) {
			mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
			mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
			switching = false;
		}

		mc.playerController.updateController();
	}

	private boolean is_in_hole() {

		BlockPos player_block = GetLocalPlayerPosFloored();

		return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR;
	}

	private int get_item_slot(Item input) {
		if (input == mc.player.getHeldItemOffhand().getItem())
			return -1;
		for (int i = 36; i >= 0; i--) {
			final Item item = mc.player.inventory.getStackInSlot(i).getItem();
			if (item == input) {
				if (i < 9) {
					if (input == Items.GOLDEN_APPLE) {
						return -1;
					}
					i += 36;
				}
				return i;
			}
		}
		return -1;
	}

	public BlockPos GetLocalPlayerPosFloored() {
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}

}
