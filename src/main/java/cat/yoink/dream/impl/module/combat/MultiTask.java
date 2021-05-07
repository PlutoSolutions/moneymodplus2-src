package cat.yoink.dream.impl.module.combat;

import org.lwjgl.input.Mouse;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class MultiTask extends Module {

	public MultiTask(String name, String description, Category category) {
		super(name, description, category);
	}

	@SubscribeEvent
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		if (Mouse.getEventButtonState() && mc.player != null
				&& mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.ENTITY) && mc.player.isHandActive()
				&& (mc.gameSettings.keyBindAttack.isPressed()
						|| Mouse.getEventButton() == mc.gameSettings.keyBindAttack.getKeyCode())) {
			mc.playerController.attackEntity(mc.player, mc.objectMouseOver.entityHit);
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
	}

}
