#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec2 u_playerPos;
uniform float u_radius;
uniform float u_softEdgeSize;
uniform vec2 u_resolution;

varying vec2 v_texCoords;

void main() {
    // Sample the scene color
    vec4 sceneColor = texture2D(u_texture, v_texCoords);

    // Convert texture coordinates to screen coordinates
    vec2 screenPos = v_texCoords * u_resolution;

    // Calculate distance from player
    float distance = length(screenPos - u_playerPos);

    // Calculate fog factor using smoothstep
    float fogFactor = smoothstep(u_radius, u_radius - u_softEdgeSize, distance);

    // Create a dark fog color for unexplored areas
    vec4 fogColor = vec4(0.05, 0.05, 0.1, 1.0);

    // Mix scene and fog based on the fog factor
    gl_FragColor = mix(fogColor, sceneColor, fogFactor);
}