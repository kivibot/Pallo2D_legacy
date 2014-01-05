#version 140

/*
[0]	specular color
[1] kiilto, , ,
*/

const int matsize = 3+1;
const int matcount = 100;
const int lightcount = 20;
// col 		pos 	spe
// 0,1,2	3,4,5	6,7,8
const int lightsize = 3+3+3;

uniform sampler2D samp[3];

uniform float uuh;
uniform float mat[matcount*matsize];      
uniform float li[lightcount*lightsize];

in	vec2 pass_Texcoord;

out vec4 out_Color;
out vec4 out_Spec;

void main(void) {
	vec4 dif = texture(samp[0], pass_Texcoord);
	vec4 mp = texture(samp[1], pass_Texcoord);
	vec4 mn = normalize(texture(samp[2], pass_Texcoord));
	
	int mid = int(mp.w);

	int moff = mid*matsize;
	
	vec4 mat_0 = vec4(1,1,1,1);//vec4(mat[moff],mat[moff+1],mat[moff+2],1.0);
	float mat_shi = mat[moff+3];
	vec4 o;
	for(int i=0; i<lightcount; i++){
		int off = i*lightsize;
	
		vec3 lcol = vec3(li[off+0],li[off+1],li[off+2]);
		vec3 lpos = vec3(li[off+3],li[off+4],li[off+5]);
		vec3 lspe = vec3(li[off+6],li[off+7],li[off+8]);
		
		vec4 ltm = vec4(lpos-mp.xyz,1.0);
	
		float d = length(ltm);
	
		float a = 1.0 / d;
	
		ltm /= d;
		
		//out_Color = vec4(lspe,1);
	
		out_Color += a * (max(0,dot(mn,ltm))*vec4(lcol,1.0) * dif + vec4(lspe,1.0) * mat_0 * pow(max(0.0, dot(reflect(ltm, mn), vec4(0.0,0.0,-1.0,0.0))), mat_shi) * dif.w);
	}
}