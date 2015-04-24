package org.xodia.civ.civs;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.xodia.civ.units.Town;

/**
 * 
 * Use this for Fog of War. This will display the status of the Civilization last seen by the player.
 * 
 * @author Jasper Bae
 *
 */
public class TempCivilization {

	private CivManager civ;
	
	private List<Town> townList;
	
	public TempCivilization(CivManager civ){
		this.civ = civ;
		
		townList = new ArrayList<Town>();
	}
	
	public void update(Town t){
		if(contains(t)){
			Town stat = get(t);
			stat.setName(t.getName());
			stat.setCapital(t.isCapital());
			stat.setCurrentDefense(t.getCurrentDefense());
			stat.setTownSize(t.getSize());
		}else{
			Town stat = new Town(t.getPlayerID(), t.getID(), t.getX(), t.getY());
			stat.setName(t.getName());
			stat.setCapital(t.isCapital());
			stat.setCurrentDefense(t.getCurrentDefense());
			stat.setTownSize(t.getSize());
			townList.add(stat);
		}
	}
	
	public void render(Town t, Graphics g){
		Town town = get(t);
		if(town != null){
			town.render(g);
		}
	}
	
	private boolean contains(Town t){
		for(Town t2 : townList){
			if(t2.getID() == t.getID()){
				return true;
			}
		}
		
		return false;
	}
	
	private Town get(Town t){
		for(Town t2 : townList){
			if(t2.getID() == t.getID()){
				return t2;
			}
		}
		
		return null;
	}
	
	public CivManager getCivManager(){
		return civ;
	}
	
}
