package org.xodia.civ.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.xodia.civ.civs.Civilization.Civ;

public class CivSpawnManager {

	public static class CivSpawn {
		private int tx;
		private int ty;
		
		public CivSpawn(int tx, int ty){
			this.tx = tx;
			this.ty = ty;
		}
		
		public int getTileX(){
			return tx;
		}
		
		public int getTileY(){
			return ty;
		}
		
		public String toString(){
			return "Civ Spawn X: " + tx + " & Y: " + ty;
		}
	}
	
	private CivSpawnManager(Map map, HashMap<Long, Civ> civList){
		this.civList = civList;
		this.map = map;
		
		civSpawnList = new HashMap<Long, CivSpawn>();
	}
	
	public void generateSpawns(){
		Random random = new Random();
		
		int offsetY = 0;
		
		for(long id : civList.keySet()){
			List<CivSpawn> spawns = new ArrayList<CivSpawn>();
			for(int j = offsetY; j < map.getTileList().size(); j++){
				int tID = map.getTileList().get(j).getID();
				if(tID != Tile.DEEP_WATER_ID && tID != Tile.SHALLOW_WATER_ID && tID != Tile.MOUNTAIN_ID){
					int add = random.nextInt(100);
					
					if(add <= 25){
						spawns.add(new CivSpawn(map.getTileList().get(j).getX(), map.getTileList().get(j).getY()));
						
						j += 25;
					}else{
						// We can add a bit of randomization
						int tx = map.getTileList().get(j).getX() + (random.nextInt(map.getWidth()) + 25) / 2;
						int ty = map.getTileList().get(j).getY() + (random.nextInt(map.getHeight()) + 25) / 2;
						
						if(tx >= map.getWidth()){
							tx /= 2;
						}
						
						if(ty >= map.getHeight()){
							ty /= 2;
						}
						
						// Find the tx and ty
						for(Tile t : map.getTileList()){
							if(t.getX() == tx && t.getY() == ty){
								if(t.getID() != Tile.DEEP_WATER_ID && tID != Tile.SHALLOW_WATER_ID && tID != Tile.MOUNTAIN_ID){
									int prob = random.nextInt(100);
									if(prob <= 45){
										spawns.add(new CivSpawn(tx, ty));
									}
								}
								
								break;
							}
						}
					}
					
					// Check if we have 5 spawns
					if(spawns.size() >= 35){
						break;
					}
				}
			}
			
			int spawn = random.nextInt(spawns.size());
			civSpawnList.put(id, spawns.get(spawn));
			
			offsetY += random.nextInt(15) + 10;
		}
	}
	
	public CivSpawn getSpawn(long id){
		return civSpawnList.get(id);
	}
	
	public static CivSpawnManager createManager(Map map, HashMap<Long, Civ> civList){
		return new CivSpawnManager(map, civList);
	}
	
	/**
	 * The map which the manager is basing it's spawns on
	 */
	private Map map;
	/**
	 * Contains the list of the player's civilization
	 */
	private HashMap<Long, Civ> civList;
	/**
	 * Contains the spawn point of the civilization's first town
	 * FINAL LIST
	 */
	private HashMap<Long, CivSpawn> civSpawnList;
	
}
