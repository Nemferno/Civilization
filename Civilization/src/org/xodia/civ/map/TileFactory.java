package org.xodia.civ.map;

import org.xodia.civ.map.MapGenerator.Biome;
import org.xodia.civ.map.MapGenerator.LandType;
import org.xodia.civ.map.MapGenerator.TileType;

public class TileFactory {
	
	private TileFactory(){}
	
	public static Tile createTile(int x, int y, LandType lt, TileType tt, Biome b){
		int id = -1;
		
		switch(lt){
		case HILL:
			switch(tt){
			case FOREST:
				id = Tile.FOREST_HILL_ID;
				break;
			case JUNGLE:
				id = Tile.JUNGLE_HILL_ID;
				break;
			case NONE:
				switch(b){
				case Desert:
					id = Tile.DESERT_HILL_ID;
					break;
				case Temperate:
					id = Tile.TEMPERATE_HILL_ID;
					break;
				case Tundra:
					id = Tile.TUNDRA_HILL_ID;
					break;
				}
				break;
			}
			break;
		case MOUNTAIN:
			id = Tile.MOUNTAIN_ID;
			break;
		case PLAIN:
			switch(tt){
			case FOREST:
				id = Tile.FOREST_PLAIN_ID;
				break;
			case JUNGLE:
				id = Tile.JUNGLE_PLAIN_ID;
				break;
			case NONE:
				switch(b){
				case Desert:
					id = Tile.DESERT_PLAIN_ID;
					break;
				case Temperate:
					id = Tile.TEMPERATE_PLAIN_ID;
					break;
				case Tundra:
					id = Tile.TUNDRA_PLAIN_ID;
					break;
				}
				break;
			}
			break;
		case DEEPWATER:
			id = Tile.DEEP_WATER_ID;
			break;
		case SHALLOWWATER:
			id = Tile.SHALLOW_WATER_ID;
			break;
		}
		
		return new Tile(x, y, id);
	}
	
}
