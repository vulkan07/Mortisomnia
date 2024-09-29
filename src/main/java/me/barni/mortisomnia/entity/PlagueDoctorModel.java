package me.barni.mortisomnia.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PlagueDoctorModel<T extends LivingEntity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    /*
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    private final ModelPart left_hand;
    private final ModelPart right_hand;
     */

    public PlagueDoctorModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = root.getChild("root").getChild("head");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(28, 30).cuboid(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 27).cuboid(-4.0F, -24.0F, -3.0F, 8.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData left_leg = root.addChild("left_leg", ModelPartBuilder.create().uv(52, 26).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -12.0F, 0.0F));

        ModelPartData right_leg = root.addChild("right_leg", ModelPartBuilder.create().uv(0, 52).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -12.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create().uv(0, 13).cuboid(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new Dilation(0.0F))
                .uv(52, 42).cuboid(-3.0F, -6.0F, -5.0F, 6.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-6.0F, -7.0F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F))
                .uv(24, 19).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 3.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -24.0F, 0.0F));

        ModelPartData beak_end_r1 = head.addChild("beak_end_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.3388F, -11.5878F, 0.48F, 0.0F, 0.0F));

        ModelPartData beak_r1 = head.addChild("beak_r1", ModelPartBuilder.create().uv(48, 16).cuboid(-1.0F, -3.0F, -7.0F, 2.0F, 3.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, -3.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData left_hand = root.addChild("left_hand", ModelPartBuilder.create().uv(48, 0).cuboid(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(16, 46).cuboid(0.0F, -1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -24.0F, 0.0F));

        ModelPartData right_hand = root.addChild("right_hand", ModelPartBuilder.create().uv(38, 46).cuboid(-4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(-5.0F, -1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, -24.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        //headYaw = MathHelper.clamp(headYaw, -80.0F, 80.0F);
        headPitch = MathHelper.clamp(headPitch, -60.0F, 80.0F);

        this.head.yaw = headYaw * 0.017453292F; //DEG TO RAD
        this.head.pitch = headPitch * 0.017453292F;
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);

    }
}