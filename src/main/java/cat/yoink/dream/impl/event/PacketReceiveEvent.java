package cat.yoink.dream.impl.event;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author yoink
 * @since 9/20/2020
 */
@Cancelable
public class PacketReceiveEvent extends Event {
	private Packet<?> packet;

	public PacketReceiveEvent(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> getPacket() {
		return packet;
	}

	public void setPacket(Packet<?> packet) {
		this.packet = packet;
	}
}
