package cat.yoink.dream.api.module;

import com.mojang.realmsclient.gui.ChatFormatting;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.util.CommandUtil;
import cat.yoink.dream.impl.event.RenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class Module {
	private String name;
	private String description;
	private Category category;
	private int bind;
	private boolean enabled;
	private boolean toggled;
	public final Minecraft mc = Minecraft.getMinecraft();

	public Module(String name, Category category) {
		this.name = name;
		this.category = category;
	}

	public Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	public void enable() {
		setEnabled(true);
		onEnable();
		MinecraftForge.EVENT_BUS.register(this);
		if (ModuleManager.getModule("ToggleMSG").isEnabled() && !this.getName().equalsIgnoreCase("ClickGUI")) {
			CommandUtil.sendChatMessage(this.getName() + ChatFormatting.GREEN + " Enabled.");
		}
	}

	public void disable() {
		setEnabled(false);
		onDisable();
		MinecraftForge.EVENT_BUS.unregister(this);
		if (ModuleManager.getModule("ToggleMSG").isEnabled() && !this.getName().equalsIgnoreCase("ClickGUI")) {
			CommandUtil.sendChatMessage(this.getName() + ChatFormatting.RED + " Disabled.");
		}
	}

	public void toggle() {
		if (isEnabled())
			disable();
		else
			enable();
	}

	public boolean isToggled() {
		return toggled;
	}

	public void addSetting(Setting setting) {
		money.settingManager.addSetting(setting);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getBind() {
		return bind;
	}

	public void setBind(int bind) {
		this.bind = bind;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void onWorldRender(RenderEvent event) {

	}
}
