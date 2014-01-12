// Vertex Shader – file "minimal.vert"
 
#version 140
 
in  vec2 in_Position;
in	vec2 in_Texcoord;

out	vec3 pass_Position;
out vec2 pass_Texcoord;
 
void main(void)
{
        pass_Position = vec3(in_Position, 0.0);
		gl_Position = vec4(pass_Position,1);
		pass_Texcoord = in_Texcoord;
}