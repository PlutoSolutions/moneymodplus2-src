package cat.yoink.dream.impl.module.movement;

import java.util.ArrayList;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.impl.event.WalkEvent;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Anchor extends Module {

	private final Setting Pitch = new Setting.Builder(SettingType.INTEGER).setName("Max Y lvl").setModule(this)
			.setIntegerValue(60).setMinIntegerValue(0).setMaxIntegerValue(90).build();

	private final Setting pull = new Setting.Builder(SettingType.BOOLEAN).setName("Pull").setModule(this)
			.setBooleanValue(false).build();

	public Anchor(String name, String description, Category category) {
		super(name, description, category);
		addSetting(Pitch);
		addSetting(pull);
	}

	private final ArrayList<BlockPos> holes = new ArrayList<BlockPos>();
	int holeblocks;

	public static boolean AnchorING;

	public boolean isBlockHole(BlockPos blockpos) {
		holeblocks = 0;
		if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN
				|| mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN
				|| mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN
				|| mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN
				|| mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK)
			++holeblocks;

		if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN
				|| mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK)
			++holeblocks;

		if (holeblocks >= 9)
			return true;
		else
			return false;
	}

	private Vec3d Center = Vec3d.ZERO;

	public Vec3d GetCenter(double posX, double posY, double posZ) {
		double x = Math.floor(posX) + 0.5D;
		double y = Math.floor(posY);
		double z = Math.floor(posZ) + 0.5D;

		return new Vec3d(x, y, z);
	}

	@SubscribeEvent
	public void onUpdateWalkingPlayer(WalkEvent event) {
		if (mc.player == null)
			return;
		if (mc.player.rotationPitch >= Pitch.getIntegerValue()) {

			if (isBlockHole(getPlayerPos().down(1)) || isBlockHole(getPlayerPos().down(2))
					|| isBlockHole(getPlayerPos().down(3)) || isBlockHole(getPlayerPos().down(4))) {
				AnchorING = true;

				if (!pull.getBooleanValue()) {
					mc.player.motionX = 0.0;
					mc.player.motionZ = 0.0;
				} else {
					Center = GetCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
					double XDiff = Math.abs(Center.x - mc.player.posX);
					double ZDiff = Math.abs(Center.z - mc.player.posZ);

					if (XDiff <= 0.1 && ZDiff <= 0.1) {
						Center = Vec3d.ZERO;
					} else {
						double MotionX = Center.x - mc.player.posX;
						double MotionZ = Center.z - mc.player.posZ;

						mc.player.motionX = MotionX / 2;
						mc.player.motionZ = MotionZ / 2;
					}
				}
			} else
				AnchorING = false;
		}
	}

	public void onDisable() {
		AnchorING = false;
		holeblocks = 0;
	}

	public BlockPos getPlayerPos() {
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}
}
