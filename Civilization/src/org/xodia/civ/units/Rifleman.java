package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Rifleman extends Unit{

	public Rifleman(long playerID, long id, int x, int y){
		super(playerID, id, x, y);
		
		setName("Rifleman");
		setMaxHealth(22);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Rifleman);
		setStrength(0);
		setRangeStrength(12);
		setSight(4);
		setUnitImage(Resource.RiflemanUnit);
	}
	
}
