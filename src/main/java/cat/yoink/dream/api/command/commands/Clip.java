package cat.yoink.dream.api.command.commands;

import cat.yoink.dream.api.command.Command;
import cat.yoink.dream.api.command.CommandManager;
import cat.yoink.dream.api.util.CommandUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;

public class Clip extends Command {
	public Clip() {
		super("vclip", "vclip", new String[] { "vclip" });
	}

	public Entity getRidingOrPlayer() {
		if (Minecraft.getMinecraft().player == null)
			return null;

		if (Minecraft.getMinecraft().player.getRidingEntity() != null)
			return Minecraft.getMinecraft().player.getRidingEntity();

		return Minecraft.getMinecraft().player;
	}

	// teleport to absolute position
	private void setPosition(double x, double y, double z) {
		final Entity local = getRidingOrPlayer();
		local.setPositionAndUpdate(x, y, z);
		final Minecraft MC = Minecraft.getMinecraft();
		if (local instanceof EntityPlayerSP) {
			Minecraft.getMinecraft().player.connection
					.sendPacket(new CPacketPlayer.Position(local.posX, local.posY, local.posZ, MC.player.onGround));
		} else {
			Minecraft.getMinecraft().player.connection.sendPacket(new CPacketVehicleMove(local));
		}
	}

	// teleport vertically by some offset
	private void offsetY(double yOffset) {
		Entity local = getRidingOrPlayer();
		setPosition(local.posX, local.posY + yOffset, local.posZ);
	}

	@Override
	public void onCommand(String arguments) {

		if (arguments.equals("")) {
			CommandUtil.sendChatMessage(String.format("&7Usage: %svclip [blocks]", CommandManager.getPrefix()));
			return;
		}

		try {
			Double d = Double.parseDouble(arguments.split(" ")[0]);
			Minecraft.getMinecraft().addScheduledTask(() -> offsetY(d));
			CommandUtil.sendChatMessage(String.format("clipped %f blocks", d));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
