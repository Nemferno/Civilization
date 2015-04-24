package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Trebuchet extends Unit{

	public Trebuchet(long playerID, long id, int x, int y){
		super(playerID, id, x, y);
		
		setName("Trebuchet");
		setMaxHealth(10);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Trebuchet);
		setStrength(0);
		setRangeStrength(18);
		setSight(4);
		setUnitImage(Resource.TrebuchetUnit);
	}
	
}
