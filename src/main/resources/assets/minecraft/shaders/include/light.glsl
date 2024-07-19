#version 150

#define MINECRAFT_LIGHT_POWER   (0.6)
#define MINECRAFT_AMBIENT_LIGHT (0.4)
#define MORTISOMNIA_CONTRAST (1.5)

vec4 minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, vec3 normal, vec4 color) {
    float light0 = max(0.0, dot(lightDir0, normal));
    float light1 = max(0.0, dot(lightDir1, normal));
    float lightAccum = min(1.0, (light0 + light1) * MINECRAFT_LIGHT_POWER + MINECRAFT_AMBIENT_LIGHT);
    vec4 o = vec4(color.rgb * lightAccum, color.a);
//    o.gb -= .3;
//    o.rgb = ((o.rgb-.5)*MORTISOMNIA_CONTRAST)+.5;
    return o;
}
vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {
    vec4 color = texture(lightMap, clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));
//    color.gb -= .3;
//    color.rgb = ((color.rgb-.5)*MORTISOMNIA_CONTRAST)+.5;
    return color;
}
