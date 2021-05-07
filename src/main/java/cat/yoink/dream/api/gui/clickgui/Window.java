package cat.yoink.dream.api.gui.clickgui;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.gui.clickgui.button.ModuleButton;
import cat.yoink.dream.api.gui.clickgui.button.SettingButton;
import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.util.font.FontUtil;
import cat.yoink.dream.impl.module.hud.ClickGUI;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;

import ca.weblite.objc.Client;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class Window {
	private final ArrayList<ModuleButton> buttons = new ArrayList<>();
	private final Category category;
	private final int W;
	private final int H;
	private final ArrayList<ModuleButton> buttonsBeforeClosing = new ArrayList<>();
	private int X;
	private int Y;
	private int dragX;
	private int dragY;
	private boolean open = true;
	private boolean dragging;
	private int showingButtonCount;
	private boolean opening;
	private boolean closing;

	public Window(Category category, int x, int y, int w, int h) {
		this.category = category;
		X = x;
		Y = y;
		W = w;
		H = h;

		int yOffset = Y + H;

		for (Module module : money.moduleManager.getModules(category)) {
			ModuleButton button = new ModuleButton(module, X, yOffset, W, H);
			buttons.add(button);
			yOffset += H;
		}
		showingButtonCount = buttons.size();
	}

	public void cycle_rainbow() {

		float[] tick_color = { (System.currentTimeMillis() % (360 * 32)) / (360f * 32) };

		int color_rgb_o = Color.HSBtoRGB(tick_color[0], 1, 1);

		money.settingManager.getSetting("ClickGUI", "Red").setIntegerValue((color_rgb_o >> 16) & 0xFF);
		money.settingManager.getSetting("ClickGUI", "Green").setIntegerValue((color_rgb_o >> 8) & 0xFF);
		money.settingManager.getSetting("ClickGUI", "Blue").setIntegerValue(color_rgb_o & 0xFF);

	}

	public void render(int mX, int mY) {
		if (dragging) {
			X = dragX + mX;
			Y = dragY + mY;
		}

		Gui.drawRect(X, Y, X + W, Y + H, new Color(0, 0, 0, 200).getRGB());
		if (money.settingManager.getSetting("ClickGUI", "Rainbow").getBooleanValue()) {
			cycle_rainbow();
		}
		FontUtil.drawString(category.getName(), X + 4, Y + 4,
				new Color(money.settingManager.getSetting("ClickGUI", "Red").getIntegerValue(),
						money.settingManager.getSetting("ClickGUI", "Green").getIntegerValue(),
						money.settingManager.getSetting("ClickGUI", "Blue").getIntegerValue(), 255).getRGB());

		if (open || opening || closing) {
			int modY = Y + H;

			int moduleRenderCount = 0;
			for (ModuleButton moduleButton : buttons) {
				moduleRenderCount++;

				if (moduleRenderCount < showingButtonCount + 1) {
					moduleButton.setX(X);
					moduleButton.setY(modY);

					moduleButton.render(mX, mY);

					if (!moduleButton.isOpen() && opening && buttonsBeforeClosing.contains(moduleButton)) {
						moduleButton.processRightClick();
					}

					modY += H;

					if (moduleButton.isOpen() || moduleButton.isOpening() || moduleButton.isClosing()) {

						int settingRenderCount = 0;
						for (SettingButton settingButton : moduleButton.getButtons()) {
							settingRenderCount++;

							if (settingRenderCount < moduleButton.getShowingModuleCount() + 1) {
								settingButton.setX(X);
								settingButton.setY(modY);

								settingButton.render(mX, mY);

								modY += H;
							}
						}
					}
				}
			}

		}

		if (opening) {
			showingButtonCount++;
			if (showingButtonCount == buttons.size()) {
				opening = false;
				open = true;
				buttonsBeforeClosing.clear();
			}
		}

		if (closing) {
			showingButtonCount--;
			if (showingButtonCount == 0 || showingButtonCount == 1) {
				closing = false;
				open = false;
			}
		}

	}

	public void mouseDown(int mX, int mY, int mB) {
		if (isHover(X, Y, W, H, mX, mY)) {
			if (mB == 0) {
				dragging = true;
				dragX = X - mX;
				dragY = Y - mY;
			} else if (mB == 1) {
				if (open && !opening && !closing) {
					showingButtonCount = buttons.size();
					closing = true;
					for (ModuleButton button : buttons) {
						if (button.isOpen()) {
							button.processRightClick();
							buttonsBeforeClosing.add(button);
						}
					}
				} else if (!open && !opening && !closing) {
					showingButtonCount = 1;
					opening = true;
				}
			}
		}

		if (open) {
			for (ModuleButton button : buttons) {
				button.mouseDown(mX, mY, mB);
			}
		}
	}

	public void mouseUp(int mX, int mY) {
		dragging = false;

		if (open) {
			for (ModuleButton button : buttons) {
				button.mouseUp(mX, mY);
			}
		}
	}

	public void keyPress(int key) {
		if (open) {
			for (ModuleButton button : buttons) {
				button.keyPress(key);
			}
		}
	}

	public void close() {
		for (ModuleButton button : buttons) {
			button.close();
		}
	}

	private boolean isHover(int X, int Y, int W, int H, int mX, int mY) {
		return mX >= X && mX <= X + W && mY >= Y && mY <= Y + H;
	}

	public int getY() {
		return Y;
	}

	public void setY(int y) {
		Y = y;
	}
}
