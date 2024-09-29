package me.barni.mortisomnia.paractivity.activities;

import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.datagen.MortisomniaSounds;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;


public class SpookSoundParactivity extends Paractivity {

    public static final int MIN_DISTANCE = 7;
    public static final int MAX_DISTANCE = 15;

    public SpookSoundParactivity(PlayerEntity player) {
        super(player);
        setMeta(2, ParaController.toControllerTime(0,30), 5, 50);
    }

    @Override
    protected ParaResult customInit() {
        if (!Utils.isCompleteNight(world))
            return ParaResult.fail("not night");
        if (!Utils.isPlayerAlone(player, 48, true))
            return ParaResult.fail("player not alone");

        return ParaResult.success();
    }

    @Override
    public ParaResult tick() {
        BlockPos pos = player.getBlockPos().down(2);
        float radius = 13; // 10 blocks away?

        double azimuthalAngle = 2 * Math.PI * RANDOM.nextDouble(); // Azimuthal angle (0 to 2*pi)
        double polarAngle = Math.PI * RANDOM.nextDouble(); // Polar angle (0 to pi)
        // Convert spherical coordinates to Cartesian coordinates
        double x = radius * Math.sin(polarAngle) * Math.cos(azimuthalAngle);
        double y = radius * Math.sin(polarAngle) * Math.sin(azimuthalAngle);
        double z = radius * Math.cos(polarAngle);

        pos = pos.add((int) x, (int) y, (int) z);

        player.getWorld().playSound(null, pos, MortisomniaSounds.SPOOK_GENERAL, SoundCategory.AMBIENT, .25f, RANDOM.nextFloat(.8f,1));

        return end();
    }

    @Override
    public String getName() {
        return Paractivity.SPOOK_SOUND;
    }
}
