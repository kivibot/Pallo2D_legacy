#version 140

uniform sampler2D samp[3];

in	vec2 pass_Texcoord;

out vec4 out_Color;

void main(void) {
	//vec4 diff = texture(samp[0],pass_Texcoord);
	vec4 light = texture(samp[1],pass_Texcoord);
	//vec4 spec = texture(samp[2],pass_Texcoord);
	out_Color = light;//diff*light+spec;
}