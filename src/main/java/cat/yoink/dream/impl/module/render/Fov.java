package cat.yoink.dream.impl.module.render;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class Fov extends Module {
	private final Setting fovSlider = new Setting.Builder(SettingType.INTEGER).setName("Fov").setModule(this)
			.setIntegerValue(120).setMinIntegerValue(0).setMaxIntegerValue(180).build();

	public float defaultFov;

	public Fov(String name, String description, Category category) {
		super(name, description, category);
		addSetting(fovSlider);
	}

	@SubscribeEvent
	public void fovOn(final EntityViewRenderEvent.FOVModifier e) {
		e.setFOV((float) this.fovSlider.getIntegerValue());
	}

	public void onEnable() {
		MinecraftForge.EVENT_BUS.register((Object) this);
		this.defaultFov = mc.gameSettings.fovSetting;
	}

	public void onDisable() {
		MinecraftForge.EVENT_BUS.unregister((Object) this);
		mc.gameSettings.fovSetting = this.defaultFov;
	}
}
