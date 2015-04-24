package org.xodia.civ.civs;

import org.xodia.civ.civs.Civilization.Civ.CivAbility;
import org.xodia.civ.civs.Civilization.Research;
import org.xodia.civ.units.Town;
import org.xodia.civ.units.Unit;

/**
 * 
 * A manager that manages your civilization. This is the only way the other units can
 * use the Civilization class is by going through this...
 * This is the only way to communicate objects and share data from civ to game
 * 
 * @author Jasper Bae
 *
 */
public class CivManager {
	
	private Civilization civ;
	
	public CivManager(Civilization civ){
		this.civ = civ;
	}
	
	public synchronized void setResearch(Research r){
		civ.setResearch(r);
	}
	
	public synchronized void incrementResearch(){
		civ.incrementResearch();
	}
	
	public synchronized void clearResearch(){
		civ.clearResearch();
	}
	
	public synchronized void addResearch(Research add){
		civ.addResearch(add);
	}
	
	public synchronized void setAlive(boolean alive){
		civ.setAlive(alive);
	}
	
	public synchronized void setTotalGold(int gold){
		civ.setTotalGold(gold);
	}
	
	public synchronized void addNonCombatant(Unit unit){
		civ.addNonCombatant(unit);
	}
	
	public synchronized void addCombatant(Unit unit){
		civ.addCombatant(unit);
	}
	
	public synchronized void addTown(Town town){
		civ.addTown(town);
	}
	
	public synchronized void removeNonCombatant(long id){
		civ.removeNonCombatant(id);
	}
	
	public synchronized void removeNonCombatant(int index){
		civ.removeNonCombatant(index);
	}
	
	public synchronized void removeCombatant(long id){
		civ.removeCombatant(id);
	}
	
	public synchronized void removeCombatant(int index){
		civ.removeCombatant(index);
	}
	
	public synchronized void removeTown(int index){
		civ.removeTown(index);
	}
	
	public synchronized void removeTown(Town town){
		civ.removeTown(town);
	}
	
	public synchronized boolean containsResearch(Research r){
		return civ.containsResearch(r);
	}
	
	public synchronized Unit getNonCombatantList(long id){
		return civ.getNonCombatantList(id);
	}
	
	public synchronized Unit getCombatantList(long id){
		return civ.getCombatantList(id);
	}
	
	public synchronized Unit[] getNonCombatantList(){
		return civ.getNonCombatantList();
	}
	
	public synchronized Unit[] getCombatantList(){
		return civ.getCombatantList();
	}
	
	public synchronized Town[] getTownList(){
		return civ.getTownList();
	}
	
	public synchronized Town getTown(long id){
		for(Town t : civ.getTownList()){
			if(t.getID() == id){
				return t;
			}
		}
		
		return null;
	}
	
	public synchronized boolean isAlive(){
		return civ.isAlive();
	}
	
	public synchronized int getResearchTurnsLeft(){
		return civ.getRTurnsLeft();
	}
	
	public synchronized Research getCurrentResearch(){
		return civ.getCurrentResearch();
	}
	
	public synchronized Research[] getHaveResearchList(){
		return civ.getHaveResearchList();
	}
	
	public synchronized int getTotalGold(){
		return civ.getTotalGold();
	}
	
	public synchronized String getCivName(){
		return civ.getName();
	}
	
	public synchronized int getTotalHappiness(){
		return civ.getTotalHappiness();
	}
	
	public synchronized int getTotalIncome(){
		return civ.getTotalIncome();
	}
	
	public synchronized boolean hasCapital(){
		for(Town t : getTownList()){
			if(t.isCapital()){
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized CivAbility[] getAbilityList(){
		return civ.getAbilityList();
	}
	
}
