package cat.yoink.dream.mixin.mixins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cat.yoink.dream.api.module.ModuleManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(value = EntityRenderer.class)
public class EntityRendererMixin {
	@Inject(method = "setupFog", at = @At(value = "HEAD"), cancellable = true)
	public void setupFog(int startCoords, float partialTicks, CallbackInfo callbackInfo) {
		if (ModuleManager.getModule("AntiFog").isEnabled())
			callbackInfo.cancel();
	}

	@Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
	public IBlockState getBlockStateAtEntityViewpoint(World worldIn, Entity entityIn, float p_186703_2_) {
		if (ModuleManager.getModule("AntiFog").isEnabled())
			return (IBlockState) Blocks.AIR;
		return ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn, entityIn, p_186703_2_);
	}

	@Redirect(method = {
			"orientCamera" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
	public RayTraceResult rayTraceBlocks(final WorldClient world, final Vec3d start, final Vec3d end) {
		if (ModuleManager.getModule("CameraClip").isEnabled()) {
			return null;
		}
		return world.rayTraceBlocks(start, end);
	}

	@Inject(method = { "hurtCameraEffect" }, at = { @At("HEAD") }, cancellable = true)
	public void hurtCameraEffect(final float ticks, final CallbackInfo info) {
		if (ModuleManager.getModule("NoHurtCam").isEnabled()) {
			info.cancel();
		}
	}
}