package org.xodia.civ.units;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.xodia.civ.map.Tile;

/**
 * 
 * Basics of a unit class
 * It consists of:
 * x and y position (in tiles)
 * health
 * 
 * @author Jasper Bae
 *
 */
public abstract class Unit {

	/**
	 * 
	 * Grants the unit an unique ability that the game handles
	 * depending on the type of ability
	 * 
	 * @author Jasper Bae
	 *
	 */
	public static enum Ability {
		
		SettleTown(0);
		
		int turncost;
		
		Ability(int turncost){
			this.turncost = turncost;
		}
		
		public int getTurnCost(){
			return turncost;
		}
	}
	
	public static enum UnitType {
		
		ShallowWater(new int[]{ Tile.SHALLOW_WATER_ID }), 
		DeepWater(new int[]{ Tile.DEEP_WATER_ID, Tile.SHALLOW_WATER_ID }), 
		Land(new int[]{ Tile.DESERT_HILL_ID, Tile.DESERT_PLAIN_ID, Tile.FOREST_HILL_ID, Tile.FOREST_PLAIN_ID,
				Tile.JUNGLE_HILL_ID, Tile.JUNGLE_PLAIN_ID, Tile.TEMPERATE_HILL_ID, Tile.TEMPERATE_PLAIN_ID,
				Tile.TUNDRA_HILL_ID, Tile.TUNDRA_PLAIN_ID }),
		ShallowAmphibious(new int[]{ Tile.SHALLOW_WATER_ID, Tile.DESERT_HILL_ID, Tile.DESERT_PLAIN_ID, Tile.FOREST_HILL_ID, Tile.FOREST_PLAIN_ID,
				Tile.JUNGLE_HILL_ID, Tile.JUNGLE_PLAIN_ID, Tile.TEMPERATE_HILL_ID, Tile.TEMPERATE_PLAIN_ID,
				Tile.TUNDRA_HILL_ID, Tile.TUNDRA_PLAIN_ID }),
		DeepAmphibious(new int[]{ Tile.SHALLOW_WATER_ID, Tile.DEEP_WATER_ID, Tile.DESERT_HILL_ID, Tile.DESERT_PLAIN_ID, Tile.FOREST_HILL_ID, Tile.FOREST_PLAIN_ID,
				Tile.JUNGLE_HILL_ID, Tile.JUNGLE_PLAIN_ID, Tile.TEMPERATE_HILL_ID, Tile.TEMPERATE_PLAIN_ID,
				Tile.TUNDRA_HILL_ID, Tile.TUNDRA_PLAIN_ID });
		
		private int[] acceptTiles;
		
		UnitType(int[] acceptableTile){
			acceptTiles = acceptableTile;
		}
		
		public boolean isAcceptable(int id){
			for(int i : acceptTiles){
				if(i == id)
					return true;
			}
			
			return false;
		}
		
	}
	
	public static enum CombatType {
		
		Range, Melee, Artillery, NonCombatant
		
	}
	
	public static enum ID {
		
		Settler(0, true), Warrior(1, false), Archer(2, false), Horseman(3, false), Trebuchet(4, false),
		Rifleman(5, false), Cannon(6, false),
		Tank(7, false), Infantry(8, false), Artillery(9, false),
		ModernTank(10, false), ModernInfantry(11, false), ModernArtillery(12, false),
		Schooner(13, false), Frigate(14, false), Destroyer(15, false);
		
		private int id;
		private boolean isNonCombatant;
		
		ID(int id, boolean nonCombatant){
			this.id = id;
			this.isNonCombatant = nonCombatant;
		}
		
		public boolean isNonCombatant(){
			return isNonCombatant;
		}
		
		public int getID(){
			return id;
		}
		
	}
	
	public static interface TurnCostListener {
		void finishTurnCost();
	}

	/**
	 * The name of the unit
	 */
	private String name;
	/**
	 * The ID of the unit
	 */
	private long id;
	
	/**
	 * The ID of the player (civ)
	 */
	private long playerID;
	
	/**
	 * It's location
	 */
	private int x, y;
	
	/**
	 * Current Health and Max Health
	 */
	private int currentHealth, maxHealth;
	
	/**
	 * The melee power of the unit
	 */
	private int strength;
	
	/**
	 * The range power of the unit
	 */
	private int rStrength;
	
	/**
	 * Type of Unit in terms of how it moves around
	 */
	private UnitType type;
	
	/**
	 * The amount of points you have to move from one tile to another
	 */
	private int curMP;
	
	/*
	 * The max amount of points you have per turn
	 */
	private int maxMP;
	
	/**
	 * Sight Range
	 */
	private int sight;
	
	/**
	 * A boolean which it allows the game to update the
	 * lighting of the unit in the Fog of War
	 */
	private boolean canUpdateFOV;
	
	/**
	 * Means it is taking an action that takes more than one turn
	 */
	private boolean isAction;
	
	/**
	 * Means that it is sieging a town currently until the town's defenses are zero!
	 */
	private boolean isSieging;
	
	/**
	 * The town it is sieging
	 */
	private long townSiegedID;
	
	/**
	 * The town being sieged currently owned by...
	 */
	private long townSiegedPlayerID;
	
	/**
	 * Identity of the Unit
	 */
	private ID unitID;
	
	/**
	 * Describes the type of the unit
	 */
	private CombatType combatType;
	
	/**
	 * List of abilities that the unit has
	 */
	private List<Ability> abilityList;
	
	private Image unitImage;
	private Image actionImage;
	
	private boolean hasSkipped;
	
	public Unit(long playerID, long id, int x, int y){
		this.x = x;
		this.y = y;
		this.id = id;
		this.playerID = playerID;
		this.canUpdateFOV = true;
		
		abilityList = new ArrayList<Ability>();
	}
	
	protected void addAbility(Ability ability){
		try{
			if(abilityList.contains(ability))
				throw new Exception("Duplicate ability for this unit!");
			
			abilityList.add(ability);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void setName(String name){
		this.name = name;
	}
	
	public void setSkipped(boolean skip){
		hasSkipped = skip;
	}
	
	public void setTownSiegedID(long id){
		townSiegedID = id;
	}
	
	public void setTownSiegedPlayerID(long id){
		townSiegedPlayerID = id;
	}
	
	public void setAction(boolean isAction){
		this.isAction = isAction;
	}
	
	public void setSieging(boolean sieging){
		this.isSieging = sieging;
	}
	
	public void setUpdateFOV(boolean can){
		canUpdateFOV = can;
	}
	
	public void setRangeStrength(int str){
		this.rStrength = str;
	}
	
	public void setUnitType(UnitType type){
		this.type = type;
	}
	
	protected void setCombatType(CombatType type){
		this.combatType = type;
	}
	
	public void setSight(int sight){
		this.sight = sight;
	}
	
	protected void setMaxMovePoints(int max){
		maxMP = max;
	}
	
	protected void setStrength(int str){
		strength = str;
	}
	
	public void setCurrentMovePoints(int cur){
		curMP = cur;
	}
	
	public void setCurrentHealth(int health){
		currentHealth = health;
	}
	
	public void setMaxHealth(int health){
		maxHealth = health;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setUnitImage(Image image){
		unitImage = image;
	}
	
	public void setActionImage(Image image){
		actionImage = image;
	}
	
	protected void setUnitID(ID id){
		this.unitID = id;
	}
	
	public boolean hasSkipped(){
		return hasSkipped;
	}
	
	public String getName(){
		return name;
	}
	
	public long getPlayerID(){
		return playerID;
	}
	
	public long getTownSiegedID(){
		return townSiegedID;
	}
	
	public long getTownSiegedPlayerID(){
		return townSiegedPlayerID;
	}
	
	public ID getUnitID(){
		return unitID;
	}
	
	public boolean isAction(){
		return isAction;
	}
	
	public boolean isSieging(){
		return isSieging;
	}
	
	public int getStrength(){
		return strength;
	}
	
	public int getMaxMovePoints(){
		return maxMP;
	}
	
	public int getCurrentMovePoints(){
		return curMP;
	}
	
	public int getSight(){
		return sight;
	}

	public boolean canUpdateFOV(){
		return canUpdateFOV;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getCurrentHealth(){
		return currentHealth;
	}
	
	public int getMaxHealth(){
		return maxHealth;
	}
	
	public int getRangeStrength(){
		return rStrength;
	}
	
	public UnitType getUnitType(){
		return type;
	}
	
	public CombatType getCombatType(){
		return combatType;
	}
	
	public long getID(){
		return id;
	}
	
	public List<Ability> getAbilities(){
		return abilityList;
	}
	
	public void render(Graphics g){
		g.setColor(Color.blue);
		g.drawString(name, getX() * 32 - 16, getY() * 32 - 16);
		
		g.setColor(Color.red);
		g.fillRect(getX() * 32 + 38, getY() * 32 + (32 - ((getCurrentHealth() <= 0 ? 0 : getCurrentHealth() / getMaxHealth()) * 32)), 3, ((getCurrentHealth() <= 0 ? 0 : getCurrentHealth() / getMaxHealth()) * 32));
		g.setColor(Color.black);
		g.drawRect(getX() * 32 + 37, getY() * 32, 3, 32);
		
		if(isAction){
			g.drawImage(actionImage, getX() * 32, getY() * 32);
		}else{
			g.drawImage(unitImage, getX() * 32, getY() * 32);
		}
	}
	
}
