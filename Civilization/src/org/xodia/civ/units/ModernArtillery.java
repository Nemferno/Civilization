package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class ModernArtillery extends Unit{

	public ModernArtillery(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Modern Artillery");
		setMaxHealth(30);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.ModernArtillery);
		setStrength(0);
		setRangeStrength(28);
		setSight(4);
		setUnitImage(Resource.ModernArtilleryUnit);
	}

}
