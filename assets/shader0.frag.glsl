#version 140

uniform sampler2D texture_diffuse;

in	vec2 out_Texcoord;

out vec4 out_Color;

void main(void) {
	out_Color = texture2D(texture_diffuse, out_Texcoord);
}