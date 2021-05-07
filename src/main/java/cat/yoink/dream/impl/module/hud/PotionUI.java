package cat.yoink.dream.impl.module.hud;

import java.awt.Color;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.realmsclient.gui.ChatFormatting;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PotionUI extends Module {

	DecimalFormat format2;
	int count;
	Color c;

	private Colors colors = new Colors();

	private final Setting x = new Setting.Builder(SettingType.INTEGER).setName("X").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(-8).setMaxIntegerValue(900).build();

	private final Setting y = new Setting.Builder(SettingType.INTEGER).setName("Y").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(0).setMaxIntegerValue(540).build();

	private final Setting sortUp = new Setting.Builder(SettingType.BOOLEAN).setName("SortUp").setModule(this)
			.setBooleanValue(true).build();

	private final Setting right = new Setting.Builder(SettingType.BOOLEAN).setName("Right").setModule(this)
			.setBooleanValue(false).build();

	public PotionUI(String name, String description, Category category) {
		super(name, description, category);
		addSetting(x);
		addSetting(y);
		addSetting(sortUp);
		addSetting(right);
	}

	public void renderStringWave(String s, int x, int y, float bright) {
		int updateX = x;
		for (int i = 0; i < s.length(); i++) {
			String str = s.charAt(i) + "";
			if (!str.equalsIgnoreCase("1") && !str.equalsIgnoreCase("[") && !str.equalsIgnoreCase("]")
					&& !str.equalsIgnoreCase("2") && !str.equalsIgnoreCase("3") && !str.equalsIgnoreCase("4")
					&& !str.equalsIgnoreCase("5") && !str.equalsIgnoreCase("6") && !str.equalsIgnoreCase("7")
					&& !str.equalsIgnoreCase("8") && !str.equalsIgnoreCase("9") && !str.equalsIgnoreCase("0")
					&& !str.equalsIgnoreCase(":")) {
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(str, updateX, y,
						colors.effect(i * 750000L, bright, 50).getRGB());
				updateX += Minecraft.getMinecraft().fontRenderer.getCharWidth(s.charAt(i));
			} else {
				c = new Color(165, 165, 165);
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(str, updateX, y, c.getRGB());
				updateX += Minecraft.getMinecraft().fontRenderer.getCharWidth(s.charAt(i));
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent egoe) {
		if (egoe.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			count = 0;
			try {
				mc.player.getActivePotionEffects().forEach(effect -> {
					String name;
					double duration;
					int amplifier;
					double p1;
					String seconds;
					String s;
					name = I18n.format(effect.getPotion().getName(), new Object[0]);
					duration = effect.getDuration() / 19.99f;
					int duration2 = (int) (duration / 60);
					amplifier = effect.getAmplifier() + 1;
					p1 = duration % 60.0;
					seconds = format2.format(p1);
					s = name + " " + amplifier + " " + duration2 + ":" + (String) seconds;
					if (sortUp.getBooleanValue()) {
						if (right.getBooleanValue()) {
							renderStringWave(s, x.getIntegerValue() - getWidth(s), y.getIntegerValue() + count, .6f);
						} else {
							renderStringWave(s, x.getIntegerValue(), y.getIntegerValue() + count, .6f);
						}
						++count;
					} else {
						if (right.getBooleanValue()) {
							renderStringWave(s, x.getIntegerValue() - getWidth(s), y.getIntegerValue() + count, .6f);
						} else {
							renderStringWave(s, x.getIntegerValue(), y.getIntegerValue() + count, .6f);
						}
						++count;
					}
				});
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	private int getWidth(final String s) {
		return mc.fontRenderer.getStringWidth(s);
	}
}
