package cat.yoink.dream.impl.module.misc;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.impl.event.AddCollisionBoxToListEvent;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiFire extends Module {

	public AntiFire(String name, String description, Category category) {
		super(name, description, category);
		// TODO Auto-generated constructor stub
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player != null)
			mc.player.extinguish();
	}

	@SubscribeEvent
	public void onAddCollisionBox(AddCollisionBoxToListEvent event) {
		if (mc.player != null) {
			AxisAlignedBB bb = new AxisAlignedBB(event.getPos()).expand(0, 0.1D, 0);
			if (event.getBlock() == Blocks.FIRE && isAbovePlayer(event.getPos())
					&& event.getEntityBox().intersects(bb)) {
				event.getCollidingBoxes().add(bb);
			}
		}
	}

	private boolean isAbovePlayer(BlockPos pos) {
		return pos.getY() >= mc.player.posY;
	}
}
