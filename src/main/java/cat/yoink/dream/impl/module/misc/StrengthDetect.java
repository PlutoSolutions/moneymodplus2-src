package cat.yoink.dream.impl.module.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import com.mojang.realmsclient.gui.ChatFormatting;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.util.CommandUtil;
import cat.yoink.dream.impl.event.PacketReceiveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StrengthDetect extends Module {

	public static List<EntityPlayer> drinkSet;
	public static List<EntityPlayer> strPlayers;

	public StrengthDetect(String name, String description, Category category) {
		super(name, description, category);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null) {
			return;
		}
		for (final EntityPlayer entityPlayer : mc.world.playerEntities) {
			for (final PotionEffect potionEffect : entityPlayer.getActivePotionEffects()) {
				boolean flag = true;
				if (potionEffect.getPotion() == MobEffects.STRENGTH) {
					strPlayers.add(entityPlayer);
					flag = false;
				}
				if (flag) {
					strPlayers.remove(entityPlayer);
				}
			}
			if (entityPlayer.getHealth() == 0.0f && strPlayers.contains(entityPlayer)) {
				strPlayers.remove(entityPlayer);
			}
		}
	}

	@SubscribeEvent
	public void onPacket(final PacketReceiveEvent event) {
		if (event.getPacket() instanceof SPacketEntityStatus) {
			final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
			if (packet.getOpCode() == 35 && packet.getEntity((World) mc.world) instanceof EntityPlayer) {
				final EntityPlayer entity = (EntityPlayer) packet.getEntity((World) mc.world);
				strPlayers.remove(entity);
				CommandUtil.sendChatMessage(ChatFormatting.AQUA + entity.getName() + " no longer has strength!");
			}
		}
		if (event.getPacket() instanceof SPacketSoundEffect) {
			final SPacketSoundEffect packet2 = (SPacketSoundEffect) event.getPacket();
			if (packet2.getSound().getSoundName().toString().equalsIgnoreCase("minecraft:entity.generic.drink")) {
				final List<EntityPlayer> players = (List<EntityPlayer>) mc.world.getEntitiesWithinAABB(
						(Class) EntityPlayer.class,
						new AxisAlignedBB(packet2.getX() - 1.0, packet2.getY() - 1.0, packet2.getZ() - 1.0,
								packet2.getX() + 1.0, packet2.getY() + 1.0, packet2.getZ() + 1.0));
				EntityPlayer drinker = null;
				if (players.size() > 1) {
					for (final EntityPlayer player : players) {
						if (drinker == null || player.getDistance(packet2.getX(), packet2.getY(),
								packet2.getZ()) < drinker.getDistance(packet2.getX(), packet2.getY(), packet2.getZ())) {
							drinker = player;
						}
					}
				} else {
					drinker = players.get(0);
				}
				if (drinker.getHeldItemMainhand().getItem() instanceof ItemPotion) {
					final List<PotionEffect> effects = (List<PotionEffect>) PotionUtils
							.getEffectsFromStack(drinker.getHeldItemMainhand());
					for (final PotionEffect p : effects) {
						if (p.getPotion() == MobEffects.STRENGTH) {
							drinkSet.add(drinker);
						}
					}
				}
			} else if (packet2.getSound().getSoundName().toString()
					.equalsIgnoreCase("minecraft:item.armor.equip_generic")) {
				final List<EntityPlayer> players = (List<EntityPlayer>) mc.world.getEntitiesWithinAABB(
						(Class) EntityPlayer.class,
						new AxisAlignedBB(packet2.getX() - 1.0, packet2.getY() - 1.0, packet2.getZ() - 1.0,
								packet2.getX() + 1.0, packet2.getY() + 1.0, packet2.getZ() + 1.0));
				EntityPlayer drinker = null;
				if (players.size() > 1) {
					for (final EntityPlayer player : players) {
						if (drinker == null || player.getDistance(packet2.getX(), packet2.getY(),
								packet2.getZ()) < drinker.getDistance(packet2.getX(), packet2.getY(), packet2.getZ())) {
							drinker = player;
						}
					}
				} else {
					drinker = players.get(0);
				}
				if (drinkSet.contains(drinker) && drinker.getHeldItemMainhand().getItem() == Items.GLASS_BOTTLE) {
					strPlayers.add(drinker);
					drinkSet.remove(drinker);
					CommandUtil.sendChatMessage(ChatFormatting.RED + drinker.getName() + " has drank a strength pot!");
				}
			}
		}
	}

	static {
		drinkSet = new ArrayList<EntityPlayer>();
		strPlayers = new ArrayList<EntityPlayer>();
	}

}
