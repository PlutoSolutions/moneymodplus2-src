package cat.yoink.dream.impl.module.movement;

import org.lwjgl.input.Keyboard;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class InventoryMove extends Module {

	public InventoryMove(String name, String description, Category category) {
		super(name, description, category);
		// TODO Auto-generated constructor stub
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc == null || mc.player == null) {
			return;
		}
		KeyBinding[] moveKeys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
				mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump };
		if (mc.currentScreen instanceof GuiContainer) {
			for (KeyBinding bind : moveKeys) {
				KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
			}
		} else if (mc.currentScreen == null) {
			for (KeyBinding bind : moveKeys) {
				if (!Keyboard.isKeyDown(bind.getKeyCode())) {
					KeyBinding.setKeyBindState(bind.getKeyCode(), false);
				}
			}
		}
		if (mc.currentScreen instanceof GuiContainer) {
			if (Keyboard.isKeyDown(Integer.valueOf(200).intValue())) {
				mc.player.rotationPitch -= 7.0F;
			}
			if (Keyboard.isKeyDown(Integer.valueOf(208).intValue())) {
				mc.player.rotationPitch += 7.0F;
			}
			if (Keyboard.isKeyDown(Integer.valueOf(205).intValue())) {
				mc.player.rotationYaw += 7.0F;
			}
			if (Keyboard.isKeyDown(Integer.valueOf(203).intValue())) {
				mc.player.rotationYaw -= 7.0F;
			}
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
				mc.player.setSprinting(true);
			}
		}
	}
}
