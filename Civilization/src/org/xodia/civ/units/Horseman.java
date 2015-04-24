package org.xodia.civ.units;

import org.xodia.civ.Resource;


public class Horseman extends Unit{

	public Horseman(long playerID, long id, int x, int y){
		super(playerID, id, x, y);

		setName("Horseman");
		setMaxHealth(25);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Melee);
		setUnitType(UnitType.Land);
		setMaxMovePoints(4);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Horseman);
		setStrength(7);
		setSight(3);
		setUnitImage(Resource.HorsemanUnit);
	}
	
}
