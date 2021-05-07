package cat.yoink.dream.mixin.mixins.mixin;

import cat.yoink.dream.mixin.mixins.accessor.ITimer;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author yoink
 * @since 9/20/2020
 */
@Mixin(Timer.class)
public class TimerMixin implements ITimer {
	@Shadow
	private float tickLength;

	@Override
	public float getTickLength() {
		return tickLength;
	}

	@Override
	public void setTickLength(float tickLength) {
		this.tickLength = tickLength;
	}
}
