package org.xodia.civ.util;

import java.util.Random;

/**
 * 
 * This is used to generate a land depending on the size
 * 
 * @author Jasper Bae
 *
 */
public class PerlinNoiseGenerator {

	private PerlinNoiseGenerator(){}
	
	public static float[][] generatePerlinNoises(int width, int height, int octave, int octave2){
		float[][] base = generateSmoothNoises(generateWhiteNoises(width, height), octave);
		
		int w = base.length;
		int h = base[0].length;
		
		float[][][] smoothNoise = new float[octave2][][];
		float persistance = 0.5f;
		
		for(int i = 0; i < octave2; i++){
			smoothNoise[i] = generateSmoothNoises(base, i);
		}
		
		float[][] perlinNoise = new float[w][h];
		float amplitude = 1.0f;
		float totalAmplitude = 0.0f;
		
		for(int o = octave2 - 1; o >= 0; o--){
			amplitude *= persistance;
			totalAmplitude += amplitude;
			
			for(int x = 0; x < w; x++){
				for(int y = 0; y < h; y++){
					perlinNoise[x][y] += smoothNoise[o][x][y] * amplitude;
				}
			}
		}
		
		for(int x = 0; x < w; x++){
			for(int y = 0; y < h; y++){
				perlinNoise[x][y] /= totalAmplitude;
			}
		}
		
		return perlinNoise;
	}
	
	private static float[][] generateWhiteNoises(int width, int height){
		Random random = new Random((long) (Math.round(Math.random() * 100 * Math.random() * 10)));
		float[][] noise = new float[width][height];
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				noise[x][y] = (float) (Math.random() % 1);
			}
		}
		
		return noise;
	}
	
	private static float[][] generateSmoothNoises(float[][] seed, int octave){
		int width = seed.length;
		int height = seed.length;
		
		float[][] newSeed = new float[width][height];
		
		int samplePeriod = 1 << octave;
		float sampleFrequency = 1.0f / samplePeriod;
		
		for(int x = 0; x < width; x++){
			int sample_i0 = (x / samplePeriod) * samplePeriod;
			int sample_i1 = (sample_i0 + samplePeriod) % width;
			float horizontal_blend = (x - sample_i0) * sampleFrequency;
			
			for(int y = 0; y < height; y++){
				int sample_j0 = (y / samplePeriod) * samplePeriod;
				int sample_j1 = (sample_j0 + samplePeriod) % height;
				float vertical_blend = (y - sample_j0) * sampleFrequency;
				
				float top = interpolate(seed[sample_i0][sample_j0], seed[sample_i1][sample_j0], horizontal_blend);
				float bot = interpolate(seed[sample_i0][sample_j1], seed[sample_i1][sample_j1], horizontal_blend);
				
				newSeed[x][y] = interpolate(top, bot, vertical_blend);
			}
		}
		
		return newSeed;
	}
	
	private static float interpolate(float x, float x1, float alpha){
		return x * (1 - alpha) + alpha * x1;
	}
	
}
