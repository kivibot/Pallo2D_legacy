#version 140

uniform sampler2D samp[3];
uniform float matID;
uniform mat3 matt;

in vec3 pass_Position;
in vec2 pass_Texcoord;

out vec3 out_Color;
out vec3 out_Position;
out vec3 out_Normal;
out vec3 out_Tex3;

void main(void) {
	out_Color = texture(samp[0], pass_Texcoord).xyz;
	out_Position = pass_Position;
	out_Normal = normalize(matt * (2.0*texture(samp[1], pass_Texcoord)-1.0).xyz);

        out_Tex3.x = matID;
        out_Tex3.y = texture(samp[2], pass_Texcoord).x;
	
}