#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTextCoord;

uniform mat4 projModelMatrix;

void main() {
  gl_Position = projModelMatrix * vec4(position, 1.0);
  outTextCoord = textCoord;
}
