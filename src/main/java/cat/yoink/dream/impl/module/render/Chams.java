package cat.yoink.dream.impl.module.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.impl.event.EventModelPlayerRender;
import cat.yoink.dream.impl.event.EventModelRender;
import cat.yoink.dream.impl.event.EventPostRenderLayers;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Chams extends Module {
	private final Setting mode = new Setting.Builder(SettingType.ENUM).setName("Mode").setModule(this)
			.setEnumValue("ESP").setEnumValues(new ArrayList<>(Arrays.asList("ESP", "Walls"))).build();

	private final Setting Vr = new Setting.Builder(SettingType.INTEGER).setName("Red").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting Vg = new Setting.Builder(SettingType.INTEGER).setName("Green").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting Vb = new Setting.Builder(SettingType.INTEGER).setName("Blue").setModule(this)
			.setIntegerValue(0).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting alpha = new Setting.Builder(SettingType.INTEGER).setName("Alpha").setModule(this)
			.setIntegerValue(130).setMinIntegerValue(0).setMaxIntegerValue(255).build();

	private final Setting lines = new Setting.Builder(SettingType.BOOLEAN).setName("Lines").setModule(this)
			.setBooleanValue(false).build();

	private final Setting width = new Setting.Builder(SettingType.INTEGER).setName("Width (Lines)").setModule(this)
			.setIntegerValue(10).setMinIntegerValue(0).setMaxIntegerValue(100).build();

	public Chams(String name, String description, Category category) {
		super(name, description, category);
		addSetting(mode);
		addSetting(Vr);
		addSetting(Vg);
		addSetting(Vb);
		addSetting(alpha);
		addSetting(lines);
		addSetting(width);

		// TODO Auto-generated constructor stub
	}

	@SubscribeEvent
	public void renderPre(EventModelRender event) {
		if (mode.getEnumValue().equalsIgnoreCase("Walls")) {
			if (!(event.entity instanceof EntityOtherPlayerMP)) {
				return;
			}

			GlStateManager.pushMatrix();
			GL11.glDisable(2929);
			GL11.glColor4f(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);
			GL11.glDisable(3553);
			event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks,
					event.netHeadYaw, event.headPitch, event.scaleFactor);
			GL11.glEnable(2929);
			GL11.glColor4f(this.Vr.getIntegerValue() / 255.0f, this.Vg.getIntegerValue() / 255.0f,
					this.Vb.getIntegerValue() / 255.0f, 1.0f);
			event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks,
					event.netHeadYaw, event.headPitch, event.scaleFactor);
			GL11.glEnable(3553);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GlStateManager.popMatrix();
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void renderPost(EventPostRenderLayers event) {
		if (mode.getEnumValue().equalsIgnoreCase("Walls")) {
			Color c = new Color(Vr.getIntegerValue(), Vg.getIntegerValue(), Vb.getIntegerValue());
			if (event.entity instanceof EntityOtherPlayerMP) {
				GL11.glPushMatrix();
				GL11.glEnable(32823);
				GL11.glPolygonOffset(1.0f, -100000.0f);
				GL11.glPushAttrib(1048575);
				if (!this.lines.getBooleanValue()) {
					GL11.glPolygonMode(1028, 6914);
				} else {
					GL11.glPolygonMode(1028, 6913);
				}
				GL11.glDisable(3553);
				GL11.glDisable(2896);
				GL11.glDisable(2929);
				GL11.glEnable(2848);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f,
						alpha.getIntegerValue() / 255.0f / 2.0f);
				if (this.lines.getBooleanValue()) {
					GL11.glLineWidth((float) this.width.getIntegerValue() / 10.f);
				}
				event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks,
						event.netHeadYaw, event.headPitch, event.scaleIn);
				GL11.glPopAttrib();
				GL11.glPolygonOffset(1.0f, 100000.0f);
				GL11.glDisable(32823);
				GL11.glPopMatrix();
			}
			// if(!event.renderer.bindTexture(event.entity.resource);)
		}
	}

	@SubscribeEvent
	public void onPlayerModel(EventModelPlayerRender event) {
		Color c = new Color(Vr.getIntegerValue(), Vg.getIntegerValue(), Vb.getIntegerValue());

		if (event.type == 0) {
			GL11.glPushMatrix();
			GL11.glEnable(32823);
			GL11.glPolygonOffset(1.0f, -1.0E7f);
			GL11.glPushAttrib(1048575);
			if (!this.lines.getBooleanValue()) {
				GL11.glPolygonMode(1028, 6914);
			} else {
				GL11.glPolygonMode(1028, 6913);
			}
			GL11.glDisable(3553);
			GL11.glDisable(2896);
			GL11.glDisable(2929);
			GL11.glEnable(2848);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f,
					alpha.getIntegerValue() / 255.0f / 2.0f);
			if (this.lines.getBooleanValue()) {
				GL11.glLineWidth((float) this.width.getIntegerValue() / 10.f);
			}
		} else if (event.type == 1) {
			GL11.glPopAttrib();
			GL11.glPolygonOffset(1.0f, 1.0E7f);
			GL11.glDisable(32823);
			GL11.glPopMatrix();
		}
	}

}
