package org.xodia.civ.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.util.ResourceLoader;
import org.xodia.civ.map.MapGenerator.MapSeed;
import org.xodia.civ.map.MapGenerator.TileID;
import org.xodia.civ.units.Unit;

/**
 * 
 * 2 constructors that takes in in order to create a map for the game.
 * The first constructor takes in a Map Generator that only makes on map
 * per creation.
 * The second constructor takes in a file name
 * 
 * Map should not have the role of rendering!
 * 
 * @author Jasper Bae
 *
 */
public class Map {

	public static enum MapSize{
		
		/**
		 * Small:
		 * - Continent
		 * - Islands
		 * Standard:
		 * - Continents
		 * - Islands
		 * - Peninsulas
		 * Large:
		 * - Continents
		 * - Islands
		 * - Peninsulas
		 * - Archipelago
		 */
		SMALL(128, 128), STANDARD(256, 256), LARGE(512, 512);
		
		int w, h;
		
		MapSize(int w, int h){
			this.w = w;
			this.h = h;
		}
		
		public int getWidthInTiles(){
			return w;
		}
		
		public int getHeightInTiles(){
			return h;
		}
		
		public String toString(){
			return super.toString() + " [ " + w + "x" + h + " ]";
		}
		
	}
	
	/**
	 * Secret constructor that takes in the size of the map
	 * 
	 * @param size
	 * The size of the map
	 */
	private Map(MapSize size){
		this.size = size;
	}
	
	/**
	 * This constructor takes in a filename and retrieves the
	 * map data.
	 * 
	 * @param filename
	 * The path to the map file
	 */
	public Map(String filename, MapSize size){
		this(size);
		
		this.filename = filename;
	}
	
	/**
	 * Must use this first in order to generate a map
	 */
	@SuppressWarnings("unchecked")
	public void generateMap(){
		if(!isMapGenerated){
			// This is using a set map
			mapList = new ArrayList<Tile>(size.w * size.h);
			
			// To determine if it is a map generator or a map file, it checks to see if filename is null
			if(filename != null){
				try{
					BufferedReader input = new BufferedReader(new FileReader(new File(ResourceLoader.getResource(filename).toURI())));
					String string = null;
					int y = 0;
					
					while((string = input.readLine()) != null){
						String chars[] = string.split(",");
						
						for(int x = 0; x < size.w; x++){
							//mapList.add(TileFactory.createTile(x, y, Integer.parseInt(chars[x]), Biome.Temperate));
						}
						
						y++;
					}
					
					input.close();
				}catch (URISyntaxException e) {
					e.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}
				
			}else{
				Object[] objects = MapGenerator.createMap(size);
				List<TileID> gen = (List<TileID>) objects[0];
				seed = (MapSeed) objects[1];
				
				for(int i = 0; i < gen.size(); i++){
					mapList.add(TileFactory.createTile(gen.get(i).getX(), gen.get(i).getY(), gen.get(i).getLandType(), gen.get(i).getTileType(), gen.get(i).getBiome()));
				}
			}
			
			isMapGenerated = true;
		}else{
			throw new RuntimeException("Map has already been generated!");
		}
	}
	
	public void generateMap(MapSeed seed){
		mapList = new ArrayList<Tile>(size.w * size.h);
		
		List<TileID> gen = MapGenerator.createFromSeed(seed);
		
		for(int i = 0; i < gen.size(); i++){
			mapList.add(TileFactory.createTile(gen.get(i).getX(), gen.get(i).getY(), gen.get(i).getLandType(), gen.get(i).getTileType(), gen.get(i).getBiome()));
		}
		
		isMapGenerated = true;
	}
	
	public int getWidth(){
		return size.w;
	}
	
	public int getHeight(){
		return size.h;
	}
	
	public boolean isBlocked(Unit.UnitType type, int x, int y){
		for(Tile t : mapList){
			if(t.getX() == x && t.getY() == y){
				if((t.getFOVStatus() == Tile.MEET_STATUS || t.getFOVStatus() == Tile.MET_STATUS))
					return !type.isAcceptable(t.getID());
				else
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isShore(int x, int y){
		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				if(!((x + i) < 0 && (y + j) < 0 && (x + i) > size.w && (y + j) > size.h)){
					Tile t = getTileAt(x + i, y + j);
					
					if(t != null){
						if(t.getID() == Tile.SHALLOW_WATER_ID){
							return true;
						}
					}else{
						return false;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isLandUnitInWater(int x, int y){
		for(Tile t : mapList){
			if(t.getID() == Tile.DEEP_WATER_ID || t.getID() == Tile.SHALLOW_WATER_ID){
				if(t.getX() == x && t.getY() == y){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Tile getTileAt(int x, int y){
		for(Tile t : mapList){
			if(t.getX() == x && t.getY() == y){
				return t;
			}
		}
		
		return null;
	}
	
	public int getCost(int x, int y){
		for(Tile t : mapList){
			if(t.getX() == x && t.getY() == y){
				return t.getCost();
			}
		}
		
		return -1;
	}
	
	public List<Tile> getTileList(){
		return mapList;
	}
	
	public MapSeed getSeed(){
		return seed;
	}
	
	/**
	 * A flag to make sure that the map only GENERATES ONCE
	 */
	private boolean isMapGenerated;
	
	/** 
	 * The path toward the file of a set map
	 */
	private String filename;
	
	/**
	 * The list of tiles of the map, used to:
	 * render the map
	 * A* algorithm
	 * town set up
	 * spawn locations
	 */
	private List<Tile> mapList;
	
	/**
	 * Size of the map
	 */
	private MapSize size;
	
	/**
	 * The seed of the map
	 */
	private MapSeed seed;
	
}
