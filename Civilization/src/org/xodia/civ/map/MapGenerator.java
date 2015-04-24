package org.xodia.civ.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.xodia.civ.map.Map.MapSize;
import org.xodia.civ.util.SimplexNoise;

public class MapGenerator {
	
	public static enum LandType {
		DEEPWATER(0), SHALLOWWATER(1), LAND(2), MOUNTAIN(4), HILL(3), PLAIN(5);
		
		private int id;
		
		LandType(int id){
			this.id = id;
		}
		
		public int getID(){
			return id;
		}
	}
	
	public static enum TileType {
		NONE(0), FOREST(1), JUNGLE(2);
		
		private int id;
		
		TileType(int id){
			this.id = id;
		}
		
		public int getID(){
			return id;
		}
	}
	
	public static enum Biome {
		Tundra, Desert, Temperate
	}
	
	private static SimplexNoise gen;
	
	private MapGenerator(){}
	
	public static List<TileID> createFromSeed(MapSeed seed){
		List<TileID> idList = new ArrayList<TileID>(seed.getSize().w * seed.getSize().h);
		
		gen = new SimplexNoise(seed.getLargestFeature(), seed.getPersistence(), seed.getSeed());
		
		float[][] data = new float[seed.getSize().w][seed.getSize().h];
		
		int offsetX = seed.getOffsetX();
		int offsetY = seed.getOffsetY();
		
		for(int i = 0; i < seed.getSize().w; i++){
			for(int j = 0; j < seed.getSize().h; j++){
				data[i][j] = (float) (0.5 * (1 + gen.getNoise(i + offsetX, j + offsetY)));
			}
		}
		
		float[][] temp = new float[seed.getSize().w][seed.getSize().h];
		float[][] rain = new float[seed.getSize().w][seed.getSize().h];
		
		SimplexNoise noise1 = new SimplexNoise(seed.getLargestFeature2(), seed.getPersistence2(), seed.getSeed2());
		SimplexNoise noise2 = new SimplexNoise(seed.getLargestFeature3(), seed.getPersistence3(), seed.getSeed3());
		
		for(int x = 0; x < seed.getSize().w; x++){
			for(int y = 0; y < seed.getSize().h; y++){
				temp[x][y] = (float) (0.5 * (1 + noise1.getNoise(x, y)));
				rain[x][y] = (float) (0.5 * (1 + noise2.getNoise(x, y)));
				
				rain[x][y] *= temp[x][y];
			}
		}
		
		// Then convert them into TileID
		for(int i = 0; i < seed.getSize().w; i++){
			for(int j = 0; j < seed.getSize().h; j++){
				float noise = data[i][j];
				
				if(noise < 0){
					noise = 0;
				}else if(noise > 1){
					noise = 1;
				}
				
				if(noise >= 0 && noise <= .55){
					idList.add(new TileID(i, j, LandType.DEEPWATER, TileType.NONE, getBiome(temp[i][j], rain[i][j])));
				}else if(noise > .55 && noise <= .60){
					idList.add(new TileID(i, j, LandType.SHALLOWWATER, TileType.NONE, getBiome(temp[i][j], rain[i][j])));
				}else if(noise > .60 && noise <= 1){
					// If it's a type land, we have to see if the temp and the rain is good!
					idList.add(new TileID(i, j, LandType.LAND, TileType.NONE, getBiome(temp[i][j], rain[i][j])));
				}
			}
		}
		
		// Convert the Land into Plain, Hill, Mountain
		List<TileID> newIDList = new ArrayList<TileID>(idList.size());
		Random random = new Random();
		
		int mountainP = 1;
		int hillP = 35;
		int plainP = 64;
		
		int forestP = 25;
		int noneP = 50;
		int jungleP = 25;
		
		for(TileID t : idList){
			if(t.getLandType() == LandType.LAND){
				int p = random.nextInt(100) + 1;
				int l = random.nextInt(100);
				LandType lt = LandType.PLAIN;
				TileType tt = TileType.NONE;
				
				if(p > 0 && p <= mountainP){
					mountainP = 10;
					hillP = 55;
					plainP = 30;
					lt = LandType.MOUNTAIN;
				}else if(p > mountainP && p <= mountainP + hillP){
					hillP = 49;
					plainP = 50;
					mountainP = 1;
					lt = LandType.HILL;
				}else if(p > mountainP + hillP && p <= mountainP + hillP + plainP){
					plainP = 89;
					hillP = 10;
					mountainP = 1;
					lt = LandType.PLAIN;
				}
				
				if(lt != LandType.MOUNTAIN){
					if(l > 0 && l <= forestP){
						if(t.getBiome() == Biome.Temperate){
							tt = TileType.FOREST;
						}
					}else if(l > forestP && l <= forestP + noneP){
						tt = TileType.NONE;
					}else if(l > forestP + noneP && l <= forestP + noneP + jungleP){
						if(t.getBiome() == Biome.Temperate){
							tt = TileType.JUNGLE;
						}
					}
				}

				newIDList.add(new TileID(t.getX(), t.getY(), lt, tt, t.getBiome()));
			}else{
				newIDList.add(new TileID(t.getX(), t.getY(), t.getLandType(), TileType.NONE, t.getBiome()));
			}
		}
		
		return newIDList;
	}
	
	public static Object[] createMap(MapSize size){
		List<TileID> idList = new ArrayList<TileID>(size.w * size.h);
		
		Random random = new Random();
		gen = new SimplexNoise(250, 0.5, random.nextInt());
		
		float[][] data = new float[size.w][size.h];
		
		int offsetX = random.nextInt();
		int offsetY = random.nextInt();
		
		for(int i = 0; i < size.w; i++){
			for(int j = 0; j < size.h; j++){
				data[i][j] = (float) (0.5 * (1 + gen.getNoise(i + offsetX, j + offsetY)));
			}
		}
		
		float[][] temp = new float[size.w][size.h];
		float[][] rain = new float[size.w][size.h];
		
		SimplexNoise noise1 = new SimplexNoise(150, 0.4, random.nextInt());
		SimplexNoise noise2 = new SimplexNoise(300, 0.4, random.nextInt());
		
		for(int x = 0; x < size.w; x++){
			for(int y = 0; y < size.h; y++){
				temp[x][y] = (float) (0.5 * (1 + noise1.getNoise(x, y)));
				rain[x][y] = (float) (0.5 * (1 + noise2.getNoise(x, y)));
				
				rain[x][y] *= temp[x][y];
			}
		}
		
		// Then convert them into TileID
		for(int i = 0; i < size.w; i++){
			for(int j = 0; j < size.h; j++){
				float noise = data[i][j];
				
				if(noise < 0){
					noise = 0;
				}else if(noise > 1){
					noise = 1;
				}
				
				if(noise >= 0 && noise <= .55){
					idList.add(new TileID(i, j, LandType.DEEPWATER, TileType.NONE, getBiome(temp[i][j], rain[i][j])));
				}else if(noise > .55 && noise <= .60){
					idList.add(new TileID(i, j, LandType.SHALLOWWATER, TileType.NONE, getBiome(temp[i][j], rain[i][j])));
				}else if(noise > .60 && noise <= 1){
					// If it's a type land, we have to see if the temp and the rain is good!
					idList.add(new TileID(i, j, LandType.LAND, TileType.NONE, getBiome(temp[i][j], rain[i][j])));
				}
			}
		}
		
		List<TileID> newIDList = new ArrayList<TileID>(idList.size());

		int mountainP = 1;
		int hillP = 35;
		int plainP = 64;
		
		int forestP = 25;
		int noneP = 50;
		int jungleP = 25;
		
		for(TileID t : idList){
			if(t.getLandType() == LandType.LAND){
				int p = random.nextInt(100) + 1;
				int l = random.nextInt(100);
				LandType lt = LandType.PLAIN;
				TileType tt = TileType.NONE;
				
				if(p > 0 && p <= mountainP){
					mountainP = 10;
					hillP = 55;
					plainP = 30;
					lt = LandType.MOUNTAIN;
				}else if(p > mountainP && p <= mountainP + hillP){
					hillP = 49;
					plainP = 50;
					mountainP = 1;
					lt = LandType.HILL;
				}else if(p > mountainP + hillP && p <= mountainP + hillP + plainP){
					plainP = 89;
					hillP = 10;
					mountainP = 1;
					lt = LandType.PLAIN;
				}
				
				if(lt != LandType.MOUNTAIN){
					if(l > 0 && l <= forestP){
						if(t.getBiome() == Biome.Temperate){
							tt = TileType.FOREST;
						}
					}else if(l > forestP && l <= forestP + noneP){
						tt = TileType.NONE;
					}else if(l > forestP + noneP && l <= forestP + noneP + jungleP){
						if(t.getBiome() == Biome.Temperate){
							tt = TileType.JUNGLE;
						}
					}
				}
				
				newIDList.add(new TileID(t.getX(), t.getY(), lt, tt, t.getBiome()));
			}else{
				newIDList.add(new TileID(t.getX(), t.getY(), t.getLandType(), TileType.NONE, t.getBiome()));
			}
		}
		
		MapSeed seed = new MapSeed(size, gen.getLargestFeature(), gen.getPersistence(), gen.getSeed(), 
				noise1.getLargestFeature(), noise1.getPersistence(), noise1.getSeed(),
				noise2.getLargestFeature(), noise2.getPersistence(), noise2.getSeed(), offsetX, offsetY);
		Object[] object = new Object[2];
		object[0] = newIDList;
		object[1] = seed;
		
		return object;
	}
	
	private static Biome getBiome(float temp, float rain){
		if(temp >= 0f && temp < 0.35f){
			return Biome.Tundra;
		}else if(temp > 0.35f && temp < 0.7f){
			if(rain >= 0f && rain <= 0.15f){
				return Biome.Desert;
			}else if(rain > .15f && rain <= 1f){
				return Biome.Temperate;
			}
		}else if(temp > 0.7f && temp <= 1f){
			if(rain >= 0f && rain <= 0.35f){
				return Biome.Desert;
			}else if(rain > .35f && rain <= 1f){
				return Biome.Temperate;
			}
		}
		
		return null;
	}
	
	public static class MapSeed {
		
		private double persistence,
						persistence2,
						persistence3;
		
		private int seed,
					seed2,
					seed3;
		
		private int largestFeature,
					largestFeature2,
					largestFeature3;
		
		private int offsetX, offsetY;
		
		private MapSize size;
		
		public MapSeed(){
			this(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		}
		
		public MapSeed(MapSize size, int largestFeature, double persistence, int seed,
				int largestFeature2, double persistence2, int seed2,
				int largestFeature3, double persistence3, int seed3, int offsetX, int offsetY){
			this.largestFeature = largestFeature;
			this.largestFeature2 = largestFeature2;
			this.largestFeature3 = largestFeature3;
			this.persistence = persistence;
			this.persistence2 = persistence2;
			this.persistence3 = persistence3;
			this.seed = seed;
			this.seed2 = seed2;
			this.seed3 = seed3;
			this.size = size;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
		
		public int getOffsetX(){
			return offsetX;
		}
		
		public int getOffsetY(){
			return offsetY;
		}
		
		public MapSize getSize(){
			return size;
		}
		
		public double getPersistence(){
			return persistence;
		}
		
		public double getPersistence2(){
			return persistence2;
		}
		
		public double getPersistence3(){
			return persistence3;
		}
		
		public int getSeed(){
			return seed;
		}
		
		public int getSeed2(){
			return seed2;
		}
		
		public int getSeed3(){
			return seed3;
		}
		
		public int getLargestFeature(){
			return largestFeature;
		}
		
		public int getLargestFeature2(){
			return largestFeature2;
		}
		
		public int getLargestFeature3(){
			return largestFeature3;
		}
		
	}
	
	public static class TileID {
		
		private int x, y;
		private TileType tType;
		private LandType lType;
		private Biome b;
		
		public TileID(int x, int y, LandType type, TileType type2, Biome b){
			this.x = x;
			this.y = y;
			this.lType = type;
			this.tType = type2;
			this.b = b;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public Biome getBiome(){
			return b;
		}
		
		public LandType getLandType(){
			return lType;
		}
		
		public TileType getTileType(){
			return tType;
		}
		
	}
	
}
