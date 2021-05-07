package cat.yoink.dream.impl.module.movement;

import java.util.Comparator;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.impl.event.PacketSendEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemBoat;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BoatFly extends Module {

	private final Setting Bypass = new Setting.Builder(SettingType.BOOLEAN).setName("Bypass").setModule(this)
			.setBooleanValue(false).build();

	private final Setting NoGravity = new Setting.Builder(SettingType.BOOLEAN).setName("NoGravity").setModule(this)
			.setBooleanValue(false).build();

	private final Setting FixYaw = new Setting.Builder(SettingType.BOOLEAN).setName("FixYaw").setModule(this)
			.setBooleanValue(false).build();

	private final Setting PlaceBypass = new Setting.Builder(SettingType.BOOLEAN).setName("PlaceBypass").setModule(this)
			.setBooleanValue(false).build();

	private final Setting Speed = new Setting.Builder(SettingType.INTEGER).setName("Speed").setModule(this)
			.setIntegerValue(10).setMinIntegerValue(1).setMaxIntegerValue(50).build();

	private final Setting UpSpeed = new Setting.Builder(SettingType.INTEGER).setName("UpSpeed").setModule(this)
			.setIntegerValue(1).setMinIntegerValue(1).setMaxIntegerValue(10).build();

	private int packetCounter = 0;
	private boolean stopFlying = false;

	public BoatFly(String name, String description, Category category) {
		super(name, description, category);
		addSetting(Bypass);
		addSetting(NoGravity);
		addSetting(FixYaw);
		addSetting(PlaceBypass);
		addSetting(Speed);
		addSetting(UpSpeed);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (mc.player.getRidingEntity() instanceof EntityBoat) {
			mc.player.getRidingEntity().setNoGravity(false);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.world != null && mc.player.getRidingEntity() != null) {
			Entity riding = mc.player.getRidingEntity();
			if (riding instanceof EntityBoat) {
				steerBoat(riding);
			}
		}
	}

	@SubscribeEvent
	public void onSendPacket(PacketSendEvent p_Event) {
		if (mc.world != null && mc.player != null) {
			if (mc.player.getRidingEntity() instanceof EntityBoat) {
				// packet trick
				if (Bypass.getBooleanValue() && p_Event.getPacket() instanceof CPacketInput
						&& !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.player.getRidingEntity().onGround) {
					++packetCounter;
					if (packetCounter == 3) {
						NCPPacketTrick();
					}
				}

				// packet canceller
				if (Bypass.getBooleanValue() && p_Event.getPacket() instanceof SPacketPlayerPosLook
						|| p_Event.getPacket() instanceof SPacketMoveVehicle && !stopFlying) {
					p_Event.setCanceled(true);
				}
			}

			// allows to place boats EVERYWHERE you want to (ncp blocks placing boats not in
			// the water)
			if (PlaceBypass.getBooleanValue() && p_Event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock
					&& mc.player.getHeldItemMainhand().getItem() instanceof ItemBoat
					|| mc.player.getHeldItemOffhand().getItem() instanceof ItemBoat) {
				p_Event.setCanceled(true);
			}
		}
	}

	private void steerBoat(Entity boat) {
		if (FixYaw.getBooleanValue()) {
			boat.rotationYaw = mc.player.rotationYaw;
			boat.rotationPitch = mc.player.rotationPitch;
		}

		boat.setNoGravity(NoGravity.getBooleanValue());
		int angle = 0;

		final boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
		final boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
		final boolean right = mc.gameSettings.keyBindRight.isKeyDown();
		final boolean back = mc.gameSettings.keyBindBack.isKeyDown();

		if (!(forward && back))
			boat.motionY = 0.0;
		if (mc.gameSettings.keyBindJump.isKeyDown())
			boat.motionY += UpSpeed.getIntegerValue() / 2f;
		if (mc.gameSettings.keyBindSprint.isKeyDown())
			boat.motionY -= UpSpeed.getIntegerValue() / 2f;
		if (!forward && !left && !right && !back)
			return;

		if (left && right) {
			if (forward)
				angle = 0;
			else if (back)
				angle = 180;

		} else if (forward && back) {
			if (left)
				angle = -90;
			else if (right)
				angle = 90;
			else
				angle = -1;
		} else {
			if (left)
				angle = -90;
			else if (right)
				angle = 90;
			else
				angle = 0;
		}
		if (forward)
			angle /= 2;
		else if (back)
			angle = 180 - angle / 2;
		if (angle == -1)
			return;

		if (isBorderingChunk(boat, boat.motionX, boat.motionZ))
			stopFlying = true;
		else
			stopFlying = false;

		float yaw = mc.player.rotationYaw + angle;
		boat.motionX = getRelativeX(yaw) * Speed.getIntegerValue();
		boat.motionZ = getRelativeZ(yaw) * Speed.getIntegerValue();
	}

	private double getRelativeX(float yaw) {
		return Math.sin(Math.toRadians(-yaw));
	}

	private double getRelativeZ(float yaw) {
		return Math.cos(Math.toRadians(yaw));
	}

	private boolean isBorderingChunk(Entity boat, Double motX, Double motZ) {
		return mc.world.getChunk((int) (boat.posX + motX) / 16, (int) (boat.posZ + motZ) / 16) instanceof EmptyChunk;
	}

	private void NCPPacketTrick() {
		packetCounter = 0;
		mc.player.getRidingEntity().dismountRidingEntity();
		Entity l_Entity = mc.world.loadedEntityList.stream().filter(p_Entity -> p_Entity instanceof EntityBoat)
				.min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity))).orElse(null);
		if (l_Entity != null) {
			mc.playerController.interactWithEntity(mc.player, l_Entity, EnumHand.MAIN_HAND);
		}
	}
}
