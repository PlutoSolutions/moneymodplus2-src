package cat.yoink.dream.impl.module.hud;

import java.awt.Color;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PvPInfo extends Module {

	private final Setting x = new Setting.Builder(SettingType.INTEGER).setName("X").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(-8).setMaxIntegerValue(900).build();

	private final Setting y = new Setting.Builder(SettingType.INTEGER).setName("Y").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(-4).setMaxIntegerValue(540).build();

	public PvPInfo(String name, String description, Category category) {
		super(name, description, category);
		addSetting(x);
		addSetting(y);
		// TODO Auto-generated constructor stub
	}

	Color c;

	private Colors colors = new Colors();

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent egoe) {
		if (egoe.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			if (mc.player == null || mc.world == null)
				return;

			if (ModuleManager.getModule("AutoCrystal").isEnabled()) {
				renderStringWave("CA: ON", x.getIntegerValue(), y.getIntegerValue() + 10, 0.6f);
			} else {
				renderStringWave("CA: OFF", x.getIntegerValue(), y.getIntegerValue() + 10, 0.6f);
			}
			if (ModuleManager.getModule("KillAura").isEnabled()) {
				renderStringWave("KA: ON", x.getIntegerValue(), y.getIntegerValue() + 20, 0.6f);
			} else {
				renderStringWave("KA: OFF", x.getIntegerValue(), y.getIntegerValue() + 20, 0.6f);
			}
			if (ModuleManager.getModule("Surround").isEnabled()) {
				renderStringWave("S:  ON", x.getIntegerValue(), y.getIntegerValue() + 30, 0.6f);
			} else {
				renderStringWave("S:  OFF", x.getIntegerValue(), y.getIntegerValue() + 30, 0.6f);
			}
			if (ModuleManager.getModule("AutoTrap").isEnabled()) {
				renderStringWave("AT: ON", x.getIntegerValue(), y.getIntegerValue() + 40, 0.6f);
			} else {
				renderStringWave("AT: OFF", x.getIntegerValue(), y.getIntegerValue() + 40, 0.6f);
			}
			if (ModuleManager.getModule("SelfTrap").isEnabled()) {
				renderStringWave("ST: ON", x.getIntegerValue(), y.getIntegerValue() + 50, 0.6f);
			} else {
				renderStringWave("ST: OFF", x.getIntegerValue(), y.getIntegerValue() + 50, 0.6f);
			}
			if (ModuleManager.getModule("Strafe").isEnabled()) {
				renderStringWave("SF: ON", x.getIntegerValue(), y.getIntegerValue() + 60, 0.6f);
			} else {
				renderStringWave("SF: OFF", x.getIntegerValue(), y.getIntegerValue() + 60, 0.6f);
			}
			if (ModuleManager.getModule("YPort").isEnabled()) {
				renderStringWave("YP: ON", x.getIntegerValue(), y.getIntegerValue() + 70, 0.6f);
			} else {
				renderStringWave("YP: OFF", x.getIntegerValue(), y.getIntegerValue() + 70, 0.6f);
			}
		}
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

}
