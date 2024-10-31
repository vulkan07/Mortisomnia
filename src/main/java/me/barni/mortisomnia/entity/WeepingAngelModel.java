package me.barni.mortisomnia.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static me.barni.mortisomnia.entity.WeepingAngelEntity.Pose.Crying;

// Made with Blockbench 4.9.0
// Exported for Minecraft version 1.17+ for Yarn
@Environment(value= EnvType.CLIENT)
public class WeepingAngelModel<T extends WeepingAngelEntity> extends SinglePartEntityModel<T> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart l_wing;
    private final ModelPart r_wing;
    private final ModelPart l_arm;
    private final ModelPart r_arm;

    public WeepingAngelModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("body").getChild("head");
        this.l_wing = root.getChild("body").getChild("l_wing");
        this.r_wing = root.getChild("body").getChild("r_wing");
        this.l_arm = root.getChild("body").getChild("l_arm");
        this.r_arm = root.getChild("body").getChild("r_arm");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-4.0F, -26.0F, -2.0F, 8.0F, 24.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData head = body.addChild("head", ModelPartBuilder.create().uv(24, 14).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(24, 14).cuboid(-3.0F, -9.0F, -4.0F, 6.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -26.0F, 0.0F));

        ModelPartData l_wing = body.addChild("l_wing", ModelPartBuilder.create().uv(42, 0).cuboid(-2.0F, 0.0F, 0.0F, 2.0F, 9.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 42).cuboid(0.0F, -1.0F, 0.0F, 2.0F, 16.0F, 1.0F, new Dilation(0.0F))
                .uv(6, 42).cuboid(2.0F, -1.0F, 0.0F, 2.0F, 18.0F, 1.0F, new Dilation(0.0F))
                .uv(6, 0).cuboid(4.0F, 1.0F, 0.0F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, -25.0F, 2.0F));

        ModelPartData r_wing = body.addChild("r_wing", ModelPartBuilder.create().uv(42, 0).mirrored().cuboid(0.0F, 0.0F, 0.0F, 2.0F, 9.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(18, 42).mirrored().cuboid(-2.0F, -1.0F, 0.0F, 2.0F, 16.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(6, 42).mirrored().cuboid(-4.0F, -1.0F, 0.0F, 2.0F, 18.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(6, 0).mirrored().cuboid(-6.0F, 1.0F, 0.0F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-5.0F, -25.0F, 2.0F));

        ModelPartData l_arm = body.addChild("l_arm", ModelPartBuilder.create().uv(40, 30).cuboid(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -26.0F, 0.0F));

        ModelPartData r_arm = body.addChild("r_arm", ModelPartBuilder.create().uv(24, 30).cuboid(-4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, -26.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        body.render(matrices, vertices, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return body;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        //head.resetTransform();
        l_arm.resetTransform();
        r_arm.resetTransform();
        l_wing.resetTransform();
        r_wing.resetTransform();

        // Move head only if not crying
        if (entity.getAngelPose() != Crying) {
            head.pitch = (float) Math.toRadians(MathHelper.clamp(headYaw, -80.0F, 80.0F));
            head.yaw = (float) Math.toRadians(MathHelper.clamp(headPitch, -25.0F, 80.0F));

        }

        switch (entity.getAngelPose()) {
            case Crying -> {
                head.pitch = (float) Math.toRadians(30);
                l_arm.pitch = (float) Math.toRadians(-103);
                l_arm.yaw = (float) Math.toRadians(30);

                r_arm.pitch = (float) Math.toRadians(-103);
                r_arm.yaw = (float) Math.toRadians(-30);

                l_wing.roll = (float) Math.toRadians(6);
                r_wing.roll = (float) Math.toRadians(-6);
            }
            case Looking -> {
                l_arm.pitch = (float) Math.toRadians(-20);
                l_arm.yaw = (float) Math.toRadians(10);

                r_arm.pitch = (float) Math.toRadians(-20);
                r_arm.yaw = (float) Math.toRadians(-10);
            }
            case Attacking -> {
                l_arm.pitch = (float) Math.toRadians(-90);
                r_arm.pitch = (float) Math.toRadians(-90);

                r_wing.roll = (float) Math.toRadians(120);
                l_wing.roll = (float) Math.toRadians(-120);

                l_wing.pitch = (float) Math.toRadians(25);
                r_wing.pitch = (float) Math.toRadians(25);
            }
        }
    }
}