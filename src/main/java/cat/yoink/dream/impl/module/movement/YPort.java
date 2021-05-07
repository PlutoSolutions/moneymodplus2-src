package cat.yoink.dream.impl.module.movement;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.impl.event.MovementUtils;
import cat.yoink.dream.mixin.mixins.accessor.IMinecraft;
import cat.yoink.dream.mixin.mixins.accessor.ITimer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class YPort extends Module {

	public YPort(String name, String description, Category category) {
		super(name, description, category);
	}

	private boolean isOnIce = false;

	public void onDisable() {
		resetTimer();
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null)
			return;
		if (!MovementUtils.isMoving(mc.player) || mc.player.isInWater() && mc.player.isInLava()
				|| mc.player.collidedHorizontally) {
			return;
		}

		if (mc.player.onGround) {
			setTimer(1.20f);
			mc.player.jump();
			MovementUtils.setSpeed(mc.player, MovementUtils.getBaseMoveSpeed() + (isOnIce ? 0.3 : 0.06));
		} else {
			mc.player.motionY = -1;
			resetTimer();
		}
	}

	public static void setTimer(float speed) {
		((ITimer) ((IMinecraft) Minecraft.getMinecraft()).getTimer()).setTickLength(50f / speed);
		;
	}

	public static void resetTimer() {
		((ITimer) ((IMinecraft) Minecraft.getMinecraft()).getTimer()).setTickLength(50f);
		;
	}

}
