// Vertex Shader – file "minimal.vert"
 
#version 140
 
uniform mat3 mat0;
uniform mat3 matc;

in  vec2 in_Position;
in	vec2 in_Texcoord;

out	vec3 pass_Position;
out vec2 pass_Texcoord;
 
void main(void)
{
        pass_Position = mat0 * vec3(in_Position,1.0);
		pass_Position.z = 0.0;
		vec3 o = vec3((matc * mat0 * vec3(in_Position,1.0)).xy, 0.0);
		gl_Position = vec4(o, 1.0);
		pass_Texcoord = in_Texcoord;
}