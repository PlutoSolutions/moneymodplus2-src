package cat.yoink.dream.mixin.mixins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cat.yoink.dream.mixin.mixins.accessor.IRenderManager;
import net.minecraft.client.renderer.entity.RenderManager;

@Mixin(RenderManager.class)
public class RenderManagerMixin implements IRenderManager {
	@Shadow
	private double renderPosX;
	@Shadow
	private double renderPosY;
	@Shadow
	private double renderPosZ;

	public double getRenderPosX() {
		return renderPosX;
	}

	public double getRenderPosY() {
		return renderPosY;
	}

	public double getRenderPosZ() {
		return renderPosZ;
	}
}
