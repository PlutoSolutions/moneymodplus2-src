package cat.yoink.dream.api.command.commands;

import cat.yoink.dream.api.command.Command;
import cat.yoink.dream.api.module.ModuleManager;

public class Plugins extends Command {

	public Plugins() {
		super("Plugins", "Plugins", new String[] { "plugins", "pl" });
	}

	public void onCommand(String arguments)
	{
		ModuleManager.getModule("PluginsGrabber").enable();
	}

}
