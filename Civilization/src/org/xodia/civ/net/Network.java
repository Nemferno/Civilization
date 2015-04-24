package org.xodia.civ.net;

import org.xodia.civ.civs.Civilization.Civ;
import org.xodia.civ.civs.Civilization.Civ.CivAbility;
import org.xodia.civ.civs.Civilization.Civ.CivPackage;
import org.xodia.civ.civs.Civilization.Research;
import org.xodia.civ.map.Map.MapSize;
import org.xodia.civ.map.MapGenerator.MapSeed;
import org.xodia.civ.net.GameServer.Age;
import org.xodia.civ.units.Unit;
import org.xodia.civ.units.Unit.CombatType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	public static final int DEFAULT_PORT = 9498;
	
	public static void register(EndPoint point){
		Kryo kryo = point.getKryo();
		
		kryo.register(String[].class);
		kryo.register(int[].class);
		kryo.register(PlayerPacket[].class);
		
		kryo.register(MapSeed.class);
		kryo.register(MapSize.class);
		kryo.register(PlayerPacket.class);
		kryo.register(Civ.class);
		kryo.register(Unit.ID.class);
		kryo.register(Research.class);
		kryo.register(CombatType.class);
		kryo.register(Age.class);
		kryo.register(CivAbility.class);
		kryo.register(CivPackage.class);
		
		kryo.register(Client_Data.class);
		kryo.register(Client_Join.class);
		kryo.register(Client_Leave.class);
		kryo.register(Client_GameSettings.class);
		kryo.register(Client_Start.class);
		kryo.register(Client_Mouse_Location.class);
		kryo.register(Client_Change_Civ.class);
		kryo.register(Client_End_Turn.class);
		kryo.register(Client_Move_Unit.class);
		kryo.register(Client_Create_Town.class);
		kryo.register(Client_Set_Town_Production.class);
		kryo.register(Client_Attack_Unit.class);
		kryo.register(Client_Set_Research.class);
		kryo.register(Client_Siege_Town.class);
		kryo.register(Client_Cancel_Siege.class);
		kryo.register(Client_Raze_Town.class);
		kryo.register(Client_Destroy_Alliance.class);
		kryo.register(Client_Peace.class);
		kryo.register(Client_Confirmed_Winner.class);
		kryo.register(Client_Text_PubMessage.class);
		kryo.register(Client_Text_WhisperMessage.class);
		
		kryo.register(Server_Set_Research.class);
		kryo.register(Server_Text_PubMessage.class);
		kryo.register(Server_Join_Failed.class);
		kryo.register(Server_Join_Success.class);
		kryo.register(Server_Update_Lobby.class);
		kryo.register(Server_Game_Start.class);
		kryo.register(Server_Update_Game.class);
		kryo.register(Server_Create_Unit.class);
		kryo.register(Server_Client_Turn.class);
		kryo.register(Server_Client_End_Turn.class);
		kryo.register(Server_Update_Unit.class);
		kryo.register(Server_Add_Town.class);
		kryo.register(Server_Remove_Unit.class);
		kryo.register(Server_Add_Building_To_Town.class);
		kryo.register(Server_Add_Town_Production.class);
		kryo.register(Server_Update_Town_Production.class);
		kryo.register(Server_Update_Civ.class);
		kryo.register(Server_Game_Start_Failed.class);
		kryo.register(Server_Add_Research.class);
		kryo.register(Server_Update_Research.class);
		kryo.register(Server_Town_Add_Border.class);
		kryo.register(Server_Town_Add_Borders.class);
		kryo.register(Server_Update_Town.class);
		kryo.register(Server_Siege_Unit_Cancel.class);
		kryo.register(Server_Game_Lost.class);
		kryo.register(Server_Game_Won.class);
		kryo.register(Server_Remove_Town.class);
		kryo.register(Server_New_Turn.class);
		kryo.register(Server_Text_WhisperMessage.class);
	}
	
	///////////////////////////////////////
	// CLIENT MESSAGES
	///////////////////////////////////////
	/**
	 * This client message is sent when the client
	 * connects to the server. This is required so that the
	 * Server can obtain the data and then save it into its
	 * list for later uses...
	 */
	public static class Client_Data {
		public String username;
	}
	/**
	 * Sends a message to the server in order to join its lobby! if it is rejected, then
	 * it will kick the person out and remove it from its list. 
	 */
	public static class Client_Join {}
	/**
	 * Leaves the game manually!
	 */
	public static class Client_Leave {}
	/**
	 * Changes game settings
	 */
	public static class Client_GameSettings {
		
	}
	/**
	 * During Lobby, if the player wants to change his/her
	 * civ, then it sends this message to the server
	 */
	public static class Client_Change_Civ {
		public String civName;
		public CivAbility abil;
		public CivPackage pack;
	}
	/**
	 * Start the game!
	 */
	public static class Client_Start {
		
	}
	/**
	 * Sends the player's mouse coordinates
	 */
	public static class Client_Mouse_Location {
		public float mousex;
		public float mousey;
	}
	/**
	 * Sends the server that the player ended his/her turn
	 */
	public static class Client_End_Turn {}
	
	/**
	 * Sends the server where the unit wants to go!
	 */
	public static class Client_Move_Unit {
		public long playerID;
		public long id;
		public boolean isCombatant;
		public int tx;
		public int ty;
		public int totalCost;
	}
	
	/**
	 * Sends the server a message to create a town
	 */
	public static class Client_Create_Town {
		public String tName;
		public long playerID;
		public long unitID;
		public boolean isCombatant;
		public int x;
		public int y;
	}
	
	/**
	 * Sets the current town's production
	 */
	public static class Client_Set_Town_Production {
		public long playerID;
		public long townID;
		public int prodID;
		public boolean isBuilding;
	}
	
	public static class Client_Cancel_Town_Production {
		public long playerID;
		public long townID;
	}
	
	/**
	 * Tells the Server to initiate fight!
	 */
	public static class Client_Attack_Unit {
		public long playerID;
		// The one being attacked
		public long attPlayerID;
		public long unitID;
		// The one being attack
		public long attUnitID;
		public boolean isAttCombatant;
		public boolean isCombatant;
		// The previous step of the attacking tile
		public int prevX;
		public int prevY;
		// The type of attack
		public CombatType combat;
	}
	
	/**
	 * Tells the server to set its research production
	 */
	public static class Client_Set_Research {
		public long playerID;
		public Research research;
	}
	
	public static class Client_Siege_Town {
		public long playerID;
		public long unitID;
		public int prevX;
		public int prevY;
		
		public long enemyID;
		public long eTownID;
	}
	
	public static class Client_Cancel_Siege {
		public long playerID;
		public long unitID;
		
		public long enemyID;
		public long eTownID;
	}
	
	public static class Client_Raze_Town {
		public long playerID;
		public long unitID;
		
		public long enemyID;
		public long eTownID;
	}
	
	public static class Client_Destroy_Alliance {
		public long playerID;
		public long otherID;
	}
	
	public static class Client_Peace {
		public long playerID;
		public long enemyID;
	}
	
	public static class Client_Text_PubMessage {
		public String name;
		public String text;
	}
	
	public static class Client_Text_WhisperMessage {
		public String fromName;
		public String toName;
		public String text;
		public long to;
	}
	
	public static class Client_Confirmed_Winner {}
	
	//////////////////////////////////////////
	// SERVER MESSAGES
	//////////////////////////////////////////
	/**
	 * The server declines the client and gives the client a reason
	 */
	public static class Server_Join_Failed {
		public String reason;
	}
	
	/**
	 * The server accepts the client joining
	 */
	public static class Server_Join_Success {
		public boolean isHost;
		public long id;
	}
	/**
	 * The Server will keep the client up to date by sending new messages
	 * of the lobby
	 */
	public static class Server_Update_Lobby {
		public String[] players;
	}
	/**
	 * Tells the client to start the game
	 * and sends in the game data
	 */
	public static class Server_Game_Start {
		public MapSeed seed;
	}
	
	/**
	 * Tells the clients that they are unable to start it due
	 * to some issues with either the players or game settings
	 */
	public static class Server_Game_Start_Failed {
		public String message;
	}
	
	/**
	 * Sends back the new updated list
	 */
	public static class Server_Update_Game {
		public PlayerPacket[] packets;
	}
	/**
	 * Tells the client to make a unit with the appropriate id
	 */
	public static class Server_Create_Unit {
		public long playerID;
		public long id;
		public Unit.ID unitID;
		public int x;
		public int y;
	}
	/**
	 * Tells the clients to update a unit
	 * TODO Add some more information
	 */
	public static class Server_Update_Unit {
		public long playerID;
		public long id;
		public long townSiegedID;
		public long townSiegedPlayerID;
		public int x;
		public int y;
		public int currentMP;
		public int currentHealth;
		public boolean isCombatant;
		public boolean isAction;
		public boolean isSieging;
	}
	
	/**
	 * Tells the clients to add a town at a specific location
	 */
	public static class Server_Add_Town {
		public long playerID;
		public long id; // New ID of Town
		public String name;
		public boolean isCapital;
		public int x;
		public int y;
	}
	
	public static class Server_Remove_Town {
		public long playerID;
		public long id;
	}
	
	/**
	 * Tells the clients to remove a unit from their managers
	 */
	public static class Server_Remove_Unit {
		public long playerID;
		public long id;
		public boolean isCombatant;
	}
	
	/**
	 * Tells the clients how many turns have gone by for building the production
	 */
	public static class Server_Update_Town_Production {
		public long playerID;
		public long townID;
	}
	
	/**
	 * Different to Server_Add_Town_Production
	 * The role of this message is to tell the clients
	 * To add it in the "have" list
	 */
	public static class Server_Add_Building_To_Town {
		public long playerID;
		public long townID;
		public int prodID;
	}
	
	/**
	 * Tells the clients to add the production to the town!
	 */
	public static class Server_Add_Town_Production {
		public long playerID;
		public long townID;
		public int prodID;
		public boolean isBuilding;
	}
	
	/**
	 * Tells the clients to update its civManagers
	 */
	public static class Server_Update_Civ {
		public long playerID;
		public int totalGold;
	}
	
	/**
	 * Tells the clients to update its research
	 */
	public static class Server_Update_Research {
		public long playerID;
	}
	
	/**
	 * Tells the clients to add what it's researching
	 * and 
	 */
	public static class Server_Add_Research {
		public long playerID;
	}
	
	/**
	 * Tells the clients to set their research
	 */
	public static class Server_Set_Research {
		public long playerID;
		public Research research;
	}
	
	/**
	 * Sends to the clients one location of border
	 */
	public static class Server_Town_Add_Border {
		public int x, y;
		public long playerID;
		public long townID;
	}
	
	/**
	 * Sends to the clients multiple locations of borders instead of one
	 */
	public static class Server_Town_Add_Borders {
		public int[] xs;
		public int[] ys;
		public long playerID;
		public long townID;
	}
	
	/**
	 * Server sends to all the clients the "updated" information of the town
	 * This prevents more more messages of town stats to be updated separately, but
	 * as a whole
	 */
	public static class Server_Update_Town {
		public long playerID;
		public long townID;
		public int currentGrowth;
		public int maxGrowth;
		public int currentIncome;
		public int currentDefense;
		public boolean isSieged;
		public boolean isCapital;
	}

	/**
	 * Cancels the sieging of the unit...
	 */
	public static class Server_Siege_Unit_Cancel {
		public long playerID;
		public long unitID;
	}
	
	public static class Server_Game_Lost {}
	
	public static class Server_Game_Won {}
	
	public static class Server_Display_Turn {
		public int turn;
	}
	
	public static class Server_Text_PubMessage {
		public String name;
		public String text;
	}
	
	public static class Server_Text_WhisperMessage {
		public String from;
		public String to;
		public String text;
		public long fromID;
		public long toID;
	}
	
	public static class Server_New_Turn {
		public int currentTurn;
		public Age currentAge;
	}
	
	/**
	 * Tells the client that it is his/her turn
	 */
	public static class Server_Client_Turn {}
	/**
	 * Tells the client that it is not longer his/her turn
	 */
	public static class Server_Client_End_Turn{}
}
