package cat.yoink.dream.api.setting;

import cat.yoink.dream.api.module.Module;

import java.util.ArrayList;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class SettingManager {
	public final static ArrayList<Setting> settings = new ArrayList<>();

	public void addSetting(Setting setting) {
		settings.add(setting);
	}

	public ArrayList<Setting> getSettings(Module module) {
		ArrayList<Setting> sets = new ArrayList<>();

		for (Setting setting : settings) {
			if (setting.getModule().equals(module)) {
				sets.add(setting);
			}
		}

		return sets;
	}

	public static Setting getSetting(String moduleName, String name) {
		for (Setting setting : settings) {
			if (setting.getModule().getName().equalsIgnoreCase(moduleName)
					&& setting.getName().equalsIgnoreCase(name)) {
				return setting;
			}
		}

		return null;
	}
}
