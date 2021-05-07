package cat.yoink.dream.impl.module.misc;

import java.util.Set;

import com.google.common.collect.Sets;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.impl.event.PacketReceiveEvent;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSoundLag extends Module {

	public NoSoundLag(String name, String description, Category category) {
		super(name, description, category);
		// TODO Auto-generated constructor stub
	}

	private static final Set<SoundEvent> BLACKLIST = Sets.newHashSet(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
			SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
			SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
			SoundEvents.ITEM_ARMOR_EQUIP_LEATHER);

	@SubscribeEvent
	public void onReceivePacket(PacketReceiveEvent event) {
		if (event.getPacket() instanceof SPacketSoundEffect) {
			SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
			if (BLACKLIST.contains(packet.getSound())) {
				event.setCanceled(true);
			}

		}
	}

}
