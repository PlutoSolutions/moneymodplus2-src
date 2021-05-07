package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.RenderUtil;
import cat.yoink.dream.api.util.font.FontUtil;
import cat.yoink.dream.impl.event.PacketReceiveEvent;
import cat.yoink.dream.impl.event.PacketSendEvent;
import cat.yoink.dream.impl.event.RenderEvent;
import cat.yoink.dream.mixin.mixins.accessor.ICPacketPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;

public class NewAutoCrystal extends Module {

	private BlockPos render;
	private Entity renderEnt;
	private boolean switchCooldown = false;
	private boolean isAttacking = false;
	private boolean isPlacing = false;
	private boolean isBreaking = false;
	private int oldSlot = -1;
	private int newSlot;
	private int waitCounter;
	EnumFacing f;
	private static boolean togglePitch = false;
	private final ArrayList<BlockPos> PlacedCrystals = new ArrayList<BlockPos>();

	public boolean isActive = false;
	private long breakSystemTime;

	private final Setting mode = new Setting.Builder(SettingType.ENUM).setName("Mode").setModule(this)
			.setEnumValue("Always").setEnumValues(new ArrayList<>(Arrays.asList("Always", "Smart", "Only Own")))
			.build();

	private final Setting explode = new Setting.Builder(SettingType.BOOLEAN).setName("Explode").setModule(this)
			.setBooleanValue(true).build();

	private final Setting antiWeakness = new Setting.Builder(SettingType.BOOLEAN).setName("Anti Weakness")
			.setModule(this).setBooleanValue(false).build();

	private final Setting place = new Setting.Builder(SettingType.BOOLEAN).setName("Place").setModule(this)
			.setBooleanValue(true).build();

	private final Setting raytrace = new Setting.Builder(SettingType.BOOLEAN).setName("Raytrace").setModule(this)
			.setBooleanValue(false).build();

	private final Setting rotate = new Setting.Builder(SettingType.BOOLEAN).setName("Rotate").setModule(this)
			.setBooleanValue(true).build();

	private final Setting spoofRotation = new Setting.Builder(SettingType.BOOLEAN).setName("Spoof Rotate")
			.setModule(this).setBooleanValue(false).build();

	private final Setting chat = new Setting.Builder(SettingType.BOOLEAN).setName("Toggle MSG").setModule(this)
			.setBooleanValue(true).build();

	private final Setting showDamage = new Setting.Builder(SettingType.BOOLEAN).setName("Show DMG").setModule(this)
			.setBooleanValue(true).build();

	private final Setting cacheBreak = new Setting.Builder(SettingType.BOOLEAN).setName("CacheBreak").setModule(this)
			.setBooleanValue(false).build();

	private final Setting singlePlace = new Setting.Builder(SettingType.BOOLEAN).setName("Single Place").setModule(this)
			.setBooleanValue(true).build();

	private final Setting antiSuicide = new Setting.Builder(SettingType.BOOLEAN).setName("No Suicide").setModule(this)
			.setBooleanValue(true).build();

	private final Setting autoSwitch = new Setting.Builder(SettingType.BOOLEAN).setName("AutoSwitch").setModule(this)
			.setBooleanValue(false).build();

	private final Setting endCrystalMode = new Setting.Builder(SettingType.BOOLEAN).setName("1.13").setModule(this)
			.setBooleanValue(false).build();

	private final Setting placeDelay = new Setting.Builder(SettingType.INTEGER).setName("Place Delay").setModule(this)
			.setIntegerValue(2).setMinIntegerValue(0).setMaxIntegerValue(20).build();

	private final Setting antiSuicideValue = new Setting.Builder(SettingType.INTEGER).setName("Stop Health")
			.setModule(this).setIntegerValue(10).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	private final Setting facePlace = new Setting.Builder(SettingType.INTEGER).setName("Faceplace").setModule(this)
			.setIntegerValue(8).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	private final Setting attackSpeed = new Setting.Builder(SettingType.INTEGER).setName("Attack Speed").setModule(this)
			.setIntegerValue(12).setMinIntegerValue(1).setMaxIntegerValue(20).build();

	private final Setting maxSelfDmg = new Setting.Builder(SettingType.INTEGER).setName("Max Self DMG").setModule(this)
			.setIntegerValue(8).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	private final Setting minBreakDmg = new Setting.Builder(SettingType.INTEGER).setName("Min Break DMG")
			.setModule(this).setIntegerValue(6).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	private final Setting enemyRange = new Setting.Builder(SettingType.INTEGER).setName("Enemy Range").setModule(this)
			.setIntegerValue(6).setMinIntegerValue(1).setMaxIntegerValue(12).build();

	private final Setting walls = new Setting.Builder(SettingType.INTEGER).setName("Walls Range").setModule(this)
			.setIntegerValue(4).setMinIntegerValue(0).setMaxIntegerValue(10).build();

	private final Setting minDmg = new Setting.Builder(SettingType.INTEGER).setName("Min DMG").setModule(this)
			.setIntegerValue(6).setMinIntegerValue(0).setMaxIntegerValue(36).build();

	public final Setting range = new Setting.Builder(SettingType.INTEGER).setName("Hit Range").setModule(this)
			.setIntegerValue(5).setMinIntegerValue(1).setMaxIntegerValue(12).build();

	private final Setting placeRange = new Setting.Builder(SettingType.INTEGER).setName("Place Range").setModule(this)
			.setIntegerValue(5).setMinIntegerValue(1).setMaxIntegerValue(12).build();

	private final Setting offHandBreak = new Setting.Builder(SettingType.BOOLEAN).setName("Offhand Break")
			.setModule(this).setBooleanValue(false).build();

	private final Setting singleTick = new Setting.Builder(SettingType.BOOLEAN).setName("Single Tick").setModule(this)
			.setBooleanValue(false).build();

	private final Setting cancelCrystal = new Setting.Builder(SettingType.BOOLEAN).setName("Cancel Crystal")
			.setModule(this).setBooleanValue(false).build();

	private final Setting red = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting green = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting blue = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting alpha = new Setting.Builder(SettingType.INTEGER).setName("Alpha").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public NewAutoCrystal(String name, String description, Category category) {
		super(name, description, category);
		addSetting(mode);
		addSetting(place);
		addSetting(explode);
		addSetting(rotate);
		addSetting(spoofRotation);
		addSetting(raytrace);
		addSetting(antiWeakness);
		addSetting(chat);
		addSetting(showDamage);
		addSetting(cacheBreak);
		addSetting(antiSuicide);
		addSetting(singlePlace);
		addSetting(autoSwitch);
		addSetting(endCrystalMode);
		addSetting(placeDelay);
		addSetting(antiSuicideValue);
		addSetting(facePlace);
		addSetting(attackSpeed);
		addSetting(maxSelfDmg);
		addSetting(minBreakDmg);
		addSetting(antiWeakness);
		addSetting(enemyRange);
		addSetting(walls);
		addSetting(minDmg);
		addSetting(singleTick);
		addSetting(range);
		addSetting(placeRange);
		addSetting(offHandBreak);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(alpha);
		addSetting(cancelCrystal);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		isActive = false;
		isBreaking = false;
		isPlacing = false;
		BlockPos hitBlock = null;

		if (mc.player == null || mc.player.isDead) {
			return;
		} // bruh

		EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
				.filter(entity -> entity instanceof EntityEnderCrystal)
				.filter(e -> mc.player.getDistance(e) <= range.getIntegerValue()).filter(e -> crystalCheck(e))
				.map(entity -> (EntityEnderCrystal) entity).min(Comparator.comparing(c -> mc.player.getDistance(c)))
				.orElse(null);
		if (explode.getBooleanValue() && crystal != null) {

			// Anti suicide
			if (antiSuicide.getBooleanValue()
					&& (mc.player.getHealth() + mc.player.getAbsorptionAmount()) < antiSuicideValue.getIntegerValue()) {
				return;
			}
			// Walls Range
			if (!mc.player.canEntityBeSeen(crystal) && mc.player.getDistance(crystal) > walls.getIntegerValue()) {
				return;
			}

			// Anti Weakness
			if (antiWeakness.getBooleanValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
				if (!isAttacking) {
					// save initial player hand
					oldSlot = mc.player.inventory.currentItem;
					isAttacking = true;
				}
				// search for sword and tools in hotbar
				newSlot = -1;
				for (int i = 0; i < 9; i++) {
					ItemStack stack = mc.player.inventory.getStackInSlot(i);
					if (stack == ItemStack.EMPTY) {
						continue;
					}
					if ((stack.getItem() instanceof ItemSword)) {
						newSlot = i;
						break;
					}
					if ((stack.getItem() instanceof ItemTool)) {
						newSlot = i;
						break;
					}
				}
				// check if any swords or tools were found
				if (newSlot != -1) {
					mc.player.inventory.currentItem = newSlot;
					switchCooldown = true;
				}
			}

			if (System.nanoTime() / 1000000L - breakSystemTime >= 420 - attackSpeed.getIntegerValue() * 20) {

				isActive = true;
				isBreaking = true;

				if (rotate.getBooleanValue()) {
					lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
				}

				mc.playerController.attackEntity(mc.player, crystal);
			}
			/**
			 * @Author Hoosiers Pretty WIP, but it seems to make the CA much faster
			 */
			else if (offHandBreak.getBooleanValue() && !mc.player.getHeldItemOffhand().isEmpty()) {
				mc.player.swingArm(EnumHand.OFF_HAND);
				if (cancelCrystal.getBooleanValue()) {
					crystal.setDead();
					mc.world.removeAllEntities();
					mc.world.getLoadedEntityList();
				}

			} else {
				mc.player.swingArm(EnumHand.MAIN_HAND);
				if (cancelCrystal.getBooleanValue()) {
					crystal.setDead();
					mc.world.removeAllEntities();
					mc.world.getLoadedEntityList();
				}

			}
			if (cancelCrystal.getBooleanValue()) {
				crystal.setDead();
				mc.world.removeAllEntities();
				mc.world.getLoadedEntityList();
			}

			breakSystemTime = System.nanoTime() / 1000000L;
			isActive = false;
			isBreaking = false;
		}
		if (!singlePlace.getBooleanValue()) {
			return;
		} else {
			resetRotation();
			if (oldSlot != -1) {
				mc.player.inventory.currentItem = oldSlot;
				oldSlot = -1;
			}
			isAttacking = false;
			isActive = false;
			isBreaking = false;
		}

		int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL
				? mc.player.inventory.currentItem
				: -1;
		if (crystalSlot == -1) {
			for (int l = 0; l < 9; ++l) {
				if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
					crystalSlot = l;
					break;
				}
			}
		}
		boolean offhand = false;
		if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
			offhand = true;
		} else if (crystalSlot == -1) {
			return;
		}

		List<BlockPos> blocks = findCrystalBlocks();
		List<Entity> entities = new ArrayList<>();

		BlockPos q = null;
		double damage = 0.5D;
		Iterator var9 = entities.iterator();

		label164: while (true) {
			EntityPlayer entity;
			do {
				do {
					if (!var9.hasNext()) {
						if (damage == 0.5D) {
							this.render = null;
							this.renderEnt = null;
							resetRotation();
							return;
						}

						this.render = q;
						if (this.place.getBooleanValue()) {
							if (antiSuicide.getBooleanValue() && mc.player.getHealth()
									+ mc.player.getAbsorptionAmount() < antiSuicideValue.getIntegerValue()) {
								return;
							}
							if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
								if (this.autoSwitch.getBooleanValue()) {
									mc.player.inventory.currentItem = crystalSlot;
									resetRotation();
									this.switchCooldown = true;
								}
								return;
							}

							if (rotate.getBooleanValue()) {
								this.lookAtPacket((double) q.getX() + 0.5D, (double) q.getY() - 0.5D,
										(double) q.getZ() + 0.5D, mc.player);
							}
							RayTraceResult result = mc.world.rayTraceBlocks(
									new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(),
											mc.player.posZ),
									new Vec3d((double) q.getX() + 0.5D, (double) q.getY() - 0.5D,
											(double) q.getZ() + 0.5D));
							if (raytrace.getBooleanValue()) {
								if (result == null || result.sideHit == null) {
									q = null;
									f = null;
									render = null;
									resetRotation();
									isActive = false;
									isPlacing = false;
									return;
								} else {
									f = result.sideHit;
								}
							}

							if (this.switchCooldown) {
								this.switchCooldown = false;
								return;
							}

							mc.playerController.processRightClickBlock(mc.player, mc.world, q, f, new Vec3d(0, 0, 0),
									EnumHand.MAIN_HAND);
							if (q != null && mc.player != null) {
								isActive = true;
								isPlacing = true;
								if (raytrace.getBooleanValue() && f != null) {

									mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f,
											offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
								} else if (q.getY() == 255) {
									// For Hoosiers. This is how we do buildheight. If the target block (q) is at Y
									// 255. Then we send a placement packet to the bottom part of the block. Thus
									// the EnumFacing.DOWN.
									mc.player.connection
											.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.DOWN,
													offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
								} else {
									mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f,
											offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
								}

								EntityEnderCrystal cry = mc.world.loadedEntityList.stream()
										.filter(ent -> ent instanceof EntityEnderCrystal)
										.filter(e -> mc.player.getDistance(e) <= range.getIntegerValue())
										.filter(e -> crystalCheck(e)).map(ent -> (EntityEnderCrystal) ent)
										.min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);

								if (singleTick.getBooleanValue()) {
									lookAtPacket(q.getX(), q.getY(), q.getZ(), mc.player);

									mc.player.swingArm(EnumHand.MAIN_HAND);
									mc.player.swingArm(EnumHand.MAIN_HAND);
									mc.player.swingArm(EnumHand.MAIN_HAND);
									mc.player.swingArm(EnumHand.MAIN_HAND);
									mc.player.swingArm(EnumHand.MAIN_HAND);
									mc.player.swingArm(EnumHand.MAIN_HAND);
									mc.playerController.attackEntity(mc.player, cry);

								}

								mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
								// Cache the crystals we've placed
								PlacedCrystals.add(q);
								/*
								 * if (ModuleManager.isModuleEnabled("AutoGG")) {
								 * AutoGG.INSTANCE.addTargetedPlayer(renderEnt.getName()); } if
								 * (ModuleManager.isModuleEnabled("Target Hud"));{
								 * TargetHud.INSTANCE.setTarget(renderEnt); }
								 */
							}

							if (isSpoofingAngles) {
								EntityPlayerSP var10000;
								if (togglePitch) {
									var10000 = mc.player;
									var10000.rotationPitch = (float) ((double) var10000.rotationPitch + 4.0E-4D);
									togglePitch = false;
								} else {
									var10000 = mc.player;
									var10000.rotationPitch = (float) ((double) var10000.rotationPitch - 4.0E-4D);
									togglePitch = true;
								}
							}

							return;
						}
					}

					entity = (EntityPlayer) var9.next();
				} while (entity != mc.player);
			} while (entity.getHealth() > 0.0F);

			Iterator var11 = blocks.iterator();

			while (true) {
				BlockPos blockPos;
				double d;
				double self;
				double targetDamage;
				float targetHealth;
				double x;
				double y;
				double z;
				do {
					do {
						do {
							do {
								double b;
								do {
									if (!var11.hasNext()) {
										continue label164;
									}

									blockPos = (BlockPos) var11.next();
									b = entity.getDistanceSq(blockPos);
									// Better method for doing EnemyRange
									// @Author Cyber
									x = blockPos.getX() + 0.0;
									y = blockPos.getY() + 1.0;
									z = blockPos.getZ() + 0.0;
									// } while (b >= 169.0D);
								} while (entity.getDistanceSq(x, y, z) >= enemyRange.getIntegerValue()
										* enemyRange.getIntegerValue());

								d = calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1,
										(double) blockPos.getZ() + 0.5D, entity);
							} while (d <= damage);
							targetDamage = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1,
									blockPos.getZ() + 0.5, entity);
							targetHealth = entity.getHealth() + entity.getAbsorptionAmount();
						} while (targetDamage < minDmg.getIntegerValue() && targetHealth > facePlace.getIntegerValue());
						self = calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1,
								(double) blockPos.getZ() + 0.5D, mc.player);
					} while (self >= maxSelfDmg.getIntegerValue());
				} while (self >= mc.player.getHealth() + mc.player.getAbsorptionAmount());

				damage = d;
				q = blockPos;
				renderEnt = entity;
			}
		}
	}

	public void onWorldRender(RenderEvent event) {
		if (this.render != null) {

			RenderUtil.prepare(7);

			RenderUtil.release();
			RenderUtil.prepare(7);
			RenderUtil.release();
		}

		if (showDamage.getBooleanValue()) {
			if (this.render != null && this.renderEnt != null) {
				GlStateManager.pushMatrix();
				RenderUtil.glBillboardDistanceScaled((float) render.getX() + 0.5f, (float) render.getY() + 0.5f,
						(float) render.getZ() + 0.5f, mc.player, 1);
				double d = calculateDamage(render.getX() + .5, render.getY() + 1, render.getZ() + .5, renderEnt);
				String damageText = (Math.floor(d) == d ? (int) d : String.format("%.1f", d)) + "";
				GlStateManager.disableDepth();
				GlStateManager.translate(-(mc.fontRenderer.getStringWidth(damageText) / 2.0d), 0, 0);
				// mc.fontRenderer.drawStringWithShadow(damageText, 0, 0, 0xFFffffff);
				FontUtil.drawStringWithShadow2(false, damageText, 0, 0, new Color(255, 255, 255).getRGB());
				GlStateManager.popMatrix();
			}
		}
	}

	private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
		double[] v = calculateLookAt(px, py, pz, me);
		setYawAndPitch((float) v[0], (float) v[1]);
	}

	// Bruh why did I never think of just using booleans, this was so much easier
	// than
	// the previous chinese implementation I did.
	/**
	 * @Author CyberTF2.
	 */
	private boolean crystalCheck(Entity crystal) {

		if (!(crystal instanceof EntityEnderCrystal)) {
			return false;
		}

		if (mode.getEnumValue().equalsIgnoreCase("Always")) {
			return true;
		}

		if (mode.getEnumValue().equalsIgnoreCase("Only Own")) {
			for (BlockPos pos : new ArrayList<BlockPos>(PlacedCrystals)) {
				if (pos != null && pos.getDistance((int) crystal.posX, (int) crystal.posY, (int) crystal.posZ) <= range
						.getIntegerValue())
					return true;
			}
		}

		if (mode.getEnumValue().equalsIgnoreCase("Smart")) {

			EntityLivingBase target = renderEnt != null ? (EntityLivingBase) renderEnt : GetNearTarget(crystal);

			if (target == null)
				return false;

			float targetDmg = calculateDamage(crystal.posX + 0.5, crystal.posY + 1, crystal.posZ + 0.5, target);

			/*
			 * if (targetDmg >= minDmg.getValue() && selfDmg < maxSelfDmgB.getValue())
			 * return true;
			 */

			return targetDmg >= minBreakDmg.getIntegerValue()
					|| (targetDmg > minBreakDmg.getIntegerValue()) && target.getHealth() > facePlace.getIntegerValue();
		}

		return false;
	}

	private boolean validTarget(Entity entity) {
		if (entity == null)
			return false;

		if (!(entity instanceof EntityLivingBase))
			return false;

		if (entity.isDead || ((EntityLivingBase) entity).getHealth() <= 0.0F)
			return false;

		if (entity instanceof EntityPlayer) {
			return entity != mc.player;
		}

		return false;
	}

	private EntityLivingBase GetNearTarget(Entity distanceTarget) {
		return mc.world.loadedEntityList.stream().filter(entity -> validTarget(entity))
				.map(entity -> (EntityLivingBase) entity)
				.min(Comparator.comparing(entity -> distanceTarget.getDistance(entity))).orElse(null);
	}

	public boolean canPlaceCrystal(BlockPos blockPos) {
		BlockPos boost = blockPos.add(0, 1, 0);
		BlockPos boost2 = blockPos.add(0, 2, 0);
		if (!endCrystalMode.getBooleanValue())
			return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
					|| mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
					&& mc.world.getBlockState(boost).getBlock() == Blocks.AIR
					&& mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
					&& mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
					&& mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
		else
			return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
					|| mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
					&& mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
					&& mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
	}

	public static BlockPos getPlayerPos() {
		return new BlockPos(Math.floor(Minecraft.getMinecraft().player.posX),
				Math.floor(Minecraft.getMinecraft().player.posY), Math.floor(Minecraft.getMinecraft().player.posZ));
	}

	private List<BlockPos> findCrystalBlocks() {
		NonNullList<BlockPos> positions = NonNullList.create();
		positions.addAll(
				getSphere(getPlayerPos(), (float) placeRange.getIntegerValue(), (int) placeRange.getIntegerValue(),
						false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
		return positions;
	}

	public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
		List<BlockPos> circleblocks = new ArrayList<>();
		int cx = loc.getX();
		int cy = loc.getY();
		int cz = loc.getZ();
		for (int x = cx - (int) r; x <= cx + r; x++) {
			for (int z = cz - (int) r; z <= cz + r; z++) {
				for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
						BlockPos l = new BlockPos(x, y + plus_y, z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}

	public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
		float doubleExplosionSize = 12.0F;
		double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
		Vec3d vec3d = new Vec3d(posX, posY, posZ);
		double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
		double v = (1.0D - distancedsize) * blockDensity;
		float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
		double finald = 1.0D;
		if (entity instanceof EntityLivingBase) {
			finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage),
					new Explosion(Minecraft.getMinecraft().world, null, posX, posY, posZ, 6F, false, true));
		}
		return (float) finald;
	}

	public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) entity;
			DamageSource ds = DamageSource.causeExplosionDamage(explosion);
			damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(),
					(float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

			int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
			float f = MathHelper.clamp(k, 0.0F, 20.0F);
			damage *= 1.0F - f / 25.0F;
			// damage = damage * (1.0F - f / 25.0F);

			if (entity.isPotionActive(Potion.getPotionById(11))) {
				damage = damage - (damage / 4);
			}
			damage = Math.max(damage, 0.0F);
			return damage;
		}
		damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(),
				(float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
		return damage;
	}

	private static float getDamageMultiplied(float damage) {
		int diff = Minecraft.getMinecraft().world.getDifficulty().getId();
		return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
	}

	// Better Rotation Spoofing System:
	private static boolean isSpoofingAngles;
	private static double yaw;
	private static double pitch;

	// this modifies packets being sent so no extra ones are made. NCP used to flag
	// with "too many packets"
	private static void setYawAndPitch(float yaw1, float pitch1) {
		yaw = yaw1;
		pitch = pitch1;
		isSpoofingAngles = true;
	}

	private static void resetRotation() {
		if (isSpoofingAngles) {
			yaw = Minecraft.getMinecraft().player.rotationYaw;
			pitch = Minecraft.getMinecraft().player.rotationPitch;
			isSpoofingAngles = false;
		}
	}

	public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
		double dirx = me.posX - px;
		double diry = me.posY - py;
		double dirz = me.posZ - pz;

		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

		dirx /= len;
		diry /= len;
		dirz /= len;

		double pitch = Math.asin(diry);
		double yaw = Math.atan2(dirz, dirx);

		// to degree
		pitch = pitch * 180.0d / Math.PI;
		yaw = yaw * 180.0d / Math.PI;

		yaw += 90f;

		return new double[] { yaw, pitch };
	}

	@SubscribeEvent
	public void onSendPacket(PacketSendEvent event) {
		Packet packet = event.getPacket();
		if (packet instanceof CPacketPlayer && spoofRotation.getBooleanValue()) {
			if (isSpoofingAngles) {
				((ICPacketPlayer) packet).setYaw((float) yaw);
				((ICPacketPlayer) packet).setPitch((float) pitch);
			}
		}
	}

	@SubscribeEvent
	public void onReadPacket(PacketReceiveEvent event) {
		if (event.getPacket() instanceof SPacketSoundEffect) {
			final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
			if (packet.getCategory() == SoundCategory.BLOCKS
					&& packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
				for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
					if (e instanceof EntityEnderCrystal) {
						if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
							e.setDead();
						}
					}
				}
			}
		}
	}

//    @EventHandler
//    private final Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
//        Packet packet = event.getPacket();
//        if (packet instanceof CPacketPlayer && spoofRotations.getValue()) {
//            if (isSpoofingAngles) {
//            	((ICPacketPlayer)packet).setYaw((float)yaw);
//            	((ICPacketPlayer)packet).setPitch((float)pitch);
//            }
//        }
//    });
//    @EventHandler
//    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
//        if (event.getPacket() instanceof SPacketSoundEffect) {
//            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
//            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
//                for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
//                    if (e instanceof EntityEnderCrystal) {
//                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
//                            e.setDead();
//                        }
//                   }
//               }
//           }
//        }
//    });

	@Override
	public void onEnable() {
		PlacedCrystals.clear();
		isActive = false;
		isPlacing = false;
		isBreaking = false;
		if (chat.getBooleanValue() && mc.player != null) {
			String text = "[" + ChatFormatting.GREEN + "moneymod+2" + ChatFormatting.RESET + "]" + ChatFormatting.GREEN
					+ " NewAutoCrystal enabled!";
			mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(text), 5936);
		}
	}

	@Override
	public void onDisable() {
		render = null;
		renderEnt = null;
		resetRotation();
		PlacedCrystals.clear();
		isActive = false;
		isPlacing = false;
		isBreaking = false;
		if (chat.getBooleanValue()) {
			String text = "[" + ChatFormatting.GREEN + "moneymod+2" + ChatFormatting.RESET + "]" + ChatFormatting.RED
					+ " NewAutoCrystal disabled!";
			mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(text), 5936);
		}
		// TargetHud.INSTANCE.setTarget(null);
	}

}
