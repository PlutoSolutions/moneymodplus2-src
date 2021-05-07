package cat.yoink.dream.impl.module.movement;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FastSwim extends Module {

	private final Setting up = new Setting.Builder(SettingType.BOOLEAN).setName("Speed Up").setModule(this)
			.setBooleanValue(true).build();

	private final Setting down = new Setting.Builder(SettingType.BOOLEAN).setName("Speed Down").setModule(this)
			.setBooleanValue(true).build();

	private final Setting forward = new Setting.Builder(SettingType.BOOLEAN).setName("Speed Forward").setModule(this)
			.setBooleanValue(true).build();

	private final Setting sprint = new Setting.Builder(SettingType.BOOLEAN).setName("Sprint In Water").setModule(this)
			.setBooleanValue(true).build();

	private final Setting only2b = new Setting.Builder(SettingType.BOOLEAN).setName("Only2b").setModule(this)
			.setBooleanValue(true).build();

	int divider = 5;

	public FastSwim(String name, String description, Category category) {
		super(name, description, category);
		addSetting(up);
		addSetting(down);
		addSetting(forward);
		addSetting(sprint);
		addSetting(only2b);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null)
			return;
		if (this.only2b.getBooleanValue() && !mc.isSingleplayer() && mc.getCurrentServerData() != null
				&& mc.getCurrentServerData().serverIP.equalsIgnoreCase("2b2t.org")) {
			if (this.sprint.getBooleanValue() && (mc.player.isInLava() || mc.player.isInWater())) {
				mc.player.setSprinting(true);
			}
			if ((mc.player.isInWater() || mc.player.isInLava()) && mc.gameSettings.keyBindJump.isKeyDown()
					&& this.up.getBooleanValue()) {
				mc.player.motionY = 0.725 / this.divider;
			}
			if (mc.player.isInWater() || mc.player.isInLava()) {
				if ((this.forward.getBooleanValue() && mc.gameSettings.keyBindForward.isKeyDown())
						|| mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()
						|| mc.gameSettings.keyBindBack.isKeyDown()) {
					mc.player.jumpMovementFactor = 0.34f / this.divider;
				} else {
					mc.player.jumpMovementFactor = 0.0f;
				}
			}
			if (mc.player.isInWater() && this.down.getBooleanValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
				final int divider2 = this.divider * -1;
				mc.player.motionY = 2.2 / divider2;
			}
			if (mc.player.isInLava() && this.down.getBooleanValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
				final int divider2 = this.divider * -1;
				mc.player.motionY = 0.91 / divider2;
			}
		}
		if (!this.only2b.getBooleanValue()) {
			if (this.sprint.getBooleanValue() && (mc.player.isInLava() || mc.player.isInWater())) {
				mc.player.setSprinting(true);
			}
			if ((mc.player.isInWater() || mc.player.isInLava()) && mc.gameSettings.keyBindJump.isKeyDown()
					&& this.up.getBooleanValue()) {
				mc.player.motionY = 0.725 / this.divider;
			}
			if (mc.player.isInWater() || mc.player.isInLava()) {
				if ((this.forward.getBooleanValue() && mc.gameSettings.keyBindForward.isKeyDown())
						|| mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()
						|| mc.gameSettings.keyBindBack.isKeyDown()) {
					mc.player.jumpMovementFactor = 0.34f / this.divider;
				} else {
					mc.player.jumpMovementFactor = 0.0f;
				}
			}
			if (mc.player.isInWater() && this.down.getBooleanValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
				final int divider2 = this.divider * -1;
				mc.player.motionY = 2.2 / divider2;
			}
			if (mc.player.isInLava() && this.down.getBooleanValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
				final int divider2 = this.divider * -1;
				mc.player.motionY = 0.91 / divider2;
			}
		}
	}

}
