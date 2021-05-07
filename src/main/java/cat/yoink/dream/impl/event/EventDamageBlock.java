package cat.yoink.dream.impl.event;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class EventDamageBlock extends Event {
	private BlockPos blockPos;
	private EnumFacing enumFacing;

	public EventDamageBlock(final BlockPos blockPos, final EnumFacing enumFacing) {
		this.blockPos = null;
		this.enumFacing = null;
		this.setBlockPos(blockPos);
		this.setEnumFacing(enumFacing);
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public void setBlockPos(final BlockPos blockPos) {
		this.blockPos = blockPos;
	}

	public EnumFacing getEnumFacing() {
		return this.enumFacing;
	}

	public void setEnumFacing(final EnumFacing enumFacing) {
		this.enumFacing = enumFacing;
	}
}
