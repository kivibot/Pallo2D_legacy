// Vertex Shader – file "minimal.vert"
 
#version 140
 
in  vec2 in_Position;
in	vec2 in_Texcoord;

out vec2 pass_Texcoord;
 
void main(void)
{
		gl_Position = vec4(in_Position, -1.0, 1.0);
		pass_Texcoord = in_Texcoord;
}