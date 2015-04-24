package org.xodia.civ.units;

public class UnitFactory {

	private static UnitFactory instance;
	
	private UnitFactory(){}
	
	public Unit createUnit(int x, int y, long id, long playerID, Unit.ID unitID){
		switch(unitID.getID()){
		case 0:
			// Settler
			return new Settler(playerID, id, x, y);
		case 1:
			return new Warrior(playerID, id, x, y);
		case 2:
			return new Archer(playerID, id, x, y);
		default:
			return null;
		}
	}
	
	public static UnitFactory newInstance(){
		if(instance == null){
			instance = new UnitFactory();
		}
		
		return instance;
	}
	
}
