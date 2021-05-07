package cat.yoink.dream.impl.module.movement;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Step extends Module {

	private final Setting height = new Setting.Builder(SettingType.INTEGER).setName("Height").setModule(this)
			.setIntegerValue(2).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	public Step(String name, String description, Category category) {
		super(name, description, category);
		addSetting(height);
	}

	public void onDisable() {
		mc.player.stepHeight = 0.5f;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if ((mc.player != null || mc.world != null)) {
			mc.player.stepHeight = height.getIntegerValue();
		}
	}

}
