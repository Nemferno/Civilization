package org.xodia.civ.map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Tile {
	
	public static final int DEEP_WATER_ID = 0,
							SHALLOW_WATER_ID = 1,
							TUNDRA_PLAIN_ID = 2,
							TUNDRA_HILL_ID = 3,
							TEMPERATE_PLAIN_ID = 4,
							TEMPERATE_HILL_ID = 5,
							FOREST_HILL_ID = 6,
							FOREST_PLAIN_ID = 7,
							JUNGLE_HILL_ID = 8,
							JUNGLE_PLAIN_ID = 9,
							DESERT_PLAIN_ID = 10,
							DESERT_HILL_ID = 11,
							MOUNTAIN_ID = 12;
	
	public static final int NOT_MET_STATUS = 1, // Not see
							MET_STATUS = 2, // Saw it but not Seeing it
							MEET_STATUS = 3; // Seeing it
	
	/**
	 * @param x
	 * The x in tiles
	 * @param y
	 * The y in tiles
	 */
	public Tile(int x, int y, int id){
		this.x = x;
		this.y = y;
		this.id = id;
		this.currentFOVStatus = MEET_STATUS;
		this.ownedCivID = -1;
		
		switch(id){
		case DEEP_WATER_ID:
			cost = 1;
			break;
		case SHALLOW_WATER_ID:
			cost = 1;
			break;
		case TUNDRA_PLAIN_ID:
			cost = 1;
			break;
		case TUNDRA_HILL_ID:
			cost = 2;
			break;
		case TEMPERATE_HILL_ID:
			cost = 2;
			break;
		case TEMPERATE_PLAIN_ID:
			cost = 1;
			break;
		case FOREST_HILL_ID:
			cost = 2;
			break;
		case FOREST_PLAIN_ID:
			cost = 1;
			break;
		case DESERT_HILL_ID:
			cost = 2;
			break;
		case DESERT_PLAIN_ID:
			cost = 1;
			break;
		case JUNGLE_HILL_ID:
			cost = 2;
			break;
		case JUNGLE_PLAIN_ID:
			cost = 1;
			break;
		}
	}
	
	public void render(float x, float y, Graphics g){
		if(currentFOVStatus == NOT_MET_STATUS){
			g.setColor(Color.black);
		}else{
			switch(id){
			case DEEP_WATER_ID:
				g.setColor(new Color(81, 81, 255));
				break;
			case SHALLOW_WATER_ID:
				g.setColor(new Color(158, 158, 158));
				break;
			case TUNDRA_PLAIN_ID:
				g.setColor(new Color(178, 233, 255));
				break;
			case TUNDRA_HILL_ID:
				g.setColor(new Color(89, 208, 255));
				break;
			case TEMPERATE_HILL_ID:
				g.setColor(new Color(115, 205, 97));
				break;
			case TEMPERATE_PLAIN_ID:
				g.setColor(new Color(115, 206, 97));
				break;
			case FOREST_HILL_ID:
				g.setColor(new Color(85, 153, 71));
				break;
			case FOREST_PLAIN_ID:
				g.setColor(new Color(116, 153, 108));
				break;
			case DESERT_HILL_ID:
				g.setColor(new Color(219, 214, 155));
				break;
			case DESERT_PLAIN_ID:
				g.setColor(new Color(234, 228, 166));
				break;
			case JUNGLE_HILL_ID:
				g.setColor(new Color(152, 181, 128));
				break;
			case JUNGLE_PLAIN_ID:
				g.setColor(new Color(165, 196, 139));
				break;
			case MOUNTAIN_ID:
				g.setColor(new Color(153, 153, 153));
				break;
			}
			
			g.fillRect(x + (getX() * 32), y + (getY() * 32), 32, 32);
		}
		
		if(currentFOVStatus == MET_STATUS){
			g.setColor(new Color(Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue(), 0.5f));
			g.fillRect(x + (getX() * 32), y + (getY() * 32), 32, 32);
		}
	}
	
	public void setCountryOwned(long id){
		ownedCivID = id;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setFOVStatus(int status){
		this.currentFOVStatus = status;
	}
	
	public long getCountryOwned(){
		return ownedCivID;
	}
	
	public int getFOVStatus(){
		return currentFOVStatus;
	}
	
	public int getID(){
		return id;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getCost(){
		return cost;
	}
	
	/**
	 * The x in tiles 
	 */
	private int x;
	/**
	 * The y in tiles
	 */
	private int y;
	/**
	 * The ID of the tile
	 */
	private int id;
	/**
	 * The cost to move to the object
	 */
	private int cost;
	/*
	 * Current Fog of War Status
	 */
	private int currentFOVStatus;
	/**
	 * The Civilization that currently owns it...
	 */
	private long ownedCivID;
	
}
