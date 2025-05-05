#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec2 u_playerPos;
uniform float u_radius;
uniform float u_softEdge;

void main() {
    // Get the color from the screen texture
    vec4 color = texture2D(u_texture, v_texCoords);

    // Calculate distance from current pixel to player position
    vec2 screenPos = v_texCoords * u_resolution;
    float dist = distance(screenPos, u_playerPos);

    // Calculate visibility factor based on distance
    float visibility = 1.0 - smoothstep(u_radius - u_softEdge, u_radius, dist);

    float minDarkness = 0.2;
    float darkFactor = minDarkness + visibility * (1.0 - minDarkness);

    gl_FragColor = vec4(color.rgb * darkFactor, color.a);
}
