package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;

public class AutoRun extends Module {

	public AutoRun(String name, String description, Category category) {
		super(name, description, category);
	}

	@Override
	public void onEnable() {
		mc.player.sendChatMessage("#goto ~100 ~ ~100");
		this.disable();
	}
}
