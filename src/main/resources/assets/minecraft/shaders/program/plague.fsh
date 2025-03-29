#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform float time; // Client world's age in ticks
uniform float Height; // Player's Y coordinate

float remap(float x, float a, float b) {
    return (x - a) / (b - a);
}

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    vec3 color2 = color;

    // Vignette
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(texCoord, center) * 1.2;  // Adjust intensity
    float vignette = 1.0 - smoothstep(0.7, 1.2, dist);
    color2 *= vignette;


    // desaturate
    float gray = dot(color.rgb, vec3(0.3, 0.59, 0.11)); // Luminance
    color2 = mix(color.rgb, vec3(gray), 0.7);  // Mix color with grayscale


    float pulse = sin(time*0.8) +1;
    pulse = smoothstep(0.50,0.70,pulse)-1;
    pulse *= 0.04;

    // contrast
    color2 = (color2 - 0.5) * 1.1+pulse + 0.5; //1.1 is contrast
    color2 = smoothstep(0.0,0.8,color2);

    // tint
    color2 *= vec3(1.1, 1.1, 0.6);

    color = mix(color,color2, clamp(1-remap(Height,-60,25),0.0,0.7));

    /* - doctor 1
    // abberation
    float shift = 0.003;  // Offset strength (adjust as needed)
    vec3 l = texture(DiffuseSampler, texCoord + vec2(shift, 0.0)).rgb;
    vec3 r = texture(DiffuseSampler, texCoord - vec2(shift, 0.0)).rgb;
    color.r += l.r;
    color.b += r.b;

    if (color.r < .6) {
        color *= vec3(0.9, 1.0, 1.0);
        // desaturate
        float gray = dot(color.rgb, vec3(0.3, 0.59, 0.11)); // Luminance
        color = mix(color.rgb, vec3(gray), 0.4);  // Mix color with grayscale

    }

    // "contrast", darken dark spots
    color = smoothstep(0.05,0.7,color);
    */

    fragColor = vec4(color, 1.0);
}