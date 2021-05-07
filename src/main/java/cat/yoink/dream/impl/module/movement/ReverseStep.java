package cat.yoink.dream.impl.module.movement;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ReverseStep extends Module {

	public ReverseStep(String name, String description, Category category) {
		super(name, description, category);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		try {
			if (mc.player == null)
				return;
			if (!mc.player.onGround || mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava()
					|| mc.player.movementInput.jump || mc.player.noClip)
				return;
			if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0)
				return;
			if (mc.player.onGround) {
				final EntityPlayerSP player = mc.player;
				--player.motionY;
			}
		} catch (Exception e) {

		}
	}

}
