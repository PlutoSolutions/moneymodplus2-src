package cat.yoink.dream.impl.module.misc;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.mixin.mixins.accessor.IMinecraft;
import cat.yoink.dream.mixin.mixins.accessor.ITimer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class Timer extends Module {
	private final Setting speed = new Setting.Builder(SettingType.INTEGER).setName("Speed").setModule(this)
			.setIntegerValue(20).setMinIntegerValue(1).setMaxIntegerValue(300).build();

	public Timer(String name, String description, Category category) {
		super(name, description, category);

		addSetting(speed);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50f / (speed.getIntegerValue() / 10f));
	}

	@Override
	public void onDisable() {
		((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50f);
	}
}