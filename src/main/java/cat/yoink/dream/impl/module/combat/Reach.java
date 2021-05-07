package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;

public class Reach extends Module {

	public final Setting range = new Setting.Builder(SettingType.INTEGER).setName("Range").setModule(this)
			.setIntegerValue(1).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	public Reach(String name, String description, Category category) {
		super(name, description, category);
		addSetting(range);
		// TODO Auto-generated constructor stub
	}

}
