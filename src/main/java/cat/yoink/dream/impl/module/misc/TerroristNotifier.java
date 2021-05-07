package cat.yoink.dream.impl.module.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.util.CommandUtil;
import cat.yoink.dream.impl.event.EventEntityAdded;
import cat.yoink.dream.impl.event.EventEntityRemoved;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TerroristNotifier extends Module {

	public TerroristNotifier(String name, String description, Category category) {
		super(name, description, category);
	}

	private List<String> Entities = new ArrayList<String>();

	@Override
	public void onEnable() {
		Entities.clear();
	}

	@SubscribeEvent
	public void OnEntityAdded(EventEntityAdded p_Event) {

		if (!VerifyEntity(p_Event.GetEntity()))
			return;

		if (!Entities.contains(p_Event.GetEntity().getName())) {
			Entities.add(p_Event.GetEntity().getName());
			EntityPlayer terrorist = (EntityPlayer) p_Event.GetEntity();
			Entities.add(terrorist.getName().toString());
			if (checkHands(terrorist)) {
				CommandUtil.sendChatMessage("Terrorist found: " + terrorist.getName().toString());
			}
		}
	}

	@SubscribeEvent
	public void OnEntityRemove(EventEntityRemoved p_Event) {
		if (!VerifyEntity(p_Event.GetEntity()))
			return;

		if (Entities.contains(p_Event.GetEntity().getName())) {
			Entities.remove(p_Event.GetEntity().getName());
		}
	}

	private boolean checkHands(EntityPlayer nigga) {
		Item mainHandItem = nigga.getHeldItemMainhand().getItem();
		Item offHandItem = nigga.getHeldItemOffhand().getItem();
		if (mainHandItem == Items.END_CRYSTAL || (mainHandItem instanceof ItemBed && dimensionCheck())) {
			return true;
		} else if (offHandItem == Items.END_CRYSTAL || (offHandItem instanceof ItemBed && dimensionCheck())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean VerifyEntity(Entity p_Entity) {
		if (!(p_Entity instanceof EntityPlayer))
			return false;

		if (p_Entity == mc.player)
			return false;

		return true;
	}

	public boolean dimensionCheck() {
		if (mc.player.dimension == -1 || mc.player.dimension == 1) {
			return true;
		} else {
			return false;
		}
	}

}
