package org.xodia.civ.units;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.xodia.civ.map.Tile;
import org.xodia.civ.units.ProductionManager.Building;

public class Town extends Unit{

	private final int 	DEFAULT_DEFENSE = 10,
						DEFAULT_POP = 1,
						DEFAULT_INCOME = 50,
						DEFAULT_HAPPINESS = 10,
						DEFAULT_FOOD = 2,
						DEFAULT_START_GROWTH = 20;
	
	private final float DEFAULT_TAX = 0.5f;
	
	// Limits the amount of happiness a town can produce
	private final int 	MAX_HAPPINESS = 15;
	
	public static enum TownSize {
		Camp(25), Village(50), Town(75), Suburb(100), City(125);
		
		private int defaultIncome;
		
		TownSize(int income){
			defaultIncome = income;
		}
		
		public int getDefaultIncome(){
			return defaultIncome;
		}
		
	}
	
	// Manages the production of what the town is producing...
	private ProductionManager pManager;
	
	// This is a list for what the town currently has
	private List<Building> haveBuildingList;
	
	// This is a list for what the town does not have and could
	// make
	private List<Building> couldBuildingList;
	
	// What the current size of the city is or how
	// 'modernized' it is
	private TownSize currentTownSize;
	
	// The amount of defense or health the town has
	private int currentDefense;
	private int maxDefense;
	
	// Current Income the Town Produces
	private int currentIncome;
	
	// The Current Matenience Costs
	private int currentMCost;
	
	// The Current Happiness the town has
	private int currentHappiness;
	
	// The current population. tells how much
	// the town has grown
	private int currentPopulation;
	
	// Increases per turn depending on the different of
	// the food. When reached the pop will grow as well as
	// its defense. It's like a level up
	private int currentGrowth;
	private int maxGrowth;
	
	// The amount of food. The difference will
	// determine whether the town is starving, stagnant,
	// or growing!
	private int currentFood;
	
	// Decreases the amount of turns you have to produce
	private int currentProduction;
	
	// The Current Tax % of the Town
	private float taxPercentage;
	
	// Is this the capital of the civilization?
	private boolean isCapital;
	
	// The name of the city
	private String name;
	
	// A list of the tiles that the town has...
	private List<Tile> borderList;
	
	// A List of the tiles that the town doesn't have
	private List<Tile> noBorderList;
	
	private int minBorderX, maxBorderX;
	private int minBorderY, maxBorderY;
	
	// If the current is at the max, it will assign a border out of the non borders
	// and add it as its border. resets its stats and increase max
	private int currentBorderGrowth;
	private int maxBorderGrowth;
	
	private List<Unit> siegedList;
	private boolean isSieged;
	
	public Town(long playerID, long id, int x, int y){
		super(playerID, id, x, y);
		
		// Set the Default Items
		maxDefense = DEFAULT_DEFENSE;
		currentDefense = maxDefense;
		currentIncome = DEFAULT_INCOME;
		currentTownSize = TownSize.Camp;
		currentHappiness = DEFAULT_HAPPINESS;
		taxPercentage = DEFAULT_TAX;
		maxGrowth = DEFAULT_START_GROWTH;
		currentGrowth = 0;
		currentPopulation = DEFAULT_POP;
		currentFood = DEFAULT_FOOD;
		currentMCost = 0;
		maxBorderGrowth = 5;
		currentBorderGrowth = 0;
		setSight(6);
		
		pManager = new ProductionManager(this);
		
		borderList = new ArrayList<Tile>();
		noBorderList = new ArrayList<Tile>();
		haveBuildingList = new ArrayList<Building>();
		couldBuildingList = new ArrayList<Building>();
		siegedList = new ArrayList<Unit>();
		
		for(Building b : Building.values()){
			couldBuildingList.add(b);
		}
		
		minBorderX = -1;
		maxBorderX = -1;
		minBorderY = -1;
		maxBorderY = -1;
	}
	
	/**
	 * Adds up all the stats that the town has (buildings also)
	 */
	public void calculateStats(){
		// Calculate Building
		int cFood = DEFAULT_FOOD;
		int cProd = 0;
		int cMatenience = 0;
		int cMoney = 0;
		
		for(Building b : haveBuildingList){
			switch(b){
			case Farm:
				cFood += 4;
				cMatenience += 5;
				break;
			case Granary:
				cFood += 2;
				cMatenience += 5;
				break;
			case Stable:
				cProd += 1;
				cMatenience += 5;
				break;
			case Pasture:
				cFood += 1;
				cProd += 1;
				cMatenience += 5;
				break;
			case Plantations:
				cProd += 2;
				cMatenience += 5;
				break;
			case Windmill:
				cProd += 1;
				cMatenience += 5;
				break;
			case Watermill:
				cProd += 1;
				cMatenience += 5;
				break;
			case Blacksmith:
				cProd += 1;
				cMatenience += 5;
				break;
			case Factory:
				cProd += 1;
				cMatenience += 5;
				break;
			case University:
				cMoney += 10;
				break;
			case Market:
				cMoney += currentIncome * 0.25f;
				break;
			case Bank:
				cMoney += currentIncome * 0.5f;
				break;
			}
		}
		
		// Set the Current Production Cost
		currentProduction = cProd;
		
		// Set the Current Matenience Cost
		currentMCost = cMatenience;
		
		// Calculate how much income it earns
		currentIncome = currentTownSize.getDefaultIncome() + cMoney;
		currentIncome -= currentMCost;
		currentIncome += (currentTownSize.getDefaultIncome() * taxPercentage);
		
		// Calculate Happiness
		
		
		// Calculate Population Growth
		int gDiff = cFood - currentPopulation;
		// TODO Put in Status of Town (Stagnant, Hunger, Surplus)
		if(gDiff < 0){
			// Decrease
			currentGrowth -= gDiff;
			
			if(currentGrowth <= 0){
				currentGrowth = 0;
				
				if(currentPopulation > 1){
					currentPopulation--;
					maxGrowth -= 5;
					currentGrowth = maxGrowth;
				}
			}
		}else if(gDiff == 0){
			
		}else{
			// Increase
			currentGrowth += gDiff;
			
			if(currentGrowth >= maxGrowth){
				currentPopulation++;
				currentGrowth = maxGrowth - currentGrowth;
				maxGrowth += 5;
			}
		}
		
		currentFood = cFood;
	}
	
	public void setSieged(boolean siege){
		isSieged = siege;
	}
	
	public void addSieged(Unit u){
		siegedList.add(u);
	}
	
	public void removeSieged(Unit u){
		siegedList.remove(u);
	}
	
	public void clearSieged(){
		siegedList.clear();
	}
	
	public void addBorder(Tile t, long id){
		if(t.getX() < minBorderX){
			minBorderX = t.getX();
		}else if(minBorderX == -1){
			minBorderX = t.getX();
		}
		
		if(t.getX() > maxBorderX){
			maxBorderX = t.getX();
		}else if(maxBorderX == -1){
			maxBorderX = t.getX();
		}
		
		if(t.getY() < minBorderY){
			minBorderY = t.getY();
		}else if(minBorderY == -1){
			minBorderY = t.getY();
		}
		
		if(t.getY() > maxBorderY){
			maxBorderY = t.getY();
		}else if(maxBorderX == -1){
			maxBorderY = t.getY();
		}
		
		t.setCountryOwned(id);
		borderList.add(t);
	}
	
	public void removeBorder(Tile t){
		t.setCountryOwned(-1);
		borderList.remove(t);
	}
	
	public void addNonBorder(Tile t){
		if(!noBorderList.contains(t))
			noBorderList.add(t);
	}
	
	public void removeNonBorder(Tile t){
		noBorderList.remove(t);
	}
	
	public void setCurrentIncome(int income){
		this.currentIncome = income;
	}
	
	public void setCurrentGrowth(int growth){
		currentGrowth = growth;
	}
	
	public void setMaxGrowth(int max){
		maxGrowth = max;
	}
	
	public void setCurrentBorderGrowth(int growth){
		currentBorderGrowth = growth;
	}
	
	public void setMaxBorderGrowth(int max){
		maxBorderGrowth = max;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setCapital(boolean capital){
		isCapital = capital;
	}
	
	public void setTownSize(TownSize size){
		currentTownSize = size;
	}
	
	public void setCurrentDefense(int defense){
		currentDefense = defense;
	}
	
	public void addBuildingToHave(Building b){
		if(!haveBuildingList.contains(b)){
			haveBuildingList.add(b);
			couldBuildingList.remove(b);
		}
	}
	
	public ProductionManager getProductionManager(){
		return pManager;
	}
	
	public TownSize getSize(){
		return currentTownSize;
	}
	
	public String getName(){
		return name;
	}
	
	public Tile[] getBorderList(){
		return borderList.toArray(new Tile[borderList.size()]);
	}
	
	public Tile[] getNonBorderList(){
		return noBorderList.toArray(new Tile[noBorderList.size()]);
	}
	
	public Unit[] getSiegedList(){
		return siegedList.toArray(new Unit[siegedList.size()]);
	}
	
	public boolean isSieged(){
		return isSieged;
	}
	
	public int getMinBorderX(){
		return minBorderX;
	}
	
	public int getMaxBorderX(){
		return maxBorderX;
	}
	
	public int getMinBorderY(){
		return minBorderY;
	}
	
	public int getMaxBorderY(){
		return maxBorderY;
	}
	
	public int getCurrentBorderGrowth(){
		return currentBorderGrowth;
	}
	
	public int getMaxBorderGrowth(){
		return maxBorderGrowth;
	}
	
	public int getNonBorderSize(){
		return noBorderList.size();
	}
	
	public int getMaxDefense(){
		return maxDefense;
	}
	
	public int getProduction(){
		return currentProduction;
	}
	
	public int getCurrentDefense(){
		return currentDefense;
	}
	
	public int getCurrentIncome(){
		return currentIncome;
	}
	
	public int getCurrentMatenienceCost(){
		return currentMCost;
	}
	
	public int getCurrentHappiness(){
		return currentHappiness;
	}
	
	public int getCurrentPopulation(){
		return currentPopulation;
	}
	
	public int getMaxGrowth(){
		return maxGrowth;
	}
	
	public int getCurrentGrowth(){
		return currentGrowth;
	}
	
	public int getCurrentFood(){
		return currentFood;
	}
	
	public float getTaxPercentage(){
		return taxPercentage;
	}
	
	public boolean isCapital(){
		return isCapital;
	}

	public void render(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(getX() * 32, getY() * 32, 32, 32);
		
		g.drawString(((isCapital) ? "(*) " : "") + name, getX() * 32, getY() * 32 - 15);
	}
}
