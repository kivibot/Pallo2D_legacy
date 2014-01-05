#version 140

uniform sampler2D samp[3];
uniform float matID;


in	vec4 pass_Position;
in	vec2 pass_Texcoord;

out vec4 out_Color;
out vec4 out_Position;
out vec4 out_Normal;

void main(void) {
	out_Color = texture(samp[0], pass_Texcoord);
	out_Color.w = texture(samp[2], pass_Texcoord).x;
	out_Position = pass_Position;
	out_Position.w = matID;
	out_Normal = 2.0*texture(samp[1], pass_Texcoord)-1.0;
}