#version 140

/*
[0]	specular color
[1] kiilto, , ,
*/

const int matsize = 3+1;
const int matcount = 10;
const int lightcount = 1;
// col 		pos 	spe
// 0,1,2	3,4,5	6,7,8
const int lightsize = 3+3+3;

uniform sampler2D samp[3];

uniform vec2 screen;
uniform float mat[matcount*matsize];      
uniform float li[lightcount*lightsize];

out vec3 out_Color;
out vec3 out_Spec;

void main(void) {
	vec2 pass_Texcoord = gl_FragCoord.xy/vec2(800,600);

	vec3 dif = texture(samp[0], pass_Texcoord).xyz;
	vec3 mp = texture(samp[1], pass_Texcoord).xyz;
	vec3 mn = normalize(texture(samp[2], pass_Texcoord)).xyz;
	vec3 cp = vec3(0,0,0); //Cam Pos
	
	int mid = 0;//int(mp.w);

	int moff = mid*matsize;
	
	vec3 mat_0 = vec3(mat[moff],mat[moff+1],mat[moff+2]);
	float mat_shi = mat[moff+3];
	vec4 o;
	for(int i=0; i<lightcount; i++){
		int off = i*lightsize;
	
		vec3 lcol = vec3(li[off+0],li[off+1],li[off+2]);
		vec3 lpos = vec3(li[off+3],li[off+4],li[off+5]);
		vec3 lspe = vec3(li[off+6],li[off+7],li[off+8]);
		
		vec3 ltm = lpos-mp;
	
		float d = length(ltm);
	
		float a = 1.0 / ((d*0.1+d*d*2));
	
		ltm /= d;
		
		vec3 hv = normalize(ltm+cp-mp);
		
		out_Color = a * (max(0,dot(mn,ltm))* lcol * dif + lspe * mat_0 * pow(max(0.0, dot(hv,mn)), mat_shi) * 1.0);//dif.w);
		//out_Color += a * (lspe * mat_0 * pow(max(0.0, dot(hv,mn)), mat_shi) * 1.0);
	}
}