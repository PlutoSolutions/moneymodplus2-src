package cat.yoink.dream.mixin.mixins.mixin;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cat.yoink.dream.impl.event.EventModelRender;
import cat.yoink.dream.impl.event.EventPostRenderLayers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { RenderLivingBase.class }, priority = 2147483645)
public abstract class RenderLivingBaseMixin {
	@Shadow
	protected ModelBase mainModel;

	@Redirect(method = {
			"renderModel" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
	private void renderModelWrapper(final ModelBase modelBase, final Entity entity, final float limbSwing,
			final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch,
			final float scaleFactor) {
		EventModelRender modelrenderpre = new EventModelRender(modelBase, entity, limbSwing, limbSwingAmount,
				ageInTicks, netHeadYaw, headPitch, scaleFactor, 0);
		MinecraftForge.EVENT_BUS.post(modelrenderpre);
		// modelrenderpre.setState(Event.State.PRE);
		// modelrenderpre.call();
		if (modelrenderpre.isCanceled()) {
			return;
		}
		modelBase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		EventModelRender modelrenderpost = new EventModelRender(modelBase, entity, limbSwing, limbSwingAmount,
				ageInTicks, netHeadYaw, headPitch, scaleFactor, 1);
		MinecraftForge.EVENT_BUS.post(modelrenderpost);
		// modelrenderpost.setState(Event.State.POST);
		// modelrenderpost.call();
	}

	@Inject(method = { "renderLayers" }, at = { @At("RETURN") })
	public void renderLayers(final EntityLivingBase entitylivingbaseIn, final float limbSwing,
			final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw,
			final float headPitch, final float scaleIn, final CallbackInfo ci) {
		EventPostRenderLayers eventPostRenderLayers = new EventPostRenderLayers(RenderLivingBase.class.cast(this),
				this.mainModel, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
				headPitch, scaleIn);
		MinecraftForge.EVENT_BUS.post(eventPostRenderLayers);
	}
}