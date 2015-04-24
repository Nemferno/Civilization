package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Tank extends Unit{

	public Tank(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Tank");
		setMaxHealth(28);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Melee);
		setUnitType(UnitType.Land);
		setMaxMovePoints(4);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Tank);
		setStrength(12);
		setRangeStrength(0);
		setSight(4);
		setUnitImage(Resource.TankUnit);
	}
	
}
