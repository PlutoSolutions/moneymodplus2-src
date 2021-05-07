package cat.yoink.dream.impl.module.hud;

import java.awt.Color;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Compass extends Module {

	private final Setting Rainbow = new Setting.Builder(SettingType.BOOLEAN).setName("Rainbow").setModule(this)
			.setBooleanValue(false).build();

	Color c;

	private Colors colors = new Colors();

	public Compass(String name, String description, Category category) {
		super(name, description, category);
		addSetting(Rainbow);
		this.resolution = new ScaledResolution(mc);
	}

	ScaledResolution resolution;

	private double getX(final double rad) {
		return Math.sin(rad) * (3 * 10.0);
	}

	private double getY(final double rad) {
		final double epicPitch = MathHelper.clamp(mc.getRenderViewEntity().rotationPitch + 30.0f, -90.0f, 90.0f);
		final double pitchRadians = Math.toRadians(epicPitch);
		return Math.cos(rad) * Math.sin(pitchRadians) * (3 * 10.0);
	}

	private double getPosOnCompass(final Direction dir) {
		final double yaw = Math.toRadians(MathHelper.wrapDegrees(mc.getRenderViewEntity().rotationYaw));
		final int index = dir.ordinal();
		return yaw + index * 1.5707963267948966;
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent egoe) {
		if (egoe.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			final double centerX = this.resolution.getScaledWidth() * 1.11;
			final double centerY = this.resolution.getScaledHeight_double() * 1.8;
			for (final Direction dir : Direction.values()) {
				final double rad = getPosOnCompass(dir);
				if (Rainbow.getBooleanValue()) {
					renderStringWave(dir.name(), (int) (centerX + this.getX(rad)), (int) (centerY + this.getY(rad)),
							5 / 10f);
				} else {
					FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
					fr.drawStringWithShadow(dir.name(), (int) (centerX + this.getX(rad)),
							(int) (centerY + this.getY(rad)), (dir == Direction.N) ? new Color(255, 0, 0, 255).getRGB()
									: new Color(255, 255, 255, 255).getRGB());
				}
				// FontUtils.drawStringWithShadow(ClickGuiModule.customFont.getValue(),
				// dir.name(), (int)(centerX + this.getX(rad)), (int)(centerY + this.getY(rad)),
				// (dir == Direction.N) ? new Color(255, 0, 0, 255).getRGB() : new Color(255,
				// 255, 255, 255).getRGB());
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

	private enum Direction {
		N, W, S, E;
	}

}
