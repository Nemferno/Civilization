package org.xodia.civ.civs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xodia.civ.civs.Civilization.Civ.CivAbility;
import org.xodia.civ.units.Town;
import org.xodia.civ.units.Unit;

/**
 * 
 * This is the player.
 * Consists of all the data needed.
 * 
 * @author Jasper Bae
 *
 */
public class Civilization {

	public static class Civ {
		
		public static enum CivPackage {
			
			Seafarer("Seafarer", new Research[]{ Research.Agriculture, Research.Sailing }), Military("Military", new Research[]{ Research.Agriculture, Research.Tools }), Pacifist("Pacifist", new Research[]{ Research.Agriculture, Research.Wheel });
			
			private String name;
			private Research[] starter;
			
			CivPackage(String name, Research[] starter){
				this.name = name;
				this.starter = starter;
			}
			
			public String getName(){
				return name;
			}
			
			public Research[] getStarterResearch(){
				return Arrays.copyOf(starter, starter.length);
			}
			
		}
		
		// Stats like growth increasing by %
		// and/or movement speed costs etc...
		public static enum CivAbility {
			
			Forced_March("Info:\nAllows all units to have +1 movement."), Slavery("Info:\nAllows the time of production to decrease by 1 turn. When a town is being sieged, decrease the time by 2 turns."), 
			Bargaining("Info:\nDecrease the cost of research and units."), Bushido("Info:\nAll units will do their full damage even if they are wounded.");
			
			private String description;
			
			CivAbility(String desc){
				description = desc;
			}
			
			public String getDescription(){
				return description;
			}
			
		}
		
		private String name;
		private List<CivAbility> abilityList;
		private List<Research> starterRList;
		
		public Civ(String name, Research[] starterList, CivAbility... abilities){
			this.name = name;
			
			abilityList = new ArrayList<CivAbility>();
			starterRList = new ArrayList<Research>();
			
			for(CivAbility a : abilities){
				abilityList.add(a);
			}
			
			for(Research r : starterList){
				starterRList.add(r);
			}
			
		}
		
		public String getName(){
			return name;
		}
		
		public CivAbility[] getAbilityList(){
			return abilityList.toArray(new CivAbility[abilityList.size()]);
		}
		
		public Research[] getStartResearchList(){
			return starterRList.toArray(new Research[starterRList.size()]);
		}
		
	}
	
	public static enum Research {
		
		// ECONOMICS
		Agriculture("Agriculture", 5, 500, new Research[]{}), Wheel("Wheel", 5, 500, new Research[]{ Agriculture }), AnimalHusbandry("Animal Husbandry", 5, 500, new Research[]{ Agriculture }),
		Mathematics("Mathematics", 5, 500, new Research[]{ Wheel }), Calendar("Calendar", 5, 500, new Research[]{ Mathematics }),
		Currency("Currency", 5, 500, new Research[]{ Mathematics }), Engineering("Engineering", 5, 500, new Research[]{ Mathematics }), MetalCasting("Metal Casting", 5, 500, new Research[]{ Mathematics }),
		Banking("Banking", 5, 500, new Research[]{ Currency }), Machinery("Machinery", 5, 500, new Research[]{ Engineering }), Steel("Steel", 5, 500, new Research[]{ Engineering, MetalCasting }),
		Industrialization("Industrialization", 5, 500, new Research[]{ Machinery, Steel }),
		
		// MILITARY
		Fletching("Fletching", 5, 500, new Research[]{}), Tools("Tools", 5, 500, new Research[]{}), HorsebackRiding("Horseback Riding", 5, 500, new Research[]{}), IronWorks("Iron Works", 5, 500, new Research[]{ Tools }),
		Combustion("Combustion", 5, 500, new Research[]{ IronWorks }), Ballistic("Ballistic", 5, 500, new Research[]{ Combustion }), GunPowder("Gun Powder", 5, 500, new Research[]{ Ballistic }),
		ArmorPlating("Armor Plating", 5, 500, new Research[]{ Combustion }), BreachLoading("Breach Loading", 5, 500, new Research[]{ GunPowder }),
		ModernTech("Modern Tech", 5, 500, new Research[]{ BreachLoading, ArmorPlating }),
		Sailing("Sailing", 5, 500, new Research[]{}), Compass("Compass", 5, 500, new Research[]{ Sailing }), Optic("Optic", 5, 500, new Research[]{ Compass });
		
		private int cost;
		private int turns;
		
		private String name;
		
		private List<Research> requiredList;
		
		Research(String name, int turns, int cost, Research[] required){
			this.cost = cost;
			this.turns = turns;
			this.name = name;
			
			requiredList = new ArrayList<Research>();
			
			for(Research r : required){
				requiredList.add(r);
			}
		}
		
		public int getTurns(){
			return turns;
		}
		
		public int getCost(){
			return cost;
		}
		
		public String getName(){
			return name;
		}
		
		public Research[] getRequiredList(){
			return requiredList.toArray(new Research[requiredList.size()]);
		}
	}
	
	private long userID;
	private String username;
	
	private Civ civ;
	private List<Town> townList;
	private List<Unit> nonCombatantList;
	private List<Unit> combatantList;
	
	// A list of research that the civilization has
	private List<Research> haveResearchList;
	
	// A list of research that the civilization does not have
	private List<Research> noResearchList;
	
	// Current Research it is researching
	private Research cResearch;
	
	// Num of turns left
	private int rTurnsLeft;
	
	// Total Gold
	private int tGold;
	
	private boolean isAlive;
	
	public Civilization(Civ civ, String username, long userID){
		this.civ = civ;
		this.userID = userID;
		this.username = username;
		
		townList = new ArrayList<Town>();
		nonCombatantList = new ArrayList<Unit>();
		combatantList = new ArrayList<Unit>();
		haveResearchList = new ArrayList<Research>();
		noResearchList = new ArrayList<Research>();
		
		for(Research r : Research.values()){
			noResearchList.add(r);
		}
		
		for(Research r : civ.getStartResearchList()){
			addResearch(r);
		}
		
		isAlive = true;
	}
	
	public void setAlive(boolean alive){
		isAlive = alive;
	}
	
	public void setResearch(Research r){
		cResearch = r;
	}
	
	public void incrementResearch(){
		rTurnsLeft++;
	}
	
	public void clearResearch(){
		cResearch = null;
		rTurnsLeft = 0;
	}
	
	public void addResearch(Research r){
		if(!haveResearchList.contains(r)){
			haveResearchList.add(r);
			noResearchList.remove(r);
		}
	}
	
	public void setTotalGold(int gold){
		tGold = gold;
	}
	
	public void addNonCombatant(Unit unit){
		nonCombatantList.add(unit);
	}
	
	public void addCombatant(Unit unit){
		combatantList.add(unit);
	}
	
	public void addTown(Town town){
		townList.add(town);
	}
	
	public void removeNonCombatant(long id){
		int index = 0;
		for(int i = 0; i < nonCombatantList.size(); i++){
			if(nonCombatantList.get(i).getID() == id){
				index = i;
				break;
			}
		}
		
		removeNonCombatant(index);
	}
	
	public void removeNonCombatant(int index){
		nonCombatantList.remove(index);
	}
	
	public void removeCombatant(long id){
		int index = 0;
		for(int i = 0; i < combatantList.size(); i++){
			if(combatantList.get(i).getID() == id){
				index = i;
				break;
			}
		}
		
		removeCombatant(index);
	}
	
	public void removeCombatant(int index){
		combatantList.remove(index);
	}
	
	public void removeTown(int index){
		townList.remove(index);
	}
	
	public void removeTown(Town town){
		townList.remove(town);
	}
	
	public boolean containsResearch(Research r){
		return haveResearchList.contains(r);
	}
	
	public int getRTurnsLeft(){
		return rTurnsLeft;
	}
	
	public boolean isAlive(){
		return isAlive;
	}
	
	public Research getCurrentResearch(){
		return cResearch;
	}
	
	public Research[] getHaveResearchList(){
		return haveResearchList.toArray(new Research[haveResearchList.size()]);
	}
	
	public Town[] getTownList(){
		return townList.toArray(new Town[townList.size()]);
	}
	
	public Unit[] getCombatantList(){
		return combatantList.toArray(new Unit[combatantList.size()]);
	}
	
	public Unit[] getNonCombatantList(){
		return nonCombatantList.toArray(new Unit[nonCombatantList.size()]);
	}
	
	public Unit getCombatantList(long id){
		for(Unit u : combatantList){
			if(u.getID() == id){
				return u;
			}
		}
		
		return null;
	}
	
	public Unit getNonCombatantList(long id){
		for(Unit u : nonCombatantList){
			if(u.getID() == id){
				return u;
			}
		}
		
		return null;
	}
	
	public int getTotalGold(){
		return tGold;
	}
	
	public String getName(){
		return civ.getName();
	}
	
	public long getUserID(){
		return userID;
	}
	
	public String getUsername(){
		return username;
	}
	
	public int getTotalIncome(){
		int total = 0;
		for(Town t : townList){
			total += t.getCurrentIncome();
		}
		return total;
	}
	
	public int getTotalHappiness(){
		int total = 0;
		for(Town town : townList){
			total += town.getCurrentHappiness();
		}
		return total;
	}
	
	public CivAbility[] getAbilityList(){
		return civ.getAbilityList();
	}
	
}
