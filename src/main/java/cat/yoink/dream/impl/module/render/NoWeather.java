package cat.yoink.dream.impl.module.render;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoWeather extends Module {

	public NoWeather(String name, String description, Category category) {
		super(name, description, category);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.world == null)
			return;
		if (mc.world.isRaining()) {
			mc.world.setRainStrength(0);
		}
	}
}
