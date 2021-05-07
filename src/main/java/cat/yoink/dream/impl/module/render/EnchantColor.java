package cat.yoink.dream.impl.module.render;

import java.awt.Color;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;

public class EnchantColor extends Module {

	public final Setting r = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(120).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public final Setting g = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(120).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public final Setting b = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(120).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public final Setting rainbow = new Setting.Builder(SettingType.BOOLEAN).setName("Rainbow").setModule(this)
			.setBooleanValue(false).build();

	public EnchantColor(String name, String description, Category category) {
		super(name, description, category);
		addSetting(r);
		addSetting(g);
		addSetting(b);
		addSetting(rainbow);
		// TODO Auto-generated constructor stub
	}

	public static Color getColor(final long offset, final float fade) {
		if (((EnchantColor) ModuleManager.getModule("EnchantColor")).rainbow.getBooleanValue() == false) {
			// if
			// (Xulu.MODULE_MANAGER.getModuleT(EnchantColor.class).mode.getValue().equalsIgnoreCase("Color"))
			// {
			return new Color(((EnchantColor) ModuleManager.getModule("EnchantColor")).r.getIntegerValue() / 255,
					((EnchantColor) ModuleManager.getModule("EnchantColor")).g.getIntegerValue() / 255,
					((EnchantColor) ModuleManager.getModule("EnchantColor")).b.getIntegerValue() / 255);
		}
		final float hue = (System.nanoTime() + offset) / 1.0E10f % 1.0f;
		final long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
		final Color c = new Color((int) color);
		return new Color(c.getRed() / 255.0f * fade, c.getGreen() / 255.0f * fade, c.getBlue() / 255.0f * fade,
				c.getAlpha() / 255.0f);
	}
}
