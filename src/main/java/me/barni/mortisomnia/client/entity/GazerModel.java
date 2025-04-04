package me.barni.mortisomnia.client.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

// Made with Blockbench 4.9.2
// Exported for Minecraft version 1.17+ for Yarn
@Environment(value= EnvType.CLIENT)
public class GazerModel<T extends GazerRenderState> extends EntityModel<GazerRenderState> {
	private final ModelPart root;
	private final ModelPart head_bone;

	public GazerModel(ModelPart root) {
		super(root);
		this.root = root.getChild("root");
        this.head_bone = root.getChild("root").getChild("torso_bone").getChild("head_bone");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData l_leg_bone = root.addChild("l_leg_bone", ModelPartBuilder.create().uv(16, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(2.0F, -12.0F, 0.0F));

		ModelPartData r_leg_bone = root.addChild("r_leg_bone", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(-2.0F, -12.0F, 0.0F));

		ModelPartData torso_bone = root.addChild("torso_bone", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -12.0F, 0.0F));

		ModelPartData head_bone = torso_bone.addChild("head_bone", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -12.0F, 0.0F));

		ModelPartData l_arm_bone = torso_bone.addChild("l_arm_bone", ModelPartBuilder.create().uv(24, 16).cuboid(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(4.0F, -12.0F, 0.0F));

		ModelPartData r_arm_bone = torso_bone.addChild("r_arm_bone", ModelPartBuilder.create(), ModelTransform.origin(-4.0F, -12.0F, 0.0F));

		ModelPartData r_arm_r1 = r_arm_bone.addChild("r_arm_r1", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}


    @Override
    public void setAngles(GazerRenderState state) {
		//TODO
//        headYaw = MathHelper.clamp(headYaw, -80.0F, 80.0F);
//        headPitch = MathHelper.clamp(headPitch, -60.0F, 80.0F);

//        this.head_bone.yaw = headYaw * 0.017453292F; //DEG TO RAD
//        this.head_bone.pitch = headPitch * 0.017453292F;
    }
}