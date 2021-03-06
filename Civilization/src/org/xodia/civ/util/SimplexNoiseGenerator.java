package org.xodia.civ.util;

public class SimplexNoiseGenerator {

	private static int grad3[][] = {
		{1,1,0},
		{-1,1,0},
		{1,-1,0},
		{-1,-1,0},
		{1,0,1},
		{-1,0,1},
		{1,0,-1},
		{-1,0,-1},
		{0,1,1},
		{0,-1,1},
		{0,1,-1},
		{0,-1,-1}
	};
	
	private static int p[] = {151,160,137,91,90,15,
		 131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
		 190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
		 88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
		 77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
		 102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
		 135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
		 5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
		 223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
		 129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
		 251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
		 49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
		 138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180};
	
	private static int simplex[][] = {
		 {0,1,2,3},{0,1,3,2},{0,0,0,0},{0,2,3,1},{0,0,0,0},{0,0,0,0},{0,0,0,0},{1,2,3,0},
		 {0,2,1,3},{0,0,0,0},{0,3,1,2},{0,3,2,1},{0,0,0,0},{0,0,0,0},{0,0,0,0},{1,3,2,0},
		 {0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},
		 {1,2,0,3},{0,0,0,0},{1,3,0,2},{0,0,0,0},{0,0,0,0},{0,0,0,0},{2,3,0,1},{2,3,1,0},
		 {1,0,2,3},{1,0,3,2},{0,0,0,0},{0,0,0,0},{0,0,0,0},{2,0,3,1},{0,0,0,0},{2,1,3,0},
		 {0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},
		 {2,0,1,3},{0,0,0,0},{0,0,0,0},{0,0,0,0},{3,0,1,2},{3,0,2,1},{0,0,0,0},{3,1,2,0},
		 {2,1,0,3},{0,0,0,0},{0,0,0,0},{0,0,0,0},{3,1,0,2},{0,0,0,0},{3,2,0,1},{3,2,1,0}};
	 
	private static int perm[] = new int[512];
	
	static{
		for(int i = 0; i < 512; i++){
			perm[i] = p[i & 255];
		}
	}
	
	private SimplexNoiseGenerator(){}
	
	private static int fastFloor(float x){
		return x > 0 ? (int) x : (int) x - 1;
	}
	
	private static float dot(int g[], float x, float y){
		return g[0] * x + g[1] * y;
	}
	
	public static float noise(float x, float y){
		float n0, n1, n2;
		
		final float F2 = (float) (0.5f * ((float) Math.sqrt(3.0) - 1.0f));
		float s = (x + y) * F2;
		int i = fastFloor(x + s);
		int j = fastFloor(y + s);
		
		final float G2 = (3.0f - (float) Math.sqrt(3.0)) / 6.0f;
		float t = (i + j) * G2;
		float X0 = i - t;
		float Y0 = j - t;
		float x0 = x - X0;
		float y0 = y - Y0;
		
		int i1, j1;
		if(x0 > y0){
			i1 = 1;
			j1 = 0;
		}else{
			i1 = 0;
			j1 = 1;
		}
		
		float x1 = x0 - i1 + G2;
		float y1 = y0 - j1 + G2;
		float x2 = x0 - 1.0f + 2.0f * G2;
		float y2 = y0 - 1.0f + 2.0f * G2;
		
		int ii = i & 255;
		int jj = j & 255;
		
		int gi0 = perm[ii + perm[jj]] % 12;
		int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
		int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;
		
		float t0 = 0.5f - x0*x0 - y0*y0;
		if(t0 < 0)
			n0 = 0.0f;
		else{
			t0 *= t0;
			n0 = t0 * t0 * dot(grad3[gi0], x0, y0);
		}
		
		float t1 = 0.5f - x1*x1 - y1*y1;
		if(t1 < 0)
			n1 = 0.0f;
		else{
			t1 *= t1;
			n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
		}
		
		float t2 = 0.5f - x2 *x2 - y2 * y2;
		if(t2 < 0)
			n2 = 0.0f;
		else{
			t2 *= t2;
			n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
		}
		
		return 70.0f * (n0 + n1 + n2);
	}
	
	public static float noise(float x, float y, float frequency){
		return noise(x / frequency, y / frequency);
	}
	
	public static float octavedNoise(float x, float y){
		return (noise(x, y, 1f) * 1 +
				noise(x, y, 2f) * 2 +
				noise(x, y, 4f) * 4 +
				noise(x, y, 8f) * 8 +
				noise(x, y, 16f) * 16) / (1 + 2 + 4 + 8 + 16);
	}
	
	public static float[][] generateSimplexNoise(int width, int height, int octaves, float roughness, float scale){
		float[][] totalNoise = new float[width][height];
		float layerFrequency = scale;
		float layerWeight = 1;
		float weightSum = 0;
		
		for(int octave = 0; octave < octaves; octave++){
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					totalNoise[x][y] += noise(x * layerFrequency, y * layerFrequency) * layerWeight;
				}
			}
			
			layerFrequency *= 2;
			weightSum += layerWeight;
			layerWeight *= roughness;
		}
		
		return totalNoise;
	}
	
}
