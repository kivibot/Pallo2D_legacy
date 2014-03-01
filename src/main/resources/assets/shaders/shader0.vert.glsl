// Vertex Shader – file "minimal.vert"
 
#version 140
 
in  vec2 in_Position;
in	vec2 in_Texcoord;

//out vec4 gl_Position;
out vec2 out_Texcoord;
 
void main(void)
{
        gl_Position = vec4(in_Position, -1.0, 1.0);
		
		out_Texcoord = in_Texcoord;
}