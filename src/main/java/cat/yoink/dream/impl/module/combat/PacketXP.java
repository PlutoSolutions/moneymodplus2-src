package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.impl.event.PacketSendEvent;
import cat.yoink.dream.mixin.mixins.accessor.ICPacketPlayer;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketXP extends Module {

	public PacketXP(String name, String description, Category category) {
		super(name, description, category);
	}

	@SubscribeEvent
	public void listener(final PacketSendEvent event) {
		if (event.getPacket() instanceof CPacketPlayer
				&& mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle) {
			final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
			((ICPacketPlayer) packet).setPitch(90.0f);
		}
	}
}
