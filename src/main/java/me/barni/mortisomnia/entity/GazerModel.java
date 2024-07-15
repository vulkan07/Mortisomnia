package me.barni.mortisomnia.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 4.9.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
@Environment(value= EnvType.CLIENT)
public class GazerModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelPart root;
	private final ModelPart head_bone;

	public GazerModel(ModelPart root) {
		this.root = root.getChild("root");
        this.head_bone = root.getChild("root").getChild("torso_bone").getChild("head_bone");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData l_leg_bone = root.addChild("l_leg_bone", ModelPartBuilder.create().uv(16, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -12.0F, 0.0F));

		ModelPartData r_leg_bone = root.addChild("r_leg_bone", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -12.0F, 0.0F));

		ModelPartData torso_bone = root.addChild("torso_bone", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.0F, 0.0F));

		ModelPartData head_bone = torso_bone.addChild("head_bone", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.0F, 0.0F));

		ModelPartData l_arm_bone = torso_bone.addChild("l_arm_bone", ModelPartBuilder.create().uv(24, 16).cuboid(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -12.0F, 0.0F));

		ModelPartData r_arm_bone = torso_bone.addChild("r_arm_bone", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, -12.0F, 0.0F));

		ModelPartData r_arm_r1 = r_arm_bone.addChild("r_arm_r1", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		root.render(matrices, vertices, light, overlay, color);
	}

    @Override
    public void setAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -80.0F, 80.0F);
        headPitch = MathHelper.clamp(headPitch, -60.0F, 80.0F);

        this.head_bone.yaw = headYaw * 0.017453292F; //DEG TO RAD
        this.head_bone.pitch = headPitch * 0.017453292F;
    }
}