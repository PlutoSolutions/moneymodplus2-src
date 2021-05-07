package cat.yoink.dream.api.command.commands;

import java.awt.Desktop;
import java.io.File;

import cat.cattyn.moneymod.moneydiscord;
import cat.yoink.dream.api.command.Command;
import cat.yoink.dream.api.command.CommandManager;
import cat.yoink.dream.api.util.CommandUtil;

public class OpenFolder extends Command {

	public OpenFolder() {
		super("OpenFolder", "OpenFolder", new String[] { "openfolder" });
	}

	@Override
	public void onCommand(String arguments) {
		CommandUtil.sendChatMessage(String.format("opening folder"));
		try {
			Desktop.getDesktop().open(new File("moneymod"));
		} catch (Exception e) {

		}
	}
}
