package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Cannon extends Unit{

	public Cannon(long playerID, long id, int x, int y){
		super(playerID, id, x, y);
		
		setName("Cannon");
		setMaxHealth(16);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Cannon);
		setStrength(0);
		setRangeStrength(18);
		setSight(4);
		setUnitImage(Resource.CannonUnit);
	}
	
}
