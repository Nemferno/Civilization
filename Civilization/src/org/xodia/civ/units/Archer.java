package org.xodia.civ.units;

import org.xodia.civ.Resource;


public class Archer extends Unit{

	public Archer(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Archer");
		setMaxHealth(15);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.Land);
		setMaxMovePoints(4);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Archer);
		setStrength(3);
		setRangeStrength(8);
		setSight(5);
		setUnitImage(Resource.ArcherUnit);
	}

}
