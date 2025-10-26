#version 120
uniform vec3 tintColor;

void main() {
    vec3 color = vec3(1.0, 1.0, 1.0);
    gl_FragColor = vec4(color * tintColor, 1.0);
}
