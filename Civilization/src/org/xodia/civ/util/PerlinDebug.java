package org.xodia.civ.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class PerlinDebug {
	
	public static void main(String[] args) throws Exception{
		int width = 512;
		int height = 512;
		float[][] data = null;
		
		SimplexNoise noise = new SimplexNoise(250, 0.5, new Random().nextInt());
		
		data = new float[width][height];
		
		Random random = new Random();
		
		int offsetX = random.nextInt();
		int offsetY = random.nextInt();
		
		float[][] temp = new float[width][height];
		float[][] rain = new float[width][height];
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				//int x = (int) (0 + i * ((512 - 0) / width));
				//int y = (int) (0 + j * ((512 - 0) / height));
				data[i][j] = (float) (0.5 * (1 + noise.getNoise(i + offsetX, j + offsetY)));
				System.out.print(data[i][j]);
			}
			
			System.out.println();
		}
		
		noise = new SimplexNoise(150, 0.4, new Random().nextInt());
		SimplexNoise noise2 = new SimplexNoise(300, 0.4, new Random().nextInt());
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				temp[x][y] = (float) (0.5 * (1 + noise.getNoise(x, y)));
				rain[x][y] = (float) (0.5 * (1 + noise2.getNoise(x, y)));
			}
		}
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				rain[x][y] *= temp[x][y];
			}
		}
		
		
		
		/*float[] data = PerlinGenerator.performNormalize(PerlinGenerator.createNoise(100, 100, 2));
		
		for(int i = 0; i < data.length; i++){
			data[i] = 255 * data[i];
		}
		
		int index = 0;
		
		for(int x = 0; x < 100; x++){
			for(int y = 0; y < 100; y++){
				System.out.print(data[index++] + ", ");
			}
			
			System.out.println();
		}
		
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY);
		image.getRaster().setPixels(0, 0, 100, 100, data);*/
		
		/*int width = 512;
		int height = 512;
		
		float[][] data = SimplexNoiseGenerator.generateSimplexNoise(width, height, 4, 0.4f, 0.005f);
		
		for(int x = 0; x < width; x++){
			for(int j = 0; j < height; j++){
				System.out.print(data[x][j]);
			}
			
			System.out.println();
		}*/
		
		/*for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				data[i][j] = (int) SimplexNoiseGenerator.octavedNoise(i, j, 4, 0.5f, 0.1f);
				System.out.print(data[i][j]);
			}
			System.out.println();
		}*/
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage image3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage image4 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int y = 0; y < data[0].length; y++){
			for(int x = 0; x < data.length; x++){
				if(data[x][y] > 1)
					data[x][y] = 1;
				
				if(data[x][y] < 0)
					data[x][y] = 0;
				
				Color col2 = new Color(data[x][y], data[x][y], data[x][y]);
				image2.setRGB(x, y, col2.getRGB());
				
				Color col = null;
				
				if(data[x][y] >= 0 && data[x][y] <= .60f){
					col = Color.blue;
				}else if(data[x][y] <= 1 && data[x][y] > 0.6f){
					col = Color.green;
				}else{
					col = Color.white;
				}
				
				//Color col = new Color(data[x][y], data[x][y], data[x][y]);
				image.setRGB(x, y, col.getRGB());
				
				if(temp[x][y] > 1)
					temp[x][y] = 1;
				
				if(temp[x][y] < 0)
					temp[x][y] = 0;
				
				Color col3 = Color.black;
				
				float temperature = temp[x][y];
				
				if(temperature >= 0 && temperature < .35){
					col3 = Color.blue;
					// Snow
				}else if(temperature > .35 && temperature < 0.70){
					col3 = Color.lightGray;
					// Normal
				}else if(temperature > .70 && temperature <= 1){
					col3 = Color.orange;
					// Desert
				}
				
				image3.setRGB(x, y, col3.getRGB());
				
				if(rain[x][y] > 1)
					rain[x][y] = 1;
				
				if(rain[x][y] < 0)
					rain[x][y] = 0;
				
				float wetness = rain[x][y];
				Color col4 = Color.black;
				
				if(wetness >= 0 && wetness < .15){
					col4 = Color.orange; // Very dry!
				}else if(wetness > .15 && wetness < .25){
					col4 = Color.yellow; // Eh~ Moderate
				}else if(wetness > .25 && wetness < .35){
					col4 = Color.cyan; // Kind of Wet
				}else if(wetness > 0.35 && wetness <= 1){
					col4 = Color.blue; // Really Wet!
				}
				
				image4.setRGB(x, y, col4.getRGB());
			}
		}
		
		File output = new File("perlin.png");
		output.createNewFile();
		File output2 = new File("perlin2.png");
		output2.createNewFile();
		File output3 = new File("temp.png");
		output3.createNewFile();
		File output4 = new File("rain.png");
		output4.createNewFile();
		
		ImageIO.write(image, "png", output);
		ImageIO.write(image2, "png", output2);
		ImageIO.write(image3, "png", output3);
		ImageIO.write(image4, "png", output4);
	}
	
}
