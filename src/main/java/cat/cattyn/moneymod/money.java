package cat.cattyn.moneymod;

import cat.yoink.dream.api.EventHandler;
import cat.yoink.dream.api.command.CommandManager;
import cat.yoink.dream.api.friend.Friends;
import cat.yoink.dream.api.gui.clickgui.ClickGUI;
import cat.yoink.dream.api.module.ModuleManager;
import cat.yoink.dream.api.setting.SettingManager;
import cat.yoink.dream.api.util.ConfigUtils;
import cat.yoink.dream.api.util.font.CustomFontRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.awt.*;

import org.json.alt.simple.parser.JSONParser;
import org.lwjgl.opengl.Display;

/**
 * @author yoink
 * @since 9/20/2020
 */
@Mod(modid = "moneymod+", name = "Moneymod+", version = "2")
public class money {
	public static money INSTANCE;

	public money() {
		money.INSTANCE = this;
	}

	public static String MODID = "moneymod+";
	public static String MODNAME = "Moneymod+";
	public static String MODVER = "2";
	public static String credits = "yoink, cattyn, asphyxia uwu";
	public static ModuleManager moduleManager;
	public static SettingManager settingManager;
	public static CustomFontRenderer customFontRenderer;
	public static ClickGUI clickGUI;
	public static CommandManager commandManager;
	public static ConfigUtils configUtils;
	public Friends friends;

	@Mod.EventHandler
	public void initialize(FMLInitializationEvent event) {
		this.friends = new Friends();
		settingManager = new SettingManager();
		moduleManager = new ModuleManager();
		customFontRenderer = new CustomFontRenderer(new Font("Verdana", Font.PLAIN, 18), true, false);
		clickGUI = new ClickGUI();
		CommandManager.initialize();
		configUtils = new ConfigUtils();

		MinecraftForge.EVENT_BUS.register(new EventHandler());

		Display.setTitle(MODID + " " + MODVER);
	}

	public static money getInstance() {
		return money.INSTANCE;
	}
}
