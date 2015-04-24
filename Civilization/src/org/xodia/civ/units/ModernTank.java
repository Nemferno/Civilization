package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class ModernTank extends Unit{

	public ModernTank(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Modern Tank");
		setMaxHealth(34);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Melee);
		setUnitType(UnitType.Land);
		setMaxMovePoints(4);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.ModernTank);
		setStrength(16);
		setRangeStrength(0);
		setSight(4);
		setUnitImage(Resource.ModernTankUnit);
	}

}
