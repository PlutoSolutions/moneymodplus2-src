package cat.yoink.dream.mixin.mixins.mixin;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.mixin.mixins.accessor.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author yoink
 * @since 9/20/2020
 */
@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {
	@Shadow
	@Final
	private Timer timer;

	@Shadow
	public int rightClickDelayTimer;

	@Override
	public Timer getTimer() {
		return timer;
	}

	@Override
	public void setRightClickDelayTimer(final int i) {
		this.rightClickDelayTimer = i;
	}

	@Inject(method = { "shutdown()V" }, at = { @At("HEAD") })
	public void saveSettingsOnShutdown(final CallbackInfo ci) {
		money.configUtils.saveMods();
		System.out.println("Saved moneymod+2 mods!");
		money.configUtils.saveSettingsList();
		System.out.println("Saved moneymod+2 settings!");
		money.configUtils.saveBinds();
		System.out.println("Saved moneymod+2 binds!");
		money.configUtils.savePrefix();
		money.configUtils.saveFriends();
	}
}
