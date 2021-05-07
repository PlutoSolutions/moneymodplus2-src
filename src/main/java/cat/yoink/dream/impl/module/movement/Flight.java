package cat.yoink.dream.impl.module.movement;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Flight extends Module {

	private final Setting speed = new Setting.Builder(SettingType.INTEGER).setName("Speed").setModule(this)
			.setIntegerValue(10).setMinIntegerValue(0).setMaxIntegerValue(15).build();

	private final Setting ecme = new Setting.Builder(SettingType.BOOLEAN).setName("ECME").setModule(this)
			.setBooleanValue(false).build();

	public Flight(String name, String description, Category category) {
		super(name, description, category);
		addSetting(speed);
		addSetting(ecme);
	}

	@Override
	public void onEnable() {
		if (mc.player == null)
			return;
		mc.player.capabilities.isFlying = true;
		if (mc.player.capabilities.isCreativeMode)
			return;
		mc.player.capabilities.allowFlying = true;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (ecme.getBooleanValue())
			mc.player.onGround = mc.player.ticksExisted % 2 == 0;
		mc.player.capabilities.setFlySpeed(speed.getIntegerValue() / 100f);
		mc.player.capabilities.isFlying = true;
		if (mc.player.capabilities.isCreativeMode)
			return;
		mc.player.capabilities.allowFlying = true;
	}

	@Override
	public void onDisable() {
		mc.player.capabilities.isFlying = false;
		mc.player.capabilities.setFlySpeed(0.05f);
		if (mc.player.capabilities.isCreativeMode)
			return;
		mc.player.capabilities.allowFlying = false;
	}
}
