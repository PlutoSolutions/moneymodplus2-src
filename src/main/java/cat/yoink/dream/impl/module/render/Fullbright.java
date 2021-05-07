package cat.yoink.dream.impl.module.render;

import java.util.Objects;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Fullbright extends Module {

	private boolean hasEffect = false;
	private final PotionEffect effect = new PotionEffect(Objects.requireNonNull(Potion.getPotionById(16)));

	public Fullbright(String name, String description, Category category) {
		super(name, description, category);
		effect.setPotionDurationMax(true);
	}

	@Override
	public void onEnable() {
		if (mc.player == null)
			return;

		mc.player.addPotionEffect(effect);
		hasEffect = true;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null)
			return;

		if (!hasEffect) {
			mc.player.addPotionEffect(effect);
			hasEffect = true;
		}
	}

	@Override
	public void onDisable() {
		mc.player.removeActivePotionEffect(effect.getPotion());
		hasEffect = false;
	}

}
