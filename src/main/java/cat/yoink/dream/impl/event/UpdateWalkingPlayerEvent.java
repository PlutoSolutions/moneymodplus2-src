package cat.yoink.dream.impl.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class UpdateWalkingPlayerEvent extends Event {
	public int type;

	public UpdateWalkingPlayerEvent(int type) {
		this.type = type;
	}
}