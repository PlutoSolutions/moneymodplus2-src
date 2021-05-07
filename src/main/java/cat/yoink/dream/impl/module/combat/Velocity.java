package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.impl.event.PacketReceiveEvent;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Velocity extends Module {

	public Velocity(String name, String description, Category category) {
		super(name, description, category);
	}

	@SubscribeEvent
	public void listener(final PacketReceiveEvent event) {
		if (mc.player == null || mc.world == null)
			return;
		if (event.getPacket() instanceof SPacketEntityVelocity
				&& ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
			event.setCanceled(true);
		}
		if (event.getPacket() instanceof SPacketExplosion) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null || mc.world == null)
			return;
		mc.player.entityCollisionReduction = 1.0F;
	}
}
