package cat.yoink.dream.api.command.commands;

import org.lwjgl.input.Keyboard;

import cat.yoink.dream.api.command.Command;
import cat.yoink.dream.api.command.CommandManager;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.util.CommandUtil;

public class Bind extends Command {
	public Bind() {
		super("Bind", "Set the Bind", new String[] { "bind" });
	}

	@Override
	public void onCommand(String arguments) {
		if (arguments.equals("")) {
			CommandUtil.sendChatMessage(String.format("&7Usage: %sbind <Module> <Key>", CommandManager.getPrefix()));
			return;
		}

		boolean moduleFound = false;

		String[] arg = arguments.split(" ");
		int key = Keyboard.getKeyIndex(arg[1].toUpperCase());

		for (Module module : ModuleManager.getModules()) {
			if (module.getName().equalsIgnoreCase(arg[0])) {
				try {
					if (Keyboard.getKeyName(key).equals("NONE")) {
						module.setBind(Keyboard.KEYBOARD_SIZE);
					} else {
						module.setBind(key);

					}
					CommandUtil.sendChatMessage(
							String.format("bound %s to %s", module.getName(), Keyboard.getKeyName(key)));
					moduleFound = true;
				} catch (Exception ignored) {
				}
			}
		}

		if (!moduleFound) {
			CommandUtil.sendChatMessage("&7Module not found");
		}

	}
}
