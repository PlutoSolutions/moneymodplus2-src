package cat.yoink.dream.mixin.mixins.mixin;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mixin(value = Block.class)
public class BlockMixin {

	@Inject(method = "addCollisionBoxToList", at = @At("HEAD"), cancellable = true)
	private void CollistionList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState,
			CallbackInfo callback) {
		cat.yoink.dream.impl.event.AddCollisionBoxToListEvent collisionBoxEvent = new cat.yoink.dream.impl.event.AddCollisionBoxToListEvent(
				state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
		MinecraftForge.EVENT_BUS.post(collisionBoxEvent);
		// EventManager.call(collisionBoxEvent);
		if (collisionBoxEvent.isCanceled()) {
			callback.cancel();
		}
	}
}