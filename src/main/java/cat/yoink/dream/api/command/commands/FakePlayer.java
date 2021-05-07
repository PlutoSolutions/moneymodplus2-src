package cat.yoink.dream.api.command.commands;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.json.alt.simple.JSONObject;
import org.json.alt.simple.parser.JSONParser;

import com.mojang.authlib.GameProfile;

import cat.yoink.dream.api.command.Command;
import cat.yoink.dream.api.command.CommandManager;
import cat.yoink.dream.api.util.CommandUtil;
import cat.yoink.dream.api.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class FakePlayer extends Command {
	private UUID localPlayerUUID;

	public FakePlayer() {
		super("Fakeplayer", "Set the FakePlayer", new String[] { "fakeplayer" });
		try {
			localPlayerUUID = UUID.fromString(getUuid("myrh"));
		} catch (Exception e) {
			localPlayerUUID = UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03");
		}
	}

	EntityOtherPlayerMP fakePlayer;

	@Override
	public void onCommand(String arguments) {
		if (localPlayerUUID == null)
			localPlayerUUID = UUID.fromString(getUuid("myrh"));

		if (arguments.equals("")) {
			CommandUtil
					.sendChatMessage(String.format("&7Usage: %sfakeplayer spawn/remove", CommandManager.getPrefix()));
			return;
		}

		if (arguments.equalsIgnoreCase("spawn")) {
			fakePlayer = null;

			if (Minecraft.getMinecraft().world == null) {
				return;
			}

			try {
				fakePlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().world,
						new GameProfile(localPlayerUUID, "myrh"));
			} catch (Exception e) {
				fakePlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().world,
						new GameProfile(UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03"), "myrh"));
			}

			fakePlayer.copyLocationAndAnglesFrom(Minecraft.getMinecraft().player);
			fakePlayer.rotationYawHead = Minecraft.getMinecraft().player.rotationYawHead;
			Minecraft.getMinecraft().world.addEntityToWorld(-100, fakePlayer);
			CommandUtil.sendChatMessage("fakeplayer spawned");
		}

		else if (arguments.equalsIgnoreCase("remove")) {
			Minecraft.getMinecraft().world.removeEntity(fakePlayer);
			CommandUtil.sendChatMessage("fakeplayer removed");
		}

		else {
			CommandUtil
					.sendChatMessage(String.format("&7Usage: %sfakeplayer spawn/remove", CommandManager.getPrefix()));
			return;
		}
	}

	public static URL getUUIDURL(String name) {
		try {
			URL returnvalue = new URL(name);
			return returnvalue;
		} catch (Exception except) {
			except.printStackTrace();
		}

		return null;
	}

	public static URL[] convertURLToArray(URL url) {
		return new URL[] { url };
	}

	public static String getUuid(String name) {
		JSONParser parser = new JSONParser();
		URL url = convertURLToArray(getUUIDURL("https://api.mojang.com/users/profiles/minecraft/" + name))[0];
		if (url == null)
			return null;

		try {
			String UUIDJson = IOUtils.toString(url, StandardCharsets.UTF_8);
			if (UUIDJson.isEmpty())
				return "invalid name";
			JSONObject UUIDObject = (JSONObject) parser.parse(UUIDJson);
			return reformatUuid(UUIDObject.get("id").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}

	private static String reformatUuid(String uuid) {
		String longUuid = "";

		longUuid += uuid.substring(1, 9) + "-";
		longUuid += uuid.substring(9, 13) + "-";
		longUuid += uuid.substring(13, 17) + "-";
		longUuid += uuid.substring(17, 21) + "-";
		longUuid += uuid.substring(21, 33);

		return longUuid;
	}
}