package cat.yoink.dream.impl.module.render;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;

public class BlockHighlight extends Module {

	public final Setting red = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(210).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public final Setting green = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(30).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public final Setting blue = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(30).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public final Setting alpha = new Setting.Builder(SettingType.INTEGER).setName("Alpha").setModule(this)
			.setIntegerValue(30).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public BlockHighlight(String name, String description, Category category) {
		super(name, description, category);
	}

}
