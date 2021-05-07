package cat.yoink.dream.api.command.commands;

import cat.yoink.dream.api.command.Command;
import cat.yoink.dream.api.command.CommandManager;
import cat.yoink.dream.api.util.CommandUtil;

public class Prefix extends Command {
	public Prefix() {
		super("Prefix", "Set the command prefix", new String[] { "prefix" });
	}

	@Override
	public void onCommand(String arguments) {
		if (arguments.equals("")) {
			CommandUtil.sendChatMessage(String.format("&7Usage: %sprefix <Char>", CommandManager.getPrefix()));
		}

		CommandManager.setPrefix(arguments);
		CommandUtil.sendChatMessage(String.format("&7Set prefix to %s", arguments));
	}
}
