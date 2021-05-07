package cat.yoink.dream.impl.event;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class EventPostRenderLayers extends Event {
	// $FF: synthetic field
	public float limbSwingAmount;
	// $FF: synthetic field
	public ModelBase modelBase;
	// $FF: synthetic field
	public RenderLivingBase renderer;
	// $FF: synthetic field
	public float headPitch;
	// $FF: synthetic field
	public float netHeadYaw;
	// $FF: synthetic field
	public float limbSwing;
	// $FF: synthetic field
	public float partialTicks;
	// $FF: synthetic field
	public EntityLivingBase entity;
	// $FF: synthetic field
	public float scaleIn;
	// $FF: synthetic field
	public float ageInTicks;

	public RenderLivingBase getRenderer() {
		return this.renderer;
	}

	public float getLimbSwingAmount() {
		return this.limbSwingAmount;
	}

	public float getHeadPitch() {
		return this.headPitch;
	}

	public float getNetHeadYaw() {
		return this.netHeadYaw;
	}

	public float getPartialTicks() {
		return this.partialTicks;
	}

	public ModelBase getModelBase() {
		return this.modelBase;
	}

	public float getAgeInTicks() {
		return this.ageInTicks;
	}

	public float getScaleIn() {
		return this.scaleIn;
	}

	public float getLimbSwing() {
		return this.limbSwing;
	}

	public EventPostRenderLayers(RenderLivingBase var1, ModelBase var2, EntityLivingBase var3, float var4, float var5,
			float var6, float var7, float var8, float var9, float var10) {
		this.renderer = var1;
		this.modelBase = var2;
		this.entity = var3;
		this.limbSwing = var4;
		this.limbSwingAmount = var5;
		this.partialTicks = var6;
		this.ageInTicks = var7;
		this.netHeadYaw = var8;
		this.headPitch = var9;
		this.scaleIn = var10;
	}

	public EntityLivingBase getEntitylivingbaseIn() {
		return this.entity;
	}
}
