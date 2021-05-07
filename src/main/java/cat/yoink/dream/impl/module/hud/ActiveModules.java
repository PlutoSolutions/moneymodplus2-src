package cat.yoink.dream.impl.module.hud;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.Colors;
import cat.yoink.dream.api.util.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ActiveModules extends Module {
	private final Setting ForgeHax = new Setting.Builder(SettingType.BOOLEAN).setName("ForgeHax").setModule(this)
			.setBooleanValue(false).build();

	private final Setting Rainbow = new Setting.Builder(SettingType.BOOLEAN).setName("Rainbow").setModule(this)
			.setBooleanValue(false).build();

	private final Setting RainbowSat = new Setting.Builder(SettingType.INTEGER).setName("Rainbow Sat").setModule(this)
			.setIntegerValue(6).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting red = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(210).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting green = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(30).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting blue = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(30).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting posX = new Setting.Builder(SettingType.INTEGER).setName("X").setModule(this)
			.setIntegerValue(2).setMinIntegerValue(-7).setMaxIntegerValue(900).build();

	private final Setting posY = new Setting.Builder(SettingType.INTEGER).setName("Y").setModule(this)
			.setIntegerValue(2).setMinIntegerValue(-3).setMaxIntegerValue(540).build();

	Color c;

	private Colors colors = new Colors();

	public ActiveModules(String name, String description, Category category) {
		super(name, description, category);
		addSetting(ForgeHax);
		addSetting(Rainbow);
		addSetting(RainbowSat);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(posX);
		addSetting(posY);
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

	public void cycle_rainbow() {

		float[] tick_color = { (System.currentTimeMillis() % (360 * 32)) / (360f * 32) };

		int color_rgb_o = Color.HSBtoRGB(tick_color[0], 1, 1);

		money.settingManager.getSetting("ActiveModules", "Red").setIntegerValue((color_rgb_o >> 16) & 0xFF);
		money.settingManager.getSetting("ActiveModules", "Green").setIntegerValue((color_rgb_o >> 8) & 0xFF);
		money.settingManager.getSetting("ActiveModules", "Blue").setIntegerValue(color_rgb_o & 0xFF);

	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent egoe) {
		if (egoe.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			c = new Color((int) red.getIntegerValue(), (int) green.getIntegerValue(), (int) blue.getIntegerValue());
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			int y = 2;

			ArrayList<String> list = new ArrayList<String>();

			for (Module mod : ModuleManager.getModules()) {
				if (mod.isEnabled()) {
					list.add(mod.getName());
				}
			}

			list.sort((String s1, String s2) -> Minecraft.getMinecraft().fontRenderer.getStringWidth(s1)
					- Minecraft.getMinecraft().fontRenderer.getStringWidth(s2));
			Collections.reverse(list);

			for (String name : list) {
				if (Rainbow.getBooleanValue()) {
					cycle_rainbow();
				}
				if (ForgeHax.getBooleanValue()) {
					FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
					fr.drawStringWithShadow(name + "<",
							sr.getScaledWidth() - fr.getStringWidth(name) - 6 - posX.getIntegerValue(),
							y + posY.getIntegerValue(),
							new Color(red.getIntegerValue(), green.getIntegerValue(), blue.getIntegerValue(), 255)
									.getRGB());
					y += fr.FONT_HEIGHT;
				} else {
					FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
					fr.drawStringWithShadow(name,
							sr.getScaledWidth() - fr.getStringWidth(name) - 1 - posX.getIntegerValue(),
							y + posY.getIntegerValue(),
							new Color(red.getIntegerValue(), green.getIntegerValue(), blue.getIntegerValue(), 255)
									.getRGB());
					y += fr.FONT_HEIGHT;
				}
			}
		}
	}
}
