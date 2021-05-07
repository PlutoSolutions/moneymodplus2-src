package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoLog extends Module {

	private final Setting health = new Setting.Builder(SettingType.INTEGER).setName("MinHealth").setModule(this)
			.setIntegerValue(8).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	private final Setting disableOnLog = new Setting.Builder(SettingType.BOOLEAN).setName("Disable On Log")
			.setModule(this).setBooleanValue(true).build();

	public AutoLog(String name, String description, Category category) {
		super(name, description, category);
		addSetting(health);
		addSetting(disableOnLog);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null)
			return;
		if (mc.player.getHealth() < health.getIntegerValue()) {
			mc.world.sendQuittingDisconnectingPacket();
			mc.displayGuiScreen(new GuiMainMenu());
			if (disableOnLog.getBooleanValue()) {
				this.disable();
			}
		}
	}

}
