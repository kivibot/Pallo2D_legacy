// Vertex Shader – file "minimal.vert"
 
#version 140
 
in  vec2 in_Position;
in	vec2 in_Texcoord;

out	vec4 pass_Position;
out vec2 pass_Texcoord;
 
void main(void)
{
        pass_Position = vec4(in_Position, -1.0, 1.0);
		gl_Position = pass_Position;
		pass_Texcoord = in_Texcoord;
}