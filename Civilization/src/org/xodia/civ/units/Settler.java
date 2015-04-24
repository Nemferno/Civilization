package org.xodia.civ.units;

import org.xodia.civ.Resource;


public class Settler extends Unit{

	public Settler(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Settler");
		setMaxHealth(10);
		setCurrentHealth(getMaxHealth());
		addAbility(Ability.SettleTown);
		setCombatType(CombatType.NonCombatant);
		setUnitType(UnitType.Land);
		setMaxMovePoints(2);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Settler);
		setSight(3);
		setStrength(0);
		setUnitImage(Resource.SettlerUnit);
	}

}
