package cat.yoink.dream.impl.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class MovementUtils {
	static Minecraft mc = Minecraft.getMinecraft();

	public static double calcMoveYaw(float yawIn) {
		float moveForward = roundedForward;
		float moveString = roundedStrafing;

		float strafe = 90 * moveString;
		if (moveForward != 0f) {
			strafe *= moveForward * 0.5f;
		} else
			strafe *= 1f;
		// strafe *= if (moveForward != 0F) moveForward * 0.5F else 1F;
		float yaw = yawIn - strafe;
		if (moveForward < 0f) {
			yaw -= 180;
		} else
			yaw -= 0;

		// yaw -= if (moveForward < 0F) 180 else 0;

		return Math.toRadians(yaw);
	}

	private static float roundedForward = getRoundedMovementInput(
			Minecraft.getMinecraft().player.movementInput.moveForward);

	private static float roundedStrafing = getRoundedMovementInput(
			Minecraft.getMinecraft().player.movementInput.moveStrafe);

	private static float getRoundedMovementInput(Float input) {
		if (input > 0) {
			input = 1f;
		} else if (input < 0) {
			input = -1f;
		} else
			input = 0f;
		return input;
	}

	public static boolean isMoving(EntityLivingBase entity) {
		return entity.moveForward != 0 || entity.moveStrafing != 0;
	}

	public static void setSpeed(final EntityLivingBase entity, final double speed) {
		double[] dir = forward(speed);
		entity.motionX = dir[0];
		entity.motionZ = dir[1];
	}

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873;
		if (Minecraft.getMinecraft().player != null
				&& Minecraft.getMinecraft().player.isPotionActive(Potion.getPotionById(1))) {
			final int amplifier = Minecraft.getMinecraft().player.getActivePotionEffect(Potion.getPotionById(1))
					.getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return baseSpeed;
	}

	public static double[] forward(final double speed) {
		float forward = Minecraft.getMinecraft().player.movementInput.moveForward;
		float side = Minecraft.getMinecraft().player.movementInput.moveStrafe;
		float yaw = Minecraft.getMinecraft().player.prevRotationYaw
				+ (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw)
						* Minecraft.getMinecraft().getRenderPartialTicks();
		if (forward != 0.0f) {
			if (side > 0.0f) {
				yaw += ((forward > 0.0f) ? -45 : 45);
			} else if (side < 0.0f) {
				yaw += ((forward > 0.0f) ? 45 : -45);
			}
			side = 0.0f;
			if (forward > 0.0f) {
				forward = 1.0f;
			} else if (forward < 0.0f) {
				forward = -1.0f;
			}
		}
		final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
		final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
		final double posX = forward * speed * cos + side * speed * sin;
		final double posZ = forward * speed * sin - side * speed * cos;
		return new double[] { posX, posZ };
	}
}