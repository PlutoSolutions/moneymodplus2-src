package cat.yoink.dream.impl.module.movement;

import java.util.Objects;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.impl.event.MoveEvent;
import cat.yoink.dream.impl.event.WalkEvent;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Strafe extends Module {

	private final Setting jump = new Setting.Builder(SettingType.BOOLEAN).setName("Auto Jump").setModule(this)
			.setBooleanValue(true).build();

	private final Setting limiter = new Setting.Builder(SettingType.BOOLEAN).setName("Set Ground").setModule(this)
			.setBooleanValue(true).build();

	private final Setting specialMoveSpeed = new Setting.Builder(SettingType.INTEGER).setName("Speed").setModule(this)
			.setIntegerValue(100).setMinIntegerValue(0).setMaxIntegerValue(150).build();

	private final Setting potionSpeed = new Setting.Builder(SettingType.INTEGER).setName("Potion Speed").setModule(this)
			.setIntegerValue(130).setMinIntegerValue(0).setMaxIntegerValue(150).build();

	private final Setting potionSpeed2 = new Setting.Builder(SettingType.INTEGER).setName("Potion Speed 2")
			.setModule(this).setIntegerValue(125).setMinIntegerValue(0).setMaxIntegerValue(150).build();

	private final Setting acceleration = new Setting.Builder(SettingType.INTEGER).setName("Accel").setModule(this)
			.setIntegerValue(2149).setMinIntegerValue(1000).setMaxIntegerValue(2500).build();

	public Strafe(String name, String description, Category category) {
		super(name, description, category);
		addSetting(jump);
		addSetting(limiter);
		addSetting(specialMoveSpeed);
		addSetting(potionSpeed);
		addSetting(potionSpeed2);
		addSetting(acceleration);
	}

	@Override
	public void onEnable() {
		moveSpeed = getBaseMoveSpeed();
	}

	@Override
	public void onDisable() {
		moveSpeed = 0D;
		stage = 2;
	}

	private int stage = 1;
	private double moveSpeed;
	private double lastDist;
	private int cooldownHops = 0;

	@SubscribeEvent
	public void onUpdateWalkingPlayer(WalkEvent event) {
		this.lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX)
				+ (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {

		if (mc.player == null)
			return;
	}

	@SubscribeEvent
	public void onMove(MoveEvent event) {
		if (!limiter.getBooleanValue() && mc.player.onGround) {
			stage = 2;
		}

		switch (this.stage) {
		case 0: {
			++this.stage;
			this.lastDist = 0.0;
			break;
		}
		case 2: {
			double motionY = 0.40123128;
			if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
				if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
					motionY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
				}
				event.setY(mc.player.motionY = motionY);
				this.moveSpeed *= 2.149;
				break;
			}
			break;
		}
		case 3: {
			this.moveSpeed = this.lastDist - 0.76 * (this.lastDist - this.getBaseMoveSpeed());
			break;
		}
		default: {
			if ((((false && mc.world
					.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0))
					.size() > 0)) || mc.player.collidedVertically) && this.stage > 0) {
				this.stage = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
			}
			this.moveSpeed = this.lastDist - this.lastDist / 159.0;
			break;
		}
		}
		this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
		double forward = mc.player.movementInput.moveForward;
		double strafe = mc.player.movementInput.moveStrafe;
		final double yaw = mc.player.rotationYaw;
		if (forward == 0.0 && strafe == 0.0) {
			event.setX(0.0);
			event.setZ(0.0);
		} else if (forward != 0.0 && strafe != 0.0) {
			forward *= Math.sin(0.7853981633974483);
			strafe *= Math.cos(0.7853981633974483);
		}
		event.setX((forward * this.moveSpeed * -Math.sin(Math.toRadians(yaw))
				+ strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99);
		event.setZ((forward * this.moveSpeed * Math.cos(Math.toRadians(yaw))
				- strafe * this.moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99);
		++this.stage;
	}

	public double getBaseMoveSpeed() {
		double baseSpeed = 0.272;
		if (mc.player.isPotionActive(MobEffects.SPEED)) {
			final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))
					.getAmplifier();
			baseSpeed *= 1.0 + 0.2 * amplifier;
		}
		return baseSpeed;
	}
}
