package cat.yoink.dream.api.command;

import java.util.ArrayList;

import cat.yoink.dream.api.command.commands.*;
import cat.yoink.dream.api.util.CommandUtil;

public class CommandManager {
	public static ArrayList<Command> commands = new ArrayList<>();
	public static String prefix = ".";

	public static void initialize() {
		// commands.add(new Set());
		commands.add(new Prefix());
		commands.add(new RPC());
		commands.add(new OpenFolder());
		commands.add(new Bind());
		commands.add(new FakePlayer());
		commands.add(new Clip());
		commands.add(new Plugins());
		commands.add(new Friend());
	}

	public static void onCommand(String input) {
		boolean commandFound = false;
		String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
		String startCommand = split[0];
		String args = input.substring(startCommand.length()).trim();

		for (Command command : commands) {
			for (String alias : command.getAlias()) {
				if (startCommand.equals(getPrefix() + alias)) {
					commandFound = true;
					command.onCommand(args);
				}
			}
		}
		if (!commandFound) {
			CommandUtil.sendChatMessage("&cCommand not found");
		}

	}

	public static ArrayList<Command> getCommands() {
		return commands;
	}

	public static String getPrefix() {
		return prefix;
	}

	public static void setPrefix(String prefix) {
		CommandManager.prefix = prefix;
	}
}