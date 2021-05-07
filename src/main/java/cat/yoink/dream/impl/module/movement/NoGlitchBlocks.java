package cat.yoink.dream.impl.module.movement;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.impl.event.EventDamageBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoGlitchBlocks extends Module {

	public NoGlitchBlocks(String name, String description, Category category) {
		super(name, description, category);
		// TODO Auto-generated constructor stub
	}

	@SubscribeEvent
	public void onDamageBlock(final EventDamageBlock e) {
		if (mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
			mc.player.connection.sendPacket(new CPacketPlayerDigging());
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
					new BlockPos(-1, -1, -1), EnumFacing.fromAngle(-1.0)));
		}
	}

}
