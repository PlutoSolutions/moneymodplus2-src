package cat.yoink.dream.impl.module.render;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.mixin.mixins.accessor.IItemRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LowHands extends Module {

	ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

	private final Setting main = new Setting.Builder(SettingType.INTEGER).setName("MainHand").setModule(this)
			.setIntegerValue(5).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting off = new Setting.Builder(SettingType.INTEGER).setName("OffHand").setModule(this)
			.setIntegerValue(5).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	public LowHands(String name, String description, Category category) {
		super(name, description, category);
		addSetting(main);
		addSetting(off);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		((IItemRenderer) itemRenderer).setOffHand((float) off.getIntegerValue() / 10);
		((IItemRenderer) itemRenderer).setMainHand((float) main.getIntegerValue() / 10);
	}

}
