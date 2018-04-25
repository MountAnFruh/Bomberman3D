#version 330

in vec2 outTextCoord;
in vec3 mvPos;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec4 color;
uniform int hasTexture;

void main() {
  if ( hasTexture == 1 ) {
    fragColor = color * texture(texture_sampler, outTextCoord);
  } else {
    fragColor = color;
  }
}