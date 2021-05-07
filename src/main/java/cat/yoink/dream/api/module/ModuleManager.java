package cat.yoink.dream.api.module;

import cat.yoink.dream.impl.module.combat.*;
import cat.yoink.dream.impl.module.exploit.*;
import cat.yoink.dream.impl.module.hud.*;
import cat.yoink.dream.impl.module.misc.*;
import cat.yoink.dream.impl.module.movement.*;
import cat.yoink.dream.impl.module.render.*;

import java.util.ArrayList;

/**
 * @author yoink
 * @since 9/20/2020
 */
public class ModuleManager {
	private final static ArrayList<Module> modules = new ArrayList<>();

	public ModuleManager() {
		modules.add(new ClickGUI("ClickGUI", "Toggle modules by clicking on them", Category.HUD));
		modules.add(
				new CustomFont("CustomFont", "Use a custom font render instead of Minecraft's default", Category.HUD));
		modules.add(new PacketMine("PacketMine", "Mine blocks with packets", Category.EXPLOIT));
		modules.add(new Timer("Timer", "Speeds up your game", Category.MISC));
		modules.add(new Criticals("Criticals", "Deal critical hits without jumping", Category.COMBAT));
		modules.add(new LongJump("LongJump", "Jumps far", Category.MOVEMENT));
		modules.add(new ChatSuffix("ChatSuffix", "Adds a suffix to your chat messages", Category.MISC));
		modules.add(new ActiveModules("ActiveModules", "ActiveModules", Category.HUD));
		// modules.add(new FakePlayer("FakePlayer", "Spawns a FakePlayer",
		// Category.MISC));
		modules.add(new Watermark("Watermark", "Shows modname on screen", Category.HUD));
		modules.add(new Sprint("Sprint", "Toggle sprint", Category.MOVEMENT));
		modules.add(new Fov("Fov", "Change Fov", Category.RENDER));
		modules.add(new PingBypass("PingBypass", "0_0", Category.EXPLOIT));
		modules.add(new BoatFly("BoatFly", "BoatFly++++++++ bypass", Category.MOVEMENT));
		modules.add(new AutoCrystal("AutoCrystal", "U know...", Category.COMBAT));
		modules.add(new ItemViewmodle("ItemViewmodel", "ItemViewmodel", Category.RENDER));
		modules.add(new Velocity("Velocity", "Velocity", Category.COMBAT));
		modules.add(new AutoTotem("AutoTotem", "AutoTotem", Category.COMBAT));
		modules.add(new MultiTask("MultiTask", "MultiTask", Category.COMBAT));
		modules.add(new FastPlace("FastPlace", "FastPlace", Category.EXPLOIT));
		modules.add(new ReverseStep("ReverseStep", "ReverseStep", Category.MOVEMENT));
		modules.add(new NoSlow("NoSlowDown", "NoSlowDown", Category.MOVEMENT));
		modules.add(new PacketXP("FootXP", "FootXP", Category.COMBAT));
		modules.add(new PortalGodMode("PortalGodMode", "PortalGodMode", Category.EXPLOIT));
		modules.add(new CoordsHUD("Position", "Position", Category.HUD));
		modules.add(new AutoRun("AutoRun", "AutoRun", Category.COMBAT));
		modules.add(new Strafe("Strafe", "Strafe", Category.MOVEMENT));
		modules.add(new Step("Step", "Step", Category.MOVEMENT));
		modules.add(new PacketFly("PacketFly", "PacketFly", Category.EXPLOIT));
		modules.add(new Surround("Surround", "Surround", Category.COMBAT));
		modules.add(new Burrow("Burrow", "Burrow", Category.COMBAT));
		modules.add(new Skeleton("Skeleton", "Skeleton", Category.RENDER));
		modules.add(new Fullbright("Fullbright", "Fullbright", Category.RENDER));
		modules.add(new AutoArmor("AutoArmor", "AutoArmor", Category.COMBAT));
		modules.add(new FastBow("FastBow", "FastBow", Category.COMBAT));
		modules.add(new NoWeather("NoWeather", "NoWeather", Category.RENDER));
		modules.add(new SkyColor("SkyColor", "SkyColor", Category.RENDER));
		modules.add(new SkinBlinker("SkinBlinker", "SkinBlinker", Category.MISC));
		modules.add(new DiscordRPC("DiscordRPC", "DiscordRPC", Category.MISC));
		modules.add(new LowHands("LowHands", "LowHands", Category.RENDER));
		modules.add(new BedAura("BedAura", "BedAura", Category.COMBAT));
		modules.add(new SelfWeb("SelfWeb", "SelfWeb", Category.COMBAT));
		modules.add(new LavaFlight("LavaFlight", "LavaFlight", Category.EXPLOIT));
		// modules.add(new NewAutoCrystal("NewAutoCrystal", "NewAutoCrystal",
		// Category.COMBAT));
		modules.add(new ToggleMSG("ToggleMSG", "ToggleMSG", Category.MISC));
		modules.add(new Anchor("Anchor", "Anchor", Category.COMBAT));
		modules.add(new Offhand("Offhand", "Offhand", Category.COMBAT));
		modules.add(new AutoTrap("AutoTrap", "AutoTrap", Category.COMBAT));
		modules.add(new AutoTNTCart("AutoTNTCart", "AutoTNTCart", Category.COMBAT));
		modules.add(new AntiBlind("AntiBlind", "AntiBlind", Category.MISC));
		modules.add(new AntiLevitate("AntiLevitate", "AntiLevitate", Category.MISC));
		modules.add(new NoVoid("NoVoid", "NoVoid", Category.EXPLOIT));
		modules.add(new AntiEndCrystal("AntiEndCrystal", "AntiEndCrystal", Category.EXPLOIT));
		modules.add(new InventoryMove("InventoryMove", "InventoryMove", Category.MOVEMENT));
		modules.add(new AutoSwim("AutoSwim", "AutoSwim", Category.MOVEMENT));
		modules.add(new AutoWalk("AutoWalk", "AutoWalk", Category.MOVEMENT));
		modules.add(new AntiFire("AntiFire", "AntiFire", Category.MISC));
		modules.add(new AntiCactus("AntiCactus", "AntiCactus", Category.MISC));
		modules.add(new AntiFog("AntiFog", "AntiFog", Category.MISC));
		modules.add(new NoGlitchBlocks("NoGlitchBlocks", "NoGlitchBlocks", Category.MISC));
		modules.add(new AntiOverlay("AntiOverlay", "AntiOverlay", Category.RENDER));
		modules.add(new Blink("Blink", "Blink", Category.COMBAT));
		modules.add(new XCarry("XCarry", "XCarry", Category.EXPLOIT));
		modules.add(new Compass("Compass", "Compass", Category.HUD));
		modules.add(new KillAura("KillAura", "KillAura", Category.COMBAT));
		modules.add(new BedAuraEcMe("1.13 BedAura", "1.13 BedAura", Category.COMBAT));
		modules.add(new AutoRespawn("AutoRespawn", "AutoRespawn", Category.MISC));
		modules.add(new BoatClip("BoatClip", "BoatClip", Category.EXPLOIT));
		modules.add(new FakeGamemode("FakeGamemode", "FakeGamemode", Category.MISC));
		modules.add(new NoBob("NoBob", "NoBob", Category.RENDER));
		modules.add(new FastSwim("FastSwim", "FastSwim", Category.MOVEMENT));
		modules.add(new NewOffhand("NewOffhand", "NewOffhand", Category.COMBAT));
		modules.add(new AntiSurround("AntiSurround", "AntiSurround", Category.EXPLOIT));
		// modules.add(new HoleFiller("HoleFiller","HoleFiller",Category.COMBAT));
		modules.add(new AutoLog("AutoLog", "AutoLog", Category.COMBAT));
		modules.add(new Chams("Chams", "Chams", Category.RENDER));
		modules.add(new NoSoundLag("NoSoundLag", "Lag Exploit Fix", Category.MISC));
		modules.add(new CameraClip("CameraClip", "CameraClip", Category.RENDER));
		modules.add(new NoHurtCam("NoHurtCam", "NoHurtCam", Category.RENDER));
		modules.add(new EnchantColor("EnchantColor", "EnchantColor", Category.RENDER));
		modules.add(new NoBreakAnimation("NoBreakAnimation", "NoBreakAnimation", Category.EXPLOIT));
		modules.add(new PortalChat("PortalChat", "Allows you to chat in portals", Category.MISC));
		modules.add(new AntiHunger("AntiHunger", "Prevents you from getting hungry", Category.EXPLOIT));
		modules.add(new OldStrafe("OldStrafe", "OldStrafe", Category.MOVEMENT));
		modules.add(new HoleFiller("HoleFiller", "Fills holes", Category.COMBAT));
		modules.add(new Auto32k("Auto32k", "Auto 32k", Category.COMBAT));
		modules.add(new OffhandSwing("OffhandSwing", "OffhandSwing", Category.EXPLOIT));
		modules.add(new BackTP("BackTP", "BackTP", Category.EXPLOIT));
		modules.add(new AirJump("AirJump", "AirJump", Category.MOVEMENT));
		modules.add(new ForceCrash("ForceCrash", "ForceCrash", Category.MISC));
		modules.add(new PluginsGrabber("PluginsGrabber", "PluginsGrabber", Category.EXPLOIT));
		modules.add(new Flight("Flight", "Flight", Category.MOVEMENT));
		modules.add(new SelfTrap("SelfTrap", "SelfTrap", Category.COMBAT));
		modules.add(new ShulkerPreview("ShulkerPreview", "ShulkerPreview", Category.RENDER));
		modules.add(new StrengthDetect("StrengthDetect", "StrengthDetect", Category.MISC));
		modules.add(new HoleEsp("HoleEsp", "HoleEsp", Category.RENDER));
		modules.add(new YPort("YPort", "YPort", Category.MOVEMENT));
		modules.add(new PvPInfo("PvPInfo", "PvPInfo", Category.HUD));
		modules.add(new BoatPlaceBypass("BoatPlaceBypass", "BoatPlaceBypass", Category.EXPLOIT));
		modules.add(new GreenText("GreenText", "GreenText", Category.MISC));
		modules.add(new NameTags("NameTags", "NameTags", Category.RENDER));
		modules.add(new TerroristNotifier("TerroristNotifier", "TerroristNotifier", Category.MISC));
		// modules.add(new PotionUI("PotionUI","PotionUI",Category.HUD));
	}

//StrengthDetect
	public static ArrayList<Module> getModules() {
		return modules;
	}

	public static void onRender() {
		modules.stream().filter(Module::isEnabled);
	}

	public static Module getModule(String name) {
		for (Module module : modules) {
			if (module.getName().equalsIgnoreCase(name))
				return module;
		}

		return null;
	}

	public ArrayList<Module> getModules(Category category) {
		ArrayList<Module> mods = new ArrayList<>();

		for (Module module : modules) {
			if (module.getCategory().equals(category))
				mods.add(module);
		}

		return mods;
	}
}
