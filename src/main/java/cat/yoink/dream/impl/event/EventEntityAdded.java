package cat.yoink.dream.impl.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventEntityAdded extends Event {

	private Entity m_Entity;

	public EventEntityAdded(Entity p_Entity) {
		m_Entity = p_Entity;
	}

	public Entity GetEntity() {
		return m_Entity;
	}

}
