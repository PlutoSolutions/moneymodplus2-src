package cat.yoink.dream.api.command.commands;

import cat.cattyn.moneymod.moneydiscord;
import cat.yoink.dream.api.command.Command;
import cat.yoink.dream.api.command.CommandManager;
import cat.yoink.dream.api.util.CommandUtil;

public class RPC extends Command {

	public RPC() {
		super("rpc", "prikol", new String[] { "rpc" });
	}

	@Override
	public void onCommand(String arguments) {
		if (arguments.equals("")) {
			CommandUtil.sendChatMessage(String.format("&7Usage: %srpc <string>", CommandManager.getPrefix()));
		}

		moneydiscord.setState(arguments);
		CommandUtil.sendChatMessage(String.format("&7Set rpc to %s", arguments));
	}

}
