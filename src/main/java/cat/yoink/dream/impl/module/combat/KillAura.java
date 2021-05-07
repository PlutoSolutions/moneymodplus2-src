package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.friend.Friends;
import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.impl.event.PacketSendEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

	private final Setting range = new Setting.Builder(SettingType.INTEGER).setName("Range").setModule(this)
			.setIntegerValue(5).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting swordOnly = new Setting.Builder(SettingType.BOOLEAN).setName("Sword Only").setModule(this)
			.setBooleanValue(true).build();

	private final Setting criticals = new Setting.Builder(SettingType.BOOLEAN).setName("Criticals").setModule(this)
			.setBooleanValue(true).build();

	private final Setting caCheck = new Setting.Builder(SettingType.BOOLEAN).setName("AC Check").setModule(this)
			.setBooleanValue(false).build();

	private final Setting swingOffhand = new Setting.Builder(SettingType.BOOLEAN).setName("Swing Offhand")
			.setModule(this).setBooleanValue(false).build();

	public KillAura(String name, String description, Category category) {
		super(name, description, category);
		addSetting(range);
		addSetting(swordOnly);
		addSetting(criticals);
		addSetting(caCheck);
		addSetting(swingOffhand);
	}

	private boolean isAttacking = false;

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null || mc.player.isDead)
			return;
		List<Entity> targets = mc.world.loadedEntityList.stream().filter(entity -> entity != mc.player)
				.filter(entity -> mc.player.getDistance(entity) <= range.getIntegerValue())
				.filter(entity -> !entity.isDead).filter(entity -> entity instanceof EntityPlayer)
				.filter(entity -> ((EntityPlayer) entity).getHealth() > 0)
				.filter(entity -> !Friends.isFriend(entity.getName()))
				.sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList());

		targets.forEach(target -> {
			if (swordOnly.getBooleanValue())
				if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword))
					return;

			if (caCheck.getBooleanValue())
				if (((AutoCrystal) ModuleManager.getModule("AutoCrystal")).isToggled())
					return;

			attack(target);
		});
	}

	@SubscribeEvent
	public void onSendPacket(PacketSendEvent event) {
		if (event.getPacket() instanceof CPacketUseEntity) {
			if (criticals.getBooleanValue()
					&& ((CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK
					&& mc.player.onGround && isAttacking) {
				mc.player.connection.sendPacket(
						new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
				mc.player.connection
						.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
			}
		}
	}

	public void attack(Entity e) {
		if (mc.player.getCooledAttackStrength(0) >= 1) {
			isAttacking = true;
			mc.playerController.attackEntity(mc.player, e);
			if (swingOffhand.getBooleanValue()) {
				mc.player.swingArm(EnumHand.OFF_HAND);
			} else {
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
			isAttacking = false;
		}
	}
}
