package cat.yoink.dream.impl.module.misc;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiOverlay extends Module {

	public AntiOverlay(String name, String description, Category category) {
		super(name, description, category);
		// TODO Auto-generated constructor stub
	}

	@SubscribeEvent
	public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onEvent(RenderGameOverlayEvent event) {
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.HELMET)
				|| event.getType().equals(RenderGameOverlayEvent.ElementType.PORTAL))
			event.setCanceled(true);
	}
}
