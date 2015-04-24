package org.xodia.civ.util;

public class PerlinGenerator {

	public static float[] performBlend(float[] noiseOne, float[] noiseTwo, float persistence){
		if(noiseOne != null && noiseTwo != null && noiseOne.length > 0 && noiseTwo.length == noiseTwo.length){
			float[] result = new float[noiseOne.length];
			for(int i = 0; i < noiseOne.length; i++){
				result[i] = noiseOne[i] + (noiseTwo[i] * persistence);
			}
			return result;
		}
		
		return null;
	}
	
	public static float[] performNormalize(float[] noise){
		if(noise != null && noise.length > 0){
			float[] result = new float[noise.length];
			
			float minValue = noise[0];
			float maxValue = noise[0];
			for(int i = 0; i < noise.length; i++){
				if(noise[i] < minValue){
					minValue = noise[i];
				}else if(noise[i] > maxValue){
					maxValue = noise[i];
				}
			}
			
			for(int i = 0; i < noise.length; i++){
				result[i] = (noise[i] - minValue) / (maxValue - minValue);
			}
			
			return result;
		}
		
		return null;
	}
	
	public static float[] createNoise(int width, int height, float exponent){
		int[] p = new int[width * height];
		float[] result = new float[width * height];
		
		for(int i = 0; i < p.length / 2; i++){
			p[i] = p[i + p.length / 2] = (int) (Math.random() * p.length / 2);
		}
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				float x = i * exponent / width;
				float y = j * exponent / height;
				int X = (int) Math.floor(x) & 255;
				int Y = (int) Math.floor(y) & 255;
				int Z = 0; // No 3D
				x -= Math.floor(x);
				y -= Math.floor(y);
				float u = fade(x);
				float v = fade(y);
				float w = fade(Z);
				int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z,      
                        B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z;
				
				result[j + i * width] = lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, Z),
                        grad(p[BA], x - 1, y, Z)),
                        lerp(u, grad(p[AB], x, y - 1, Z), 
                        grad(p[BB], x - 1, y - 1, Z))),
                        lerp(v, lerp(u, grad(p[AA + 1], x, y, Z - 1), 
                     	grad(p[BA + 1], x - 1, y, Z - 1)),
                     	lerp(u, grad(p[AB + 1], x, y - 1, Z - 1), grad(p[BB + 1], x - 1, y - 1, Z - 1))));
			}
		}
		
		return result;
	}
	
	public static float[] performSmooth(int width, int height, float zoom){
		if(zoom > 0){
			float[] noise = createWhiteNoises(width, height);
			float[] result = new float[width * height];
			
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height; j++){
					float x = i / zoom;
					float y = j / zoom;
					
					float fractX = x - (int) y;
					float fractY = y - (int) y;
					
					int x1 = ((int) x + width) % width;
					int y1 = ((int) y + height) % height;
					
					int x2 = (x1 + width - 1) % width;
					int y2 = (y1 + height - 1) % height;
					
					result[j + i * width] = fractX * fractY * noise[y1 + x1 * width]
                            + fractX * (1 - fractY) * noise[y2 + x1 * width]
                            + (1 - fractX) * fractY * noise[y1 + x2 * width]
                            + (1 - fractX) * (1 - fractY) * noise[y2 + x2 * width];
				}
			}
			
			return result;
		}
		
		return null;
	}
	
	public static float[] performTurbulence(int width, int height, float zoom){
		float[] result = new float[width * height];
		float initZoom = zoom;
		
		while(zoom >= 1){
			result = performBlend(result, performSmooth(width, height, zoom), zoom);
			zoom /= 2.0f;
		}
		
		for(int i = 0; i < result.length; i++)
			result[i] = (128.0f * result[i] / initZoom);
		
		return result;
	}
	
	public static float[] createWhiteNoises(int width, int height){
		float[] result = new float[width * height];
		for(int i = 0; i < width * height; i++){
			result[i] = (float) Math.random();
		}
		return result;
	}
	
	private static float fade(float t){
		return t * t * t * (t * (t * 6 - 15) + 10);
	}
	
	private static float lerp(float t, float a, float b){
		return a + t * (b - a);
	}
	
	private static float grad(int hash, float x, float y, float z){
		int h = hash & 15;
		float u = h < 8 ? x : y,                
                v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}
	
}
