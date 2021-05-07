package cat.yoink.dream.mixin.mixins.mixin;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cat.yoink.dream.api.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;

@Mixin(value = MovementInputFromOptions.class)
public class MovementInputFromOptionsMixin extends MovementInput {

	@Redirect(method = "updatePlayerMoveState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
	public boolean isKeyPressed(KeyBinding keyBinding) {
		if (ModuleManager.getModule("InventoryMove").isEnabled()
				&& Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
			return Keyboard.isKeyDown(keyBinding.getKeyCode());
		}
		return keyBinding.isKeyDown();
	}

	@Inject(method = "updatePlayerMoveState", at = @At("RETURN"), cancellable = true)
	public void updatePlayerMoveStateReturn(CallbackInfo callback) {
		if (ModuleManager.getModule("AutoWalk").isEnabled()) {
			this.moveForward = 1;
		}

		if (ModuleManager.getModule("AutoSwim").isEnabled()
				&& (Minecraft.getMinecraft().player.isInLava() || Minecraft.getMinecraft().player.isInWater())) {
			this.jump = true;
		}
	}
}