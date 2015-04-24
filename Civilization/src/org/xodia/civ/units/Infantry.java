package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Infantry extends Unit{

	public Infantry(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Infantry");
		setMaxHealth(24);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Infantry);
		setStrength(0);
		setRangeStrength(14);
		setSight(4);
		setUnitImage(Resource.InfantryUnit);
	}

}
