package org.xodia.civ.units;

import org.xodia.civ.Resource;

public class Frigate extends Unit{

	public Frigate(long playerID, long id, int x, int y){
		super(playerID, id, x, y);
		
		setName("Frigate");
		setMaxHealth(22);
		setCurrentHealth(getMaxHealth());
		setCombatType(CombatType.Range);
		setUnitType(UnitType.DeepWater);
		setMaxMovePoints(6);
		setCurrentMovePoints(getMaxMovePoints());
		setUnitID(ID.Frigate);
		setStrength(0);
		setRangeStrength(14);
		setSight(6);
		setUnitImage(Resource.FrigateUnit);
	}
	
}
