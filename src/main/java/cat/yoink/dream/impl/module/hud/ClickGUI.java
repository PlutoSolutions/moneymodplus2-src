package cat.yoink.dream.impl.module.hud;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class ClickGUI extends Module {
	private final Setting red = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting green = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting blue = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting rainbow = new Setting.Builder(SettingType.BOOLEAN).setName("Rainbow").setModule(this)
			.setBooleanValue(false).build();

	public ClickGUI(String name, String description, Category category) {
		super(name, description, category);

		setBind(Keyboard.KEY_P);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(rainbow);
	}

	@Override
	public void onEnable() {
		mc.displayGuiScreen(money.clickGUI);
	}
}
