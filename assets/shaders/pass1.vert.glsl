#version 140

#ifdef POINT
	uniform mat3 mat0;
	uniform mat3 matc;
#endif

in  vec2 in_Position;

void main(void)
{
	#ifdef POINT
		gl_Position = vec4(vec3((matc * mat0 * vec3(in_Position,1.0)).xy,0).xy, 0.0, 1.0);
	#endif
	#ifdef AMBIENT
		gl_Position = vec4(in_Position,0.0,1.0);
	#endif
	#ifdef DIRECTIONAL
		gl_Position = vec4(in_Position,0.0,1.0);
	#endif
}