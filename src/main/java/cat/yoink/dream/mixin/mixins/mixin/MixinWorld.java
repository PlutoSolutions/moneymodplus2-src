package cat.yoink.dream.mixin.mixins.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cat.yoink.dream.impl.event.EventEntityAdded;
import cat.yoink.dream.impl.event.EventEntityRemoved;

@Mixin(World.class)
public class MixinWorld {

	@Inject(method = "onEntityAdded", at = @At("HEAD"), cancellable = true)
	public void onEntityAdded(Entity p_Entity, CallbackInfo p_Info) {
		EventEntityAdded l_Event = new EventEntityAdded(p_Entity);

		MinecraftForge.EVENT_BUS.post(l_Event);

		if (l_Event.isCanceled())
			p_Info.cancel();
	}

	@Inject(method = "onEntityRemoved", at = @At("HEAD"), cancellable = true)
	public void onEntityRemoved(Entity p_Entity, CallbackInfo p_Info) {
		EventEntityRemoved l_Event = new EventEntityRemoved(p_Entity);

		MinecraftForge.EVENT_BUS.post(l_Event);

		if (l_Event.isCanceled())
			p_Info.cancel();
	}

}
