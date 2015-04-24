package org.xodia.civ.units;

import org.xodia.civ.civs.CivManager;
import org.xodia.civ.civs.Civilization.Research;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.units.Unit.ID;

/**
 * Manages production for a town...
 * 
 * @author Jasper Bae
 *
 */
public class ProductionManager {
	
	// What it is currently building
	private Production production;
	
	// Is what the town is producing a building?
	private boolean isBuilding;
	
	// The amount of turns it is on
	private int currentTurn;
	
	public ProductionManager(Town t){
		
	}
	
	public void setProduction(Production production){
		if(production instanceof Building){
			isBuilding = true;
		}else{
			isBuilding = false;
		}
		
		this.production = production;
		currentTurn = 0;
	}
	
	public void incrementProductionTurn(){
		currentTurn++;
	}
	
	public int getTurnsLeft(){
		if(production != null)
			return production.getTurns() - currentTurn;
		else
			return -1;
	}
	
	public boolean isBuilding(){
		return isBuilding;
	}
	
	public Production getProduction(){
		return production;
	}
	
	public boolean isProductionFinished(){
		if(production != null && currentTurn >= production.getTurns()){
			return true;
		}else{
			return false;
		}
	}
	
	public static Production getProductionFrom(int id, boolean isBuilding){
		if(isBuilding){
			for(Building b : Building.values()){
				if(b.getID() == id){
					return b;
				}
			}
		}else{
			for(Unit u : Unit.values()){
				if(u.getIntID() == id){
					return u;
				}
			}
		}
		
		return null;
	}
	
	// Starter Base
	public static interface Production{
		
		int getTurns();
		String getDescription();
		Requirement[] getRequirements();
		
	}
	
	public static interface Requirement {
		
	}
	
	// TODO Civ must have certain requirements to produce buildings
	public static enum Building implements Production {
		
		Farm("Farm\nFood +4", 0, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Agriculture}) }), Granary("Granary\nFood +2", 1, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Agriculture}) }), Stable("Stable\nProduction +1", 2, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.AnimalHusbandry }) }), Pasture("Pasture\nFood +1\nProduction +1", 3, 5, new Requirement[]{  new ResearchRequirement(new Research[]{ Research.AnimalHusbandry }) }),
		University("University\nResearch 1 turn less", 4, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Mathematics }) }), Plantations("Plantations\nProduction +2", 5, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Calendar}) }), Market("Market\nGold +25%", 6, 5, new Requirement[]{  new ResearchRequirement(new Research[]{ Research.Currency}) }), Bank("Bank\nGold +50%", 7, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Banking }) }),
		Wall("Wall\nDefense +10", 8, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Engineering}) }), Windmill("Windmill\nProduction +1", 9, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Machinery}) }), Watermill("Watermill\nProduction +1", 10, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Machinery}), TownRequirement.TownIsShore }), Blacksmith("Blacksmit\nProduction +1", 11, 5, new Requirement[]{  new ResearchRequirement(new Research[]{ Research.MetalCasting}) }),
		Factory("Factory\nProduction +1", 12, 5, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Industrialization}) });
		
		private int turns;
		private int id;
		private Requirement[] reqList;
		private String description;
		
		Building(String description, int id, int turns, Requirement[] req){
			this.turns = turns;
			this.description = description;
			this.id = id;
			this.reqList = req;
		}
		
		public String getDescription(){
			return description;
		}
		
		public Requirement[] getRequirements(){
			return reqList;
		}
		
		public int getTurns(){
			return turns;
		}
		
		public int getID(){
			return id;
		}
		
	}
	
	// TODO Civ must have certain requirements to produce units
	public static enum Unit implements Production {
		
		Settler("Settler\nA unit that can settle a town.\n-Strength: 0\n-Move Points: 2", 5, ID.Settler, null, new Requirement[]{}), 
		ModernTank("Modern Tank\nStrongest tank meant for mid-late game engagements.\n-Strength: 16\n-Move Points: 4", 3, ID.ModernTank, null, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.ModernTech }) }), ModernInfantry("Modern Infantry\nStrongest all rounded unit for mid-late game.\n-Strength: 18\n-Move Points: 2", 3, ID.ModernInfantry, null, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.ModernTech }) }), ModernArtillery("Modern Artillery\nStrongest siege unit.\n-Strength: 28\n-Move Points: 2", 3, ID.ModernArtillery, null, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.ModernTech }) }),
		Tank("Tank\nStrong mid-late game unit for offensive purposes.\n-Strength: 12\n-Move Points: 4", 3, ID.Tank, ModernTank, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.ArmorPlating }) }), Infantry("Infantry\nUpgraded version of the rifleman. Packs more of a punch.\n-Strength: 14\n-Move Points: 2", 3, ID.Infantry, ModernInfantry, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.BreachLoading }) }), Artillery("Artillery\nMid-late game siege unit.\n-Strength: 24\n-Move Points: 2", 3, ID.Artillery, ModernArtillery, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.ArmorPlating }) }),
		Rifleman("Rifleman\nAll rounded range unit for mid-game.\n-Strength: 12\n-Move Points: 2", 3, ID.Rifleman, Infantry, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.GunPowder }) }), Cannon("Cannon\nDurable and accurate siege unit.\n-Strength: 18\n-Move Points: 2", 3, ID.Cannon, Artillery, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.GunPowder }) }),
		Warrior("Warrior\nEarly-game melee unit.\n-Strength: 8\n-Move Points: 2", 3, ID.Warrior, Rifleman, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Tools }) }), Archer("Archer\nEarly-game range unit that is great for offensive and defense.\n-Strength: 12\n-Move Points: 4", 3, ID.Archer, Rifleman, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Fletching }) }), Horseman("Horseman\nEarly-game unit that can flank with high mobility.\n-Strength: 6\n-Move Points: 6", 3, ID.Horseman, Rifleman, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.HorsebackRiding }) }), Trebuchet("Schooner\nEarly-game siege unit.\n-Strength: 18\n-Move Points: 2", 3, ID.Trebuchet, Cannon, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.IronWorks }) }),
		Destroyer("Destroyer\nLate game ship beating the Frigate!.\n-Strength: 20\n-Move Points: 6", 3, ID.Destroyer, null, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Optic }) }), Frigate("Frigate\nMid-game ship that can dominate the sea!\n-Strength: 14\n-Move Points: 6", 3, ID.Frigate, Destroyer, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Compass }) }), Schooner("Schooner\nEarly-game ship in the game.\n-Strength: 10\n-Move Points: 4", 3, ID.Schooner, Frigate, new Requirement[]{ new ResearchRequirement(new Research[]{ Research.Sailing }) });
		
		private int turns;
		private ID id;
		private Unit obsolete;
		private Requirement[] reqList;
		private String description;
		
		Unit(String description, int turns, ID id, Unit obsolete, Requirement[] reqList){
			this.description = description;
			this.turns = turns;
			this.id = id;
			this.reqList = reqList;
			this.obsolete = obsolete;
		}
		
		public String getDescription(){
			return description;
		}
		
		public Requirement[] getRequirements(){
			return reqList;
		}
		
		public boolean isObsolete(){
			if(obsolete != null){
				boolean hasIt = true;
				CivManager manager = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID());
				for(Requirement r : obsolete.getRequirements()){
					if(r instanceof ResearchRequirement){
						for(Research re : ((ResearchRequirement) r).researchList){
							if(!manager.containsResearch(re)){
								hasIt = false;
								break;
							}
						}
					}
					
					if(!hasIt){
						break;
					}
				}
				
				if(!hasIt){
					return false;
				}else{
					return true;
				}
			}
			
			return false;
		}
		
		public int getIntID(){
			return id.getID();
		}
		
		public ID getID(){
			return id;
		}
		
		public int getTurns(){
			return turns;
		}
		
	}
	
	public static class BuildingRequirement implements Requirement {
		
		private Building[] buildingList;
		
		BuildingRequirement(Building[] b){
			buildingList = b;
		}
		
		public Building[] getBuildingList(){
			return buildingList;
		}
		
	}
	
	public static class ResearchRequirement implements Requirement {
		
		private Research[] researchList;
		
		ResearchRequirement(Research[] r){
			researchList = r;
		}
		
		public Research[] getResearchList(){
			return researchList;
		}
		
	}
	
	public static enum CivRequirement implements Requirement {
		
	}
	
	public static enum TownRequirement implements Requirement {
		TownIsShore;
	}
	
}
