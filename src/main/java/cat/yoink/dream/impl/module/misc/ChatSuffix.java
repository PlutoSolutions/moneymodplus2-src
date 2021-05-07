package cat.yoink.dream.impl.module.misc;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.util.CommandUtil;
import cat.yoink.dream.impl.event.PacketSendEvent;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ChatSuffix extends Module {
	public ChatSuffix(String name, String description, Category category) {
		super(name, description, category);
	}

	private final String suffix = " | moneymod+2";
	// CPacketChatMessage.class, "message", "field_149440_a"
	protected static final Field msgfield = ReflectionHelper.findField(CPacketChatMessage.class, "message",
			"field_149440_a", "a");

//	@SubscribeEvent
//	public void onChat(ClientChatEvent event)
//	{
//		for (String s : Arrays.asList("/", ".", "-", ",", ":", ";", "'", "\"", "+", "\\"))
//		{
//			if (event.getMessage().startsWith(s)) return;
//		}
//
//		event.setMessage(event.getMessage() + " \uFF5C \u1d0d\u1d0f\u0274\u1d07\u028f\u1d0d\u1d0f\u1d05");
//	}

	@SubscribeEvent
	public void onPacket(final PacketSendEvent event) {
		if (event.getPacket() instanceof CPacketChatMessage) {
			String s = ((CPacketChatMessage) event.getPacket()).getMessage();
			if (s.startsWith("/") || s.contains(suffix)) {
				return;
			}
			s += suffix;
			if (s.length() >= 256) {
				s = s.substring(0, 256);
			}

			event.setCanceled(true);
			CPacketChatMessage newpacket = new CPacketChatMessage(s);
			mc.player.connection.sendPacket(newpacket);

			// ((CPacketChatMessage)event.getPacket()).message = s;
		}
	}

}
