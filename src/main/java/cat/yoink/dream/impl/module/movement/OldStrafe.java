package cat.yoink.dream.impl.module.movement;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OldStrafe extends Module {

	private final Setting jump = new Setting.Builder(SettingType.BOOLEAN).setName("AutoJump").setModule(this)
			.setBooleanValue(true).build();

	int waitCounter;
	int forward = 1;
	private static final AxisAlignedBB WATER_WALK_AA = new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D);

	public OldStrafe(String name, String description, Category category) {
		super(name, description, category);
		addSetting(jump);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {

		boolean boost = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90;

		if (mc.player.moveForward != 0) {
			if (!mc.player.isSprinting())
				mc.player.setSprinting(true);
			float yaw = mc.player.rotationYaw;
			if (mc.player.moveForward > 0) {
				if (mc.player.movementInput.moveStrafe != 0) {
					yaw += (mc.player.movementInput.moveStrafe > 0) ? -45 : 45;
				}
				forward = 1;
				mc.player.moveForward = 1.0f;
				mc.player.moveStrafing = 0;
			} else if (mc.player.moveForward < 0) {
				if (mc.player.movementInput.moveStrafe != 0) {
					yaw += (mc.player.movementInput.moveStrafe > 0) ? 45 : -45;
				}
				forward = -1;
				mc.player.moveForward = -1.0f;
				mc.player.moveStrafing = 0;
			}
			if (mc.player.onGround) {
				mc.player.setJumping(false);
				if (waitCounter < 1) {
					waitCounter++;
					return;
				} else {
					waitCounter = 0;
				}
				float f = (float) Math.toRadians(yaw);
				if (jump.getBooleanValue()) {
					mc.player.motionY = 0.405;
					mc.player.motionX -= (double) (MathHelper.sin(f) * 0.1f) * forward;
					mc.player.motionZ += (double) (MathHelper.cos(f) * 0.1f) * forward;
				} else {
					if (mc.gameSettings.keyBindJump.isPressed()) {
						mc.player.motionY = 0.405;
						mc.player.motionX -= (double) (MathHelper.sin(f) * 0.1f) * forward;
						mc.player.motionZ += (double) (MathHelper.cos(f) * 0.1f) * forward;
					}
				}
			} else {
				if (waitCounter < 1) {
					waitCounter++;
					return;
				} else {
					waitCounter = 0;
				}
				double currentSpeed = Math
						.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
				double speed = boost ? 1.0064 : 1.001;
				if (mc.player.motionY < 0)
					speed = 1;

				double direction = Math.toRadians(yaw);
				mc.player.motionX = (-Math.sin(direction) * speed * currentSpeed) * forward;
				mc.player.motionZ = (Math.cos(direction) * speed * currentSpeed) * forward;
			}
		}
	}

}
