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

    // Calculate visibility factor using smoothstep
    float visibilityFactor = smoothstep(u_radius + u_softEdgeSize, u_radius - u_softEdgeSize, distance);

    // Darken areas outside the visibility radius (but still show them)
    // The 0.25 means areas far from player are at 25% brightness
    float finalBrightness = 0.25 + (visibilityFactor * 0.75);

    // Apply the darkness effect while preserving color
    gl_FragColor = vec4(sceneColor.rgb * finalBrightness, sceneColor.a);
}