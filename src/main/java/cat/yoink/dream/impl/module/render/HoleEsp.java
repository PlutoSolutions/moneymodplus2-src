package cat.yoink.dream.impl.module.render;

import cat.cattyn.moneymod.money;
import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.RenderUtil;
import cat.yoink.dream.impl.event.RenderEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class HoleEsp extends Module {

	public final List<Hole> holes = new ArrayList<>();
	public int radius = 8;

	private final Setting bedred = new Setting.Builder(SettingType.INTEGER).setName("BedrockRed").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting bedgreen = new Setting.Builder(SettingType.INTEGER).setName("BedrockGreen").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting bedblue = new Setting.Builder(SettingType.INTEGER).setName("BedrockBlue").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting bedrainbow = new Setting.Builder(SettingType.BOOLEAN).setName("BedrockRainbow")
			.setModule(this).setBooleanValue(false).build();

	private final Setting obsred = new Setting.Builder(SettingType.INTEGER).setName("ObsRed").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting obsgreen = new Setting.Builder(SettingType.INTEGER).setName("ObsGreen").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting obsblue = new Setting.Builder(SettingType.INTEGER).setName("ObsBlue").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting obsrainbow = new Setting.Builder(SettingType.BOOLEAN).setName("ObskRainbow").setModule(this)
			.setBooleanValue(false).build();

	public HoleEsp(String name, String description, Category category) {
		super(name, description, category);
		addSetting(bedred);
		addSetting(bedgreen);
		addSetting(bedblue);
		addSetting(bedrainbow);
		addSetting(obsred);
		addSetting(obsgreen);
		addSetting(obsblue);
		addSetting(obsrainbow);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (mc.player == null) {
			return;
		}
		this.holes.clear();
		final Vec3i playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);
		for (int x = playerPos.getX() - this.radius; x < playerPos.getX() + this.radius; ++x) {
			for (int z = playerPos.getZ() - this.radius; z < playerPos.getZ() + this.radius; ++z) {
				for (int y = playerPos.getY(); y > playerPos.getY() - 4; --y) {
					final BlockPos blockPos = new BlockPos(x, y, z);
					final IBlockState blockState = mc.world.getBlockState(blockPos);
					if (this.isBlockValid(blockState, blockPos)) {
						this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
					}
				}
			}
		}
	}

	public void bedcycle_rainbow() {

		float[] tick_color = { (System.currentTimeMillis() % (360 * 32)) / (360f * 32) };

		int color_rgb_o = Color.HSBtoRGB(tick_color[0], 1, 1);

		money.settingManager.getSetting("HoleEsp", "BedrockRed").setIntegerValue((color_rgb_o >> 16) & 0xFF);
		money.settingManager.getSetting("HoleEsp", "BedrockGreen").setIntegerValue((color_rgb_o >> 8) & 0xFF);
		money.settingManager.getSetting("HoleEsp", "BedrockBlue").setIntegerValue(color_rgb_o & 0xFF);

	}

	public void obscycle_rainbow() {

		float[] tick_color = { (System.currentTimeMillis() % (360 * 33)) / (360f * 33) };

		int color_rgb_o = Color.HSBtoRGB(tick_color[0], 1, 1);

		money.settingManager.getSetting("HoleEsp", "ObsRed").setIntegerValue((color_rgb_o >> 16) & 0xFF);
		money.settingManager.getSetting("HoleEsp", "ObsGreen").setIntegerValue((color_rgb_o >> 8) & 0xFF);
		money.settingManager.getSetting("HoleEsp", "ObsBlue").setIntegerValue(color_rgb_o & 0xFF);

	}

	@SubscribeEvent
	public void onWorldRender(RenderWorldLastEvent event) {
		for (final Hole hole : this.holes) {
			final AxisAlignedBB bb = new AxisAlignedBB(hole.getX() - mc.getRenderManager().viewerPosX,
					hole.getY() - mc.getRenderManager().viewerPosY, hole.getZ() - mc.getRenderManager().viewerPosZ,
					hole.getX() + 1 - mc.getRenderManager().viewerPosX,
					hole.getY() + (1) - mc.getRenderManager().viewerPosY,
					hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);
			if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX,
					bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ,
					bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY,
					bb.maxZ + mc.getRenderManager().viewerPosZ))) {
				if (isBedrockHole(new BlockPos(hole.getX(), hole.getY(), hole.getZ()))) {
					if (bedrainbow.getBooleanValue()) {
						bedcycle_rainbow();
					}
					RenderUtil.drawESP(bb, bedred.getIntegerValue(), bedgreen.getIntegerValue(),
							bedblue.getIntegerValue(), 35F);
					RenderUtil.drawESPOutline(bb, bedred.getIntegerValue(), bedgreen.getIntegerValue(),
							bedblue.getIntegerValue(), 255f, 1f);
				} else if (isObbyHole(new BlockPos(hole.getX(), hole.getY(), hole.getZ()))
						|| isBothHole(new BlockPos(hole.getX(), hole.getY(), hole.getZ()))) {
					if (obsrainbow.getBooleanValue()) {
						obscycle_rainbow();
					}
					RenderUtil.drawESP(bb, obsred.getIntegerValue(), obsgreen.getIntegerValue(),
							obsblue.getIntegerValue(), 35F);
					RenderUtil.drawESPOutline(bb, obsred.getIntegerValue(), obsgreen.getIntegerValue(),
							obsblue.getIntegerValue(), 255f, 1f);
				}
			}
		}
	}

	private boolean isBlockValid(final IBlockState blockState, final BlockPos blockPos) {
		if (this.holes.contains(blockPos)) {
			return false;
		}
		if (blockState.getBlock() != Blocks.AIR) {
			return false;
		}
		if (mc.player.getDistanceSq(blockPos) < 1.0) {
			return false;
		}
		if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
			return false;
		}
		if (mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR) {
			return false;
		}
		return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
	}

	private boolean isObbyHole(BlockPos blockPos) {
		final BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(),
				blockPos.down() };
		for (final BlockPos touching : touchingBlocks) {
			final IBlockState touchingState = mc.world.getBlockState(touching);
			if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN)
				return false;
		}
		return true;
	}

	private boolean isBedrockHole(BlockPos blockPos) {
		final BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(),
				blockPos.down() };
		for (final BlockPos touching : touchingBlocks) {
			final IBlockState touchingState = mc.world.getBlockState(touching);
			if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK)
				return false;
		}
		return true;
	}

	private boolean isBothHole(BlockPos blockPos) {
		final BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(),
				blockPos.down() };
		for (final BlockPos touching : touchingBlocks) {
			final IBlockState touchingState = mc.world.getBlockState(touching);
			if (touchingState.getBlock() == Blocks.AIR
					|| (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN))
				return false;
		}
		return true;
	}

	private class Hole extends Vec3i {
		Hole(final int x, final int y, final int z) {
			super(x, y, z);
		}
	}

}
