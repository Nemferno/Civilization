package org.xodia.civ.units;

import org.xodia.civ.Resource;


public class Warrior extends Unit{

	public Warrior(long playerID, long id, int x, int y) {
		super(playerID, id, x, y);
		
		setName("Warrior");
		setMaxHealth(25);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Melee);
		setUnitType(UnitType.Land);
		setMaxMovePoints(20);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Warrior);
		setStrength(5);
		setSight(3);
		setUnitImage(Resource.WarriorUnit);
	}

}
