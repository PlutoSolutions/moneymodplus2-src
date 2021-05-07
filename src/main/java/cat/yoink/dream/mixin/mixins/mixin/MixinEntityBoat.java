package cat.yoink.dream.mixin.mixins.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ EntityBoat.class })
public abstract class MixinEntityBoat extends MixinEntity {
	public MixinEntityBoat() {
		super();
	}

	@Shadow
	public abstract double getMountedYOffset();

	final private Minecraft mc = Minecraft.getMinecraft();

	@Inject(method = "controlBoat", at = @At("HEAD"), cancellable = true)
	private void controlBoat(CallbackInfo info) {
		if (mc.player != null && mc.player.isRiding())
			info.cancel();
	}

	@Inject(method = "applyOrientationToEntity", at = @At("HEAD"), cancellable = true)
	private void applyOrientationToEntity(Entity passenger, CallbackInfo info) {
		if (mc.player != null && mc.player.isRiding())
			info.cancel();
	}

	// Pyro's BoatFly be like :-)
	@Inject(method = "updatePassenger", at = @At("HEAD"), cancellable = true)
	private void updatePassenger(Entity passenger, CallbackInfo info) {
		if (mc.player != null && mc.player.isRiding() && mc.player == passenger) {
			info.cancel();
			float f = 0.0F;
			float f1 = (float) ((this.isDead ? 0.009999999776482582D : this.getMountedYOffset())
					+ passenger.getYOffset());
			Vec3d vec3d = (new Vec3d((double) f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - 1.5707964F);
			passenger.setPosition(this.posX + vec3d.x, this.posY + (double) f1, this.posZ + vec3d.z);
		}
	}
}