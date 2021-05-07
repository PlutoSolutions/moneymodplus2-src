package cat.cattyn.moneymod;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class moneydiscord {
	public static DiscordRichPresence presence;
	private static final club.minnced.discord.rpc.DiscordRPC rpc;
	private static Thread thread;

	static String astate = "crystalpvp for monke";

	public static void setState(final String state) {
		astate = state;
	}

	public static void start() {
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		String server2 = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer"
				: Minecraft.getMinecraft().getCurrentServerData().serverIP;
		if (server2 == null) {
			server2 = "Main Menu";
		}
		rpc.Discord_Initialize("771754886231621652", handlers, true, "");
		presence.startTimestamp = System.currentTimeMillis() / 1000L;
		presence.details = Minecraft.getMinecraft().getSession().getUsername() + " | " + server2;
		presence.state = astate;
		presence.largeImageKey = "money";
		rpc.Discord_UpdatePresence(presence);
		thread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer"
						: Minecraft.getMinecraft().getCurrentServerData().serverIP;
				if (server == null) {
					server = "Main Menu";
				}
				rpc.Discord_RunCallbacks();
				presence.details = Minecraft.getMinecraft().getSession().getUsername() + " | " + server;
				presence.state = (astate);
				rpc.Discord_UpdatePresence(presence);

				try {
					Thread.sleep(2000L);
				} catch (InterruptedException var1) {
				}
			}

		}, "RPC-Callback-Handler");
		thread.start();
	}

	public static void stop() {
		if (thread != null && !thread.isInterrupted()) {
			thread.interrupt();
		}

		rpc.Discord_Shutdown();
	}

	static {
		rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
		presence = new DiscordRichPresence();
	}
}
