#version 140

const int matsize = 3+1;
const int matcount = 20;

const int lightsize = 3+3+3;

uniform sampler2D samp[4];

uniform vec2 screen;
uniform float mat[matcount*matsize];      
uniform float li[lightsize];

#ifdef POINT
	uniform float attenuationSquare;
	uniform float attenuationLinear;
	uniform float attenuationConst;
#endif

#ifdef ARRAY
	in vec3 pass_Color;
	in vec3 pass_lpos;
#endif

out vec3 out_Color;
out vec3 out_Spec;

void main(void) {
	vec2 pass_Texcoord = gl_FragCoord.xy/vec2(800,600);

	vec3 dif = texture(samp[0], pass_Texcoord).xyz;
	
	vec3 mp = texture(samp[1], pass_Texcoord).xyz;
	
	vec3 mn = texture(samp[2], pass_Texcoord).xyz;
	vec3 cp = vec3(0.0,0.0,70.0); //Cam Pos

        vec3 tex3 = texture(samp[3], pass_Texcoord).xyz;
	
	int mid = int(tex3.x);

	int moff = mid*matsize;
	
	vec3 mat_0 = vec3(mat[moff],mat[moff+1],mat[moff+2]);
	float mat_shi = mat[moff+3];
	vec4 o;
	
	const int off = 0;
	
	#ifdef ARRAY
		vec3 lcol = pass_Color;
		vec3 lpos = pass_lpos;
		vec3 lspe = lcol;
	#else
		vec3 lcol = vec3(li[off+0],li[off+1],li[off+2]);
	#endif

	#ifdef POINT
		vec3 lpos = vec3(li[off+3],li[off+4],li[off+5]);
		vec3 lspe = vec3(li[off+6],li[off+7],li[off+8]);
	#endif
	#ifdef DIRECTIONAL
		vec3 ldir = vec3(li[off+3],li[off+4],li[off+5]);
		vec3 lspe = vec3(li[off+6],li[off+7],li[off+8]);
	#endif
	
	#ifdef POINT
		vec3 surface_to_light = lpos-mp;
	
		float d = length(surface_to_light);
	
		float a = clamp(1.0 / ((d*attenuationLinear + pow(d,2)*attenuationSquare)+attenuationConst),0.0,1.0);
	
		surface_to_light /= d;
                
                vec3 rv = -reflect(surface_to_light,mn);

                vec3 specular = vec3(0,0,0);

                //might be very costly
                if(dot(surface_to_light,mn) >= 0.0){
                    specular = lspe * mat_0 * pow(max(0.0, dot(rv,normalize(cp-mp))),mat_shi) * tex3.y;
                }


                out_Color = a * (max(0,dot(mn,surface_to_light))* lcol * dif + specular);
                //out_Color = vec3(dot(rv,normalize(cp-mp)),0,0);        

        #endif
	#ifdef DIRECTIONAL
		ldir = normalize(-ldir);
		vec3 hv = normalize(ldir+cp-mp);
	
		out_Color = max(0, dot(mn, ldir)) * lcol * dif + lspe * mat_0 * pow(max(0.0, dot(hv,mn)), mat_shi) * 1.0;
	#endif
	#ifdef AMBIENT
		out_Color = lcol * dif;
	#endif
	#ifdef ARRAY
		vec3 surface_to_light = lpos-mp;
	
		float d = length(surface_to_light);
	
		float a = clamp(1.0 / ((d*0 + pow(d,2)*0.0001)),0.0,1.0);
	
		surface_to_light /= d;
		
		vec3 rv = -reflect(surface_to_light,mn);

                vec3 specular = vec3(0,0,0);

                //might be very costly
                if(dot(surface_to_light,mn) >= 0.0){
                    specular = lspe * mat_0 * pow(max(0.0, dot(rv,normalize(cp-mp))),mat_shi) * tex3.y;
                }


                out_Color = a * (max(0,dot(mn,surface_to_light))* lcol * dif + specular);
                //out_Color = lpos;
	#endif
}