package cat.yoink.dream.mixin.mixins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cat.yoink.dream.impl.event.EventModelPlayerRender;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

@Mixin(value = ModelPlayer.class, priority = 94355)
public class ModelPlayerMixin {
	@Shadow
	public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount,
			final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
	}

	@Inject(method = { "render" }, at = { @At("HEAD") }, cancellable = true)
	private void renderPre(final Entity entityIn, final float limbSwing, final float limbSwingAmount,
			final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale,
			final CallbackInfo ci) {
		EventModelPlayerRender modelrenderpre = new EventModelPlayerRender(
				(net.minecraft.client.model.ModelBase) ModelPlayer.class.cast(this), entityIn, limbSwing,
				limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, 0);
		MinecraftForge.EVENT_BUS.post(modelrenderpre);
		if (modelrenderpre.isCanceled())
			ci.cancel();
		/*
		 * modelrenderpre.setState(Event.State.PRE); modelrenderpre.call(); if
		 * (modelrenderpre.isCancelled()) { ci.cancel(); }
		 */
	}

	@Inject(method = { "render" }, at = { @At("RETURN") })
	private void renderPost(final Entity entityIn, final float limbSwing, final float limbSwingAmount,
			final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale,
			final CallbackInfo ci) {
		EventModelPlayerRender modelrenderpost = new EventModelPlayerRender(
				(net.minecraft.client.model.ModelBase) ModelPlayer.class.cast(this), entityIn, limbSwing,
				limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, 1);
		MinecraftForge.EVENT_BUS.post(modelrenderpost);
	}
}
