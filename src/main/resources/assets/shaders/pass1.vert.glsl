#version 140

#ifdef POINT
	uniform mat3 mat0;
	uniform mat3 matc;
#endif

#ifdef ARRAY
	uniform mat3 mat0;
	uniform mat3 matc;
#endif

in  vec2 in_Position;

#ifdef ARRAY
	in vec3 in_Color;
	in vec3 in_lpos;
	out vec3 pass_Color;
	out vec3 pass_lpos;
#endif

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
	#ifdef ARRAY
		gl_Position = vec4(vec3((matc * mat0 * vec3(in_Position.xy,1.0)).xy,0).xy, 0, 1.0);
		pass_Color = in_Color;
		pass_lpos = mat0 * in_lpos;
	#endif
}