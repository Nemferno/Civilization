package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Artillery extends Unit{

	public Artillery(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Artillery");
		setMaxHealth(10);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Artillery);
		setStrength(0);
		setRangeStrength(24);
		setSight(4);
		setUnitImage(Resource.ArtilleryUnit);
	}

}
