// Vertex Shader – file "minimal.vert"
 
#version 140


uniform mat3 mat0;
uniform mat3 matc;

in  vec2 in_Position;

void main(void)
{
		gl_Position = vec4((matc * mat0 * vec3(in_Position,1.0)).xy, 0.0, 1.0);
}