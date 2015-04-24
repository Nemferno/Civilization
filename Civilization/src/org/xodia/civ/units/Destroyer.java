package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Destroyer extends Unit{

	public Destroyer(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Destroyer");
		setMaxHealth(20);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.DeepWater);
		setMaxMovePoints(6);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Destroyer);
		setStrength(0);
		setRangeStrength(20);
		setSight(6);
		setUnitImage(Resource.DestroyerUnit);
	}

}
