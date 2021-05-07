package cat.yoink.dream.mixin.mixins.mixin;

import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.util.CommandUtil;
import cat.yoink.dream.impl.event.MoveEvent;
import cat.yoink.dream.impl.event.UpdateWalkingPlayerEvent;
import cat.yoink.dream.impl.event.WalkEvent;
import cat.yoink.dream.impl.module.exploit.OffhandSwing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

/**
 * @author yoink
 * @since 9/20/2020
 */
/**
 * @author yoink
 * @since 9/20/2020
 */
@Mixin(value = EntityPlayerSP.class, priority = 9997)
public class EntityPlayerSPMixin extends AbstractClientPlayer {
	public EntityPlayerSPMixin(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
	public void move(final AbstractClientPlayer player, final MoverType moverType, final double x, final double y,
			final double z) {
		MoveEvent event = new MoveEvent(moverType, x, y, z);
		MinecraftForge.EVENT_BUS.post(event);

		if (!event.isCanceled())
			super.move(event.getType(), event.getX(), event.getY(), event.getZ());
	}

	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
	public void onUpdateWalkingPlayer(CallbackInfo ci) {
		WalkEvent event = new WalkEvent();
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled())
			ci.cancel();
	}

	@Redirect(method = {
			"onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
	public void closeScreen(final EntityPlayerSP entityPlayerSP) {
		if (ModuleManager.getModule("PortalChat").isEnabled()) {
			return;
		}
	}

	@Redirect(method = {
			"onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
	public void closeScreen(final Minecraft minecraft, final GuiScreen screen) {
		if (ModuleManager.getModule("PortalChat").isEnabled()) {
			return;
		}
	}

	@Inject(method = { "swingArm" }, at = { @At("HEAD") }, cancellable = true)
	public void swingArm(final EnumHand enumHand, final CallbackInfo info) {
		try {
			if (ModuleManager.getModule("OffhandSwing").isEnabled()) {
				super.swingArm(EnumHand.OFF_HAND);
				Minecraft.getMinecraft().player.connection.sendPacket((Packet<?>) new CPacketAnimation(enumHand));
				info.cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Shadow
	public void swingArm(final EnumHand hand) {
	}

}
