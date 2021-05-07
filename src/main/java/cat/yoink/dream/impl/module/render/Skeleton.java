package cat.yoink.dream.impl.module.render;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import cat.yoink.dream.mixin.mixins.accessor.*;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class Skeleton extends Module {

	private final Setting red = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting green = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting blue = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting alpha = new Setting.Builder(SettingType.INTEGER).setName("Alpha").setModule(this)
			.setIntegerValue(255).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	public Skeleton(String name, String description, Category category) {
		super(name, description, category);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(alpha);
	}

	private ICamera camera = new Frustum();
	private static final HashMap<EntityPlayer, float[][]> entities = new HashMap<>();

	private Vec3d getVec3(RenderWorldLastEvent event, EntityPlayer e) {
		float pt = event.getPartialTicks();
		double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * pt;
		double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * pt;
		double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * pt;
		return new Vec3d(x, y, z);
	}

	@SubscribeEvent
	public void onWorldRender(RenderWorldLastEvent event) {
		if (mc.getRenderManager() == null || mc.getRenderManager().options == null)
			return;

		startEnd(true);
		GL11.glEnable(2903);
		GL11.glDisable(2848);
		entities.keySet().removeIf(this::doesntContain);

		mc.world.playerEntities.forEach(e -> drawSkeleton(event, e));

		Gui.drawRect(0, 0, 0, 0, 0);
		startEnd(false);
	}

	private void drawSkeleton(RenderWorldLastEvent event, EntityPlayer e) {
		double d3 = mc.player.lastTickPosX
				+ (mc.player.posX - mc.player.lastTickPosX) * (double) event.getPartialTicks();
		double d4 = mc.player.lastTickPosY
				+ (mc.player.posY - mc.player.lastTickPosY) * (double) event.getPartialTicks();
		double d5 = mc.player.lastTickPosZ
				+ (mc.player.posZ - mc.player.lastTickPosZ) * (double) event.getPartialTicks();

		camera.setPosition(d3, d4, d5);

		float[][] entPos = entities.get(e);
		if (entPos != null && e.isEntityAlive() && camera.isBoundingBoxInFrustum(e.getEntityBoundingBox()) && !e.isDead
				&& e != mc.player && !e.isPlayerSleeping()) {
			GL11.glPushMatrix();
			GL11.glEnable(2848);
			GL11.glLineWidth(1.0F);
			GlStateManager.color(red.getIntegerValue() / 255.0F, green.getIntegerValue() / 255.0F,
					blue.getIntegerValue() / 255.0F, alpha.getIntegerValue() / 255.0F);
			Vec3d vec = getVec3(event, e);
			double x = vec.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
			double y = vec.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
			double z = vec.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
			GL11.glTranslated(x, y, z);
			float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
			GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
			GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? -0.235D : 0.0D);
			float yOff = e.isSneaking() ? 0.6F : 0.75F;
			GL11.glPushMatrix();
			GlStateManager.color(red.getIntegerValue() / 255.0F, green.getIntegerValue() / 255.0F,
					blue.getIntegerValue() / 255.0F, alpha.getIntegerValue() / 255.0F);
			GL11.glTranslated(-0.125D, yOff, 0.0D);
			if (entPos[3][0] != 0.0F)
				GL11.glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
			if (entPos[3][1] != 0.0F)
				GL11.glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
			if (entPos[3][2] != 0.0F)
				GL11.glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
			GL11.glBegin(3);
			GL11.glVertex3d(0.0D, 0.0D, 0.0D);
			GL11.glVertex3d(0.0D, -yOff, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GlStateManager.color(red.getIntegerValue() / 255.0F, green.getIntegerValue() / 255.0F,
					blue.getIntegerValue() / 255.0F, alpha.getIntegerValue() / 255.0F);
			GL11.glTranslated(0.125D, yOff, 0.0D);
			if (entPos[4][0] != 0.0F)
				GL11.glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
			if (entPos[4][1] != 0.0F)
				GL11.glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
			if (entPos[4][2] != 0.0F)
				GL11.glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
			GL11.glBegin(3);
			GL11.glVertex3d(0.0D, 0.0D, 0.0D);
			GL11.glVertex3d(0.0D, -yOff, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? 0.25D : 0.0D);
			GL11.glPushMatrix();
			GlStateManager.color(red.getIntegerValue() / 255.0F, green.getIntegerValue() / 255.0F,
					blue.getIntegerValue() / 255.0F, alpha.getIntegerValue() / 255.0F);
			GL11.glTranslated(0.0D, e.isSneaking() ? -0.05D : 0.0D, e.isSneaking() ? -0.01725D : 0.0D);
			GL11.glPushMatrix();
			GlStateManager.color(red.getIntegerValue() / 255.0F, green.getIntegerValue() / 255.0F,
					blue.getIntegerValue() / 255.0F, alpha.getIntegerValue() / 255.0F);
			GL11.glTranslated(-0.375D, yOff + 0.55D, 0.0D);
			if (entPos[1][0] != 0.0F)
				GL11.glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
			if (entPos[1][1] != 0.0F)
				GL11.glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
			if (entPos[1][2] != 0.0F)
				GL11.glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
			GL11.glBegin(3);
			GL11.glVertex3d(0.0D, 0.0D, 0.0D);
			GL11.glVertex3d(0.0D, -0.5D, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(0.375D, yOff + 0.55D, 0.0D);
			if (entPos[2][0] != 0.0F)
				GL11.glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
			if (entPos[2][1] != 0.0F)
				GL11.glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
			if (entPos[2][2] != 0.0F)
				GL11.glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
			GL11.glBegin(3);
			GL11.glVertex3d(0.0D, 0.0D, 0.0D);
			GL11.glVertex3d(0.0D, -0.5D, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
			GL11.glPushMatrix();
			GlStateManager.color(red.getIntegerValue() / 255.0F, green.getIntegerValue() / 255.0F,
					blue.getIntegerValue() / 255.0F, alpha.getIntegerValue() / 255.0F);
			GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
			if (entPos[0][0] != 0.0F)
				GL11.glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
			GL11.glBegin(3);
			GL11.glVertex3d(0.0D, 0.0D, 0.0D);
			GL11.glVertex3d(0.0D, 0.3D, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslated(0.0D, e.isSneaking() ? -0.16175D : 0.0D, e.isSneaking() ? -0.48025D : 0.0D);
			GL11.glPushMatrix();
			GL11.glTranslated(0.0D, yOff, 0.0D);
			GL11.glBegin(3);
			GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
			GL11.glVertex3d(0.125D, 0.0D, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GlStateManager.color(red.getIntegerValue() / 255.0F, green.getIntegerValue() / 255.0F,
					blue.getIntegerValue() / 255.0F, alpha.getIntegerValue() / 255.0F);
			GL11.glTranslated(0.0D, yOff, 0.0D);
			GL11.glBegin(3);
			GL11.glVertex3d(0.0D, 0.0D, 0.0D);
			GL11.glVertex3d(0.0D, 0.55D, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
			GL11.glBegin(3);
			GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
			GL11.glVertex3d(0.375D, 0.0D, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

	private void startEnd(boolean revert) {
		if (revert) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GL11.glEnable(2848);
			GlStateManager.disableDepth();
			GlStateManager.disableTexture2D();
			GL11.glHint(3154, 4354);
		} else {
			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
			GL11.glDisable(2848);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		}
		GlStateManager.depthMask(!revert);
	}

	public static void addEntity(EntityPlayer e, ModelPlayer model) {
		entities.put(e, new float[][] {
				{ model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ },
				{ model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY,
						model.bipedRightArm.rotateAngleZ },
				{ model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ },
				{ model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY,
						model.bipedRightLeg.rotateAngleZ },
				{ model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY,
						model.bipedLeftLeg.rotateAngleZ } });
	}

	private boolean doesntContain(EntityPlayer var0) {
		return !mc.world.playerEntities.contains(var0);
	}
}
