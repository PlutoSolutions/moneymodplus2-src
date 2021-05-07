package cat.yoink.dream.impl.module.misc;

import cat.cattyn.moneymod.moneydiscord;
import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;

public class DiscordRPC extends Module {

	public DiscordRPC(String name, String description, Category category) {
		super(name, description, category);
	}

	@Override
	public void onEnable() {
		moneydiscord.start();
	}

	@Override
	public void onDisable() {
		moneydiscord.stop();
	}

}
