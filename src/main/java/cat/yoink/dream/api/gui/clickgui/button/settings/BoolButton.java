package cat.yoink.dream.api.gui.clickgui.button.settings;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.gui.clickgui.button.SettingButton;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.util.font.FontUtil;

import java.awt.*;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class BoolButton extends SettingButton {
	private final Setting setting;

	public BoolButton(Module module, Setting setting, int X, int Y, int W, int H) {
		super(module, X, Y, W, H);
		this.setting = setting;
	}

	@Override
	public void render(int mX, int mY) {
		if (setting.getBooleanValue()) {
			drawButton(mX, mY);
			FontUtil.drawStringWithShadow(setting.getName(), (float) (getX() + 6), (float) (getY() + 4),
					new Color(money.settingManager.getSetting("ClickGUI", "Red").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Green").getIntegerValue(),
							money.settingManager.getSetting("ClickGUI", "Blue").getIntegerValue(), 232).getRGB());
		} else {
			if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
				money.clickGUI.drawGradient(getX(), getY(), getX() + getW(), getY() + getH(),
						new Color(25, 25, 25, 170).getRGB(), new Color(25, 25, 25, 170).getRGB());
			} else {
				money.clickGUI.drawGradient(getX(), getY(), getX() + getW(), getY() + getH(),
						new Color(25, 25, 25, 150).getRGB(), new Color(25, 25, 25, 150).getRGB());
			}

			FontUtil.drawString(setting.getName(), (float) (getX() + 6), (float) (getY() + 4),
					new Color(255, 255, 255, 232).getRGB());
		}
	}

	@Override
	public void mouseDown(int mX, int mY, int mB) {
		if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY) && (mB == 0 || mB == 1)) {
			setting.setBooleanValue(!setting.getBooleanValue());
		}
	}
}
