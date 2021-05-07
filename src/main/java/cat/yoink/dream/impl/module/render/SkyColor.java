package cat.yoink.dream.impl.module.render;

import java.awt.Color;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkyColor extends Module {

	private final Setting red = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting green = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting blue = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting rainbow = new Setting.Builder(SettingType.BOOLEAN).setName("Rainbow").setModule(this)
			.setBooleanValue(false).build();

	public SkyColor(String name, String description, Category category) {
		super(name, description, category);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(rainbow);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogColorRender(EntityViewRenderEvent.FogColors event) {
		float[] hue = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0f };
		int rgb = Color.HSBtoRGB(hue[0], 1.0f, 1.0f);
		int r = rgb >> 16 & 255;
		int g = rgb >> 8 & 255;
		int b = rgb & 255;
		if (rainbow.getBooleanValue()) {
			event.setRed(r / 255f);
			event.setGreen(g / 255f);
			event.setBlue(b / 255f);
		} else {
			event.setRed(red.getIntegerValue() / 255f);
			event.setGreen(green.getIntegerValue() / 255f);
			event.setBlue(blue.getIntegerValue() / 255f);
		}
	}

	@SubscribeEvent
	public void fog(EntityViewRenderEvent.FogDensity event) {
		event.setDensity(0);
		event.setCanceled(true);
	}
}
