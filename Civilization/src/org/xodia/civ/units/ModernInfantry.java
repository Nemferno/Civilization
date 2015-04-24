package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class ModernInfantry extends Unit{

	public ModernInfantry(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Modern Infantry");
		setMaxHealth(30);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Melee);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.ModernInfantry);
		setStrength(0);
		setRangeStrength(18);
		setSight(4);
		setUnitImage(Resource.ModernInfantryUnit);
	}

}
