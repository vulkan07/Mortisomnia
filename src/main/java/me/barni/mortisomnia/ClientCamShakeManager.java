package me.barni.mortisomnia;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;

@Environment(EnvType.CLIENT)
public class ClientCamShakeManager {
    private static ClientCamShakeManager INSTANCE;
    public static ClientCamShakeManager getInstance() {
        if (INSTANCE == null) INSTANCE = new ClientCamShakeManager();
        return INSTANCE;
    }
    private ClientCamShakeManager() {}

    private float shakeAmplitude;
    private float shakeAttenuation;
    private float shakeDamping;
    private int time;
    private Vec3d shakeVector, shakeVelocity = Vec3d.ZERO;
    private final float AMPLITUDE_FACTOR = .1f;

    public void update() {
        time++;
        shakeAmplitude *= shakeAttenuation;
        if (shakeAmplitude < .0005f) {
            shakeVector = Vec3d.ZERO;
            return;
        }
        if (time%2==0)
            shakeVelocity = new Vec3d(
                    (RANDOM.nextGaussian()-.5)*shakeAmplitude*AMPLITUDE_FACTOR,
                    (RANDOM.nextGaussian()-.5)*shakeAmplitude*AMPLITUDE_FACTOR,
                    (RANDOM.nextGaussian()-.5)*shakeAmplitude*AMPLITUDE_FACTOR
            );
        shakeVector = shakeVector.add(shakeVelocity);
        shakeVector = shakeVector.multiply(shakeDamping);
    }


    public void registerShake(float amplitude) {
        shakeAmplitude = amplitude;
        shakeAttenuation = .8f;
        shakeDamping = .8f;
    }
    public void registerShake(float amplitude, float attenuation, float damping) {
        shakeAmplitude = amplitude;
        shakeAttenuation = 1-attenuation;
        shakeDamping = 1-damping;
    }

    public Vec3d getShake() {
        return shakeVector;
    }
}
