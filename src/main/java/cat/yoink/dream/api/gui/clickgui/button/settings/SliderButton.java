package cat.yoink.dream.api.gui.clickgui.button.settings;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.gui.clickgui.button.SettingButton;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.util.font.FontUtil;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class SliderButton extends SettingButton {
	private final Setting setting;
	protected boolean dragging;
	protected int sliderWidth;

	SliderButton(Module module, Setting setting, int X, int Y, int W, int H) {
		super(module, X, Y, W, H);
		this.dragging = false;
		this.sliderWidth = 0;
		this.setting = setting;
	}

	protected void updateSlider(int mouseX) {
	}

	@Override
	public void render(int mX, int mY) {
		int x = getX();
		int y = getY();
		int w = getW();
		int h = getH();

		updateSlider(mX);

		if (isHover(x, y, w, h - 1, mX, mY)) {
			money.clickGUI.drawGradient(x + (sliderWidth) + 6, y, x + w, y + h, new Color(25, 25, 25, 170).getRGB(),
					new Color(25, 25, 25, 170).getRGB());
			money.clickGUI.drawGradient(x, y, x + (sliderWidth) + 6, y + h,
					new Color(money.settingManager.getSetting("ClickGUI", "Red").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Green").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Blue").getIntegerValue(), 255).getRGB(),
					new Color(money.settingManager.getSetting("ClickGUI", "Red").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Green").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Blue").getIntegerValue(), 255).getRGB());
		} else {
			money.clickGUI.drawGradient(x + (sliderWidth) + 6, y, x + w, y + h, new Color(25, 25, 25, 150).getRGB(),
					new Color(25, 25, 25, 150).getRGB());
			money.clickGUI.drawGradient(x, y, x + (sliderWidth) + 6, y + h,
					new Color(money.settingManager.getSetting("ClickGUI", "Red").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Green").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Blue").getIntegerValue(), 235).getRGB(),
					new Color(money.settingManager.getSetting("ClickGUI", "Red").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Green").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Blue").getIntegerValue(), 235).getRGB());
		}

		money.clickGUI.drawRect(x, y, x + 6, y + h,
				new Color(money.settingManager.getSetting("ClickGUI", "Red").getIntegerValue(),
						money.settingManager.getSetting("ClickGUI", "Green").getIntegerValue(),
						money.settingManager.getSetting("ClickGUI", "Blue").getIntegerValue(), 100).getRGB());
		x += 6;
		w -= 6;

		FontUtil.drawStringWithShadow(setting.getName(), (float) (x + 1), (float) (y + 4),
				new Color(255, 255, 255, 255).getRGB());
		FontUtil.drawStringWithShadow(String.valueOf(setting.getIntegerValue()),
				(float) ((x + w - 6) - FontUtil.getStringWidth(String.valueOf(setting.getIntegerValue()))),
				(float) (y + 4), new Color(255, 255, 255, 255).getRGB());
	}

	public void mouseDown(int mX, int mY, int mB) {
		if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
			dragging = true;
		}
	}

	public void mouseUp(int mouseX, int mouseY) {
		dragging = false;
	}

	public void close() {
		dragging = false;
	}

	public static class IntSlider extends SliderButton {
		private final Setting intSetting;

		public IntSlider(Module module, Setting setting, int X, int Y, int W, int H) {
			super(module, setting, X, Y, W, H);
			intSetting = setting;
		}

		@Override
		protected void updateSlider(final int mouseX) {
			final double diff = Math.min(getW(), Math.max(0, mouseX - getX()));
			final double min = intSetting.getMinIntegerValue();
			final double max = intSetting.getMaxIntegerValue();
			sliderWidth = (int) ((getW() - 6) * (intSetting.getIntegerValue() - min) / (max - min));
			if (dragging) {
				if (diff == 0.0) {
					intSetting.setIntegerValue(intSetting.getIntegerValue());
				} else {
					final DecimalFormat format = new DecimalFormat("##");
					final String newValue = format.format(diff / getW() * (max - min) + min);
					intSetting.setIntegerValue(Integer.parseInt(newValue));
				}
			}
		}
	}
}
