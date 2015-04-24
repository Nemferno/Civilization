package org.xodia.civ.net;

import org.xodia.civ.civs.Civilization.Civ.CivAbility;
import org.xodia.civ.civs.Civilization.Civ.CivPackage;

/**
 * Contains all the data that a player has.
 * The data is:
 * 
 * 
 * @author Jasper Bae
 *
 */
public class PlayerPacket {

	public long id;
	public long version;
	
	public String username;
	
	public String civName;
	public CivPackage civPackage;
	public CivAbility civAbility;
	
	public float mouseX;
	public float mouseY;
	
	public PlayerPacket(){
		this(-1);
	}
	
	public PlayerPacket(long id){
		this.id = id;
		
		version = 0;
	}
	
}
