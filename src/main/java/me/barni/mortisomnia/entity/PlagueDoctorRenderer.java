package me.barni.mortisomnia.entity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.datagen.MortisomniaEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PlagueDoctorRenderer extends LivingEntityRenderer<PlagueDoctorEntity, PlagueDoctorModel<PlagueDoctorEntity>> {
    public PlagueDoctorRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlagueDoctorModel<>(ctx.getPart(MortisomniaEntities.PLAGUE_DOCTOR_LAYER)),.5f);
        this.addFeature(new PlagueDoctorEyesFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(PlagueDoctorEntity entity) {
        return Identifier.of(Mortisomnia.MOD_ID, "textures/entity/plague_doctor.png");
    }

    @Override
    protected boolean hasLabel(PlagueDoctorEntity livingEntity) {
        return false;
    }
}