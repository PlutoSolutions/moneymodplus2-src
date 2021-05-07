package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoTotem extends Module {

	public AutoTotem(String name, String description, Category category) {
		super(name, description, category);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player != null && mc.world != null && !(mc.currentScreen instanceof GuiContainer)) {
			if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() == Items.TOTEM_OF_UNDYING) {
				return;
			}

			final int slot = this.getItemSlot();

			if (slot != -1) {
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP,
						mc.player);
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP,
						mc.player);
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP,
						mc.player);
				mc.playerController.updateController();
			}
		}
	}

	private int getItemSlot() {
		for (int i = 0; i < 36; i++) {
			final Item item = mc.player.inventory.getStackInSlot(i).getItem();
			if (item == Items.TOTEM_OF_UNDYING) {
				if (i < 9) {
					i += 36;
				}
				return i;
			}
		}
		return -1;
	}
}
