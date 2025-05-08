#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_playerPosition;

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

const float outerRadius = 0.7, innerRadius = 0.2, intensity = 0.9;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords) * v_color;

    // Calculate the player position
    vec2 playerScreenPos = u_playerPosition / u_resolution;

    // Calculate relative position from current fragment to the player
    vec2 relativePosition = gl_FragCoord.xy / u_resolution - playerScreenPos;
    relativePosition.x *= u_resolution.x / u_resolution.y; // Account for aspect ratio

    float len = length(relativePosition);
    float vignette = smoothstep(outerRadius, innerRadius, len);
    color.rgb = mix(color.rgb, color.rgb * vignette, intensity);

    gl_FragColor = color;
}
