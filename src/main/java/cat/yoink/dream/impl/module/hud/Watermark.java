package cat.yoink.dream.impl.module.hud;

import java.awt.Color;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Watermark extends Module {

	private final Setting ver = new Setting.Builder(SettingType.BOOLEAN).setName("Version").setModule(this)
			.setBooleanValue(true).build();

	private final Setting rainbow = new Setting.Builder(SettingType.BOOLEAN).setName("Rainbow").setModule(this)
			.setBooleanValue(false).build();

	private final Setting sat = new Setting.Builder(SettingType.INTEGER).setName("RainbowSat").setModule(this)
			.setIntegerValue(6).setMinIntegerValue(1).setMaxIntegerValue(9).build();

	private final Setting red = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(210).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting green = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(30).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting blue = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(30).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting x = new Setting.Builder(SettingType.INTEGER).setName("X").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(-8).setMaxIntegerValue(900).build();

	private final Setting y = new Setting.Builder(SettingType.INTEGER).setName("Y").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(-4).setMaxIntegerValue(540).build();

	Color c;

	private Colors colors = new Colors();

	public Watermark(String name, String description, Category category) {
		super(name, description, category);

		addSetting(ver);
		addSetting(rainbow);
		addSetting(sat);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(x);
		addSetting(y);
	}

	public void renderStringWave(String s, int x, int y, float bright) {
		int updateX = x;
		for (int i = 0; i < s.length(); i++) {
			String str = s.charAt(i) + "";
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(str, updateX, y,
					colors.effect(i * 3500000L, bright, 100).getRGB());
			updateX += Minecraft.getMinecraft().fontRenderer.getCharWidth(s.charAt(i));
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent egoe) {
		if (egoe.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			String Watermark;
			if (ver.getBooleanValue()) {
				Watermark = money.MODNAME + " " + money.MODVER;
			} else {
				Watermark = money.MODNAME;
			}
//			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			if (rainbow.getBooleanValue()) {
				renderStringWave(Watermark, x.getIntegerValue(), y.getIntegerValue(), sat.getIntegerValue() / 10f);
			} else {
				c = new Color((int) red.getIntegerValue(), (int) green.getIntegerValue(), (int) blue.getIntegerValue());
				FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
				fr.drawStringWithShadow(Watermark, x.getIntegerValue(), y.getIntegerValue(), c.getRGB());
			}
		}
	}
}
