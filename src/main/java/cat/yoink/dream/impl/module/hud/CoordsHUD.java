package cat.yoink.dream.impl.module.hud;

import java.awt.Color;

import com.mojang.realmsclient.gui.ChatFormatting;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CoordsHUD extends Module {

	private final Setting direction = new Setting.Builder(SettingType.BOOLEAN).setName("Direction").setModule(this)
			.setBooleanValue(false).build();

	private final Setting XYZ = new Setting.Builder(SettingType.BOOLEAN).setName("XYZ string").setModule(this)
			.setBooleanValue(true).build();

	private final Setting sat = new Setting.Builder(SettingType.INTEGER).setName("RainbowSat").setModule(this)
			.setIntegerValue(6).setMinIntegerValue(1).setMaxIntegerValue(9).build();

	private final Setting x = new Setting.Builder(SettingType.INTEGER).setName("X").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(-7).setMaxIntegerValue(900).build();

	private final Setting y = new Setting.Builder(SettingType.INTEGER).setName("Y").setModule(this).setIntegerValue(2)
			.setMinIntegerValue(0).setMaxIntegerValue(540).build();

	Color c;

	private Colors colors = new Colors();

	public CoordsHUD(String name, String description, Category category) {
		super(name, description, category);
		addSetting(direction);
		addSetting(XYZ);
		addSetting(sat);
		addSetting(x);
		addSetting(y);
	}

	public void renderStringWave(String s, int x, int y, float bright) {
		int updateX = x;
		for (int i = 0; i < s.length(); i++) {
			String str = s.charAt(i) + "";
			if (!str.equalsIgnoreCase("X") && !str.equalsIgnoreCase("[") && !str.equalsIgnoreCase("]")
					&& !str.equalsIgnoreCase("Y") && !str.equalsIgnoreCase("Z")) {
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

	public void renderStringWave2(String s, int x, int y, float bright) {
		int updateX = x;
		for (int i = 0; i < s.length(); i++) {
			String str = s.charAt(i) + "";
			if (!str.equalsIgnoreCase("[") && !str.equalsIgnoreCase("]")) {
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
			if (mc.player.dimension == -1) {
				String cx = String.format("%.1f", mc.player.posX);
				String cy = String.format("%.1f", mc.player.posY);
				String cz = String.format("%.1f", mc.player.posZ);
				String cnetherx = String.format("%.1f", mc.player.posX * 8f);
				String cnetherz = String.format("%.1f", mc.player.posZ * 8f);
				String xyzstr = "";
				if (XYZ.getBooleanValue()) {
					xyzstr = "XYZ ";
				} else {
					xyzstr = "";
				}
				if (direction.getBooleanValue()) {
					renderStringWave2(this.getFacing() + " [" + this.getTowards() + "]", x.getIntegerValue(),
							y.getIntegerValue(), sat.getIntegerValue() / 10f);
				}
				renderStringWave(
						xyzstr + cx + ", " + cy + ", " + cz + " [" + cnetherx + ", " + cy + ", " + cnetherz + "]",
						x.getIntegerValue(), y.getIntegerValue() + 10, sat.getIntegerValue() / 10f);
			}

			else {
				String cx = String.format("%.1f", mc.player.posX);
				String cy = String.format("%.1f", mc.player.posY);
				String cz = String.format("%.1f", mc.player.posZ);
				String cnetherx = String.format("%.1f", mc.player.posX / 8f);
				String cnetherz = String.format("%.1f", mc.player.posZ / 8f);
				String xyzstr = "";
				if (XYZ.getBooleanValue()) {
					xyzstr = "XYZ ";
				} else {
					xyzstr = "";
				}
				if (direction.getBooleanValue()) {
					renderStringWave2(this.getFacing() + " [" + this.getTowards() + "]", x.getIntegerValue(),
							y.getIntegerValue(), sat.getIntegerValue() / 10f);
				}

				renderStringWave(
						xyzstr + cx + ", " + cy + ", " + cz + " [" + cnetherx + ", " + cy + ", " + cnetherz + "]",
						x.getIntegerValue(), y.getIntegerValue() + 10, sat.getIntegerValue() / 10f);
			}
		}
	}

	private String getFacing() {
		switch (MathHelper.floor((double) (mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
		case 0:
			return "South";
		case 1:
			return "South West";
		case 2:
			return "West";
		case 3:
			return "North West";
		case 4:
			return "North";
		case 5:
			return "North East";
		case 6:
			return "East";
		case 7:
			return "South East";
		}
		return "Invalid";
	}

	private String getTowards() {
		switch (MathHelper.floor((double) (mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
		case 0:
			return "+Z";
		case 1:
			return "-X +Z";
		case 2:
			return "-X";
		case 3:
			return "-X -Z";
		case 4:
			return "-Z";
		case 5:
			return "+X -Z";
		case 6:
			return "+X";
		case 7:
			return "+X +Z";
		}
		return "Invalid";
	}
}
