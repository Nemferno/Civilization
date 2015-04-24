package org.xodia.civ.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.xodia.civ.civs.CivManager;
import org.xodia.civ.civs.Civilization;
import org.xodia.civ.civs.Civilization.Civ;
import org.xodia.civ.civs.Civilization.Research;
import org.xodia.civ.map.CivSpawnManager;
import org.xodia.civ.map.Map;
import org.xodia.civ.map.Map.MapSize;
import org.xodia.civ.map.MapGenerator.MapSeed;
import org.xodia.civ.map.Tile;
import org.xodia.civ.net.Network.Client_Attack_Unit;
import org.xodia.civ.net.Network.Client_Cancel_Siege;
import org.xodia.civ.net.Network.Client_Change_Civ;
import org.xodia.civ.net.Network.Client_Confirmed_Winner;
import org.xodia.civ.net.Network.Client_Create_Town;
import org.xodia.civ.net.Network.Client_Data;
import org.xodia.civ.net.Network.Client_End_Turn;
import org.xodia.civ.net.Network.Client_GameSettings;
import org.xodia.civ.net.Network.Client_Join;
import org.xodia.civ.net.Network.Client_Leave;
import org.xodia.civ.net.Network.Client_Mouse_Location;
import org.xodia.civ.net.Network.Client_Move_Unit;
import org.xodia.civ.net.Network.Client_Raze_Town;
import org.xodia.civ.net.Network.Client_Set_Research;
import org.xodia.civ.net.Network.Client_Set_Town_Production;
import org.xodia.civ.net.Network.Client_Siege_Town;
import org.xodia.civ.net.Network.Client_Start;
import org.xodia.civ.net.Network.Client_Text_PubMessage;
import org.xodia.civ.net.Network.Client_Text_WhisperMessage;
import org.xodia.civ.net.Network.Server_Add_Building_To_Town;
import org.xodia.civ.net.Network.Server_Add_Research;
import org.xodia.civ.net.Network.Server_Add_Town;
import org.xodia.civ.net.Network.Server_Add_Town_Production;
import org.xodia.civ.net.Network.Server_Client_End_Turn;
import org.xodia.civ.net.Network.Server_Client_Turn;
import org.xodia.civ.net.Network.Server_Create_Unit;
import org.xodia.civ.net.Network.Server_Game_Lost;
import org.xodia.civ.net.Network.Server_Game_Start;
import org.xodia.civ.net.Network.Server_Game_Start_Failed;
import org.xodia.civ.net.Network.Server_Game_Won;
import org.xodia.civ.net.Network.Server_Join_Failed;
import org.xodia.civ.net.Network.Server_Join_Success;
import org.xodia.civ.net.Network.Server_New_Turn;
import org.xodia.civ.net.Network.Server_Remove_Town;
import org.xodia.civ.net.Network.Server_Remove_Unit;
import org.xodia.civ.net.Network.Server_Set_Research;
import org.xodia.civ.net.Network.Server_Siege_Unit_Cancel;
import org.xodia.civ.net.Network.Server_Text_PubMessage;
import org.xodia.civ.net.Network.Server_Text_WhisperMessage;
import org.xodia.civ.net.Network.Server_Town_Add_Border;
import org.xodia.civ.net.Network.Server_Town_Add_Borders;
import org.xodia.civ.net.Network.Server_Update_Civ;
import org.xodia.civ.net.Network.Server_Update_Game;
import org.xodia.civ.net.Network.Server_Update_Lobby;
import org.xodia.civ.net.Network.Server_Update_Research;
import org.xodia.civ.net.Network.Server_Update_Town;
import org.xodia.civ.net.Network.Server_Update_Town_Production;
import org.xodia.civ.net.Network.Server_Update_Unit;
import org.xodia.civ.units.ProductionManager;
import org.xodia.civ.units.ProductionManager.Building;
import org.xodia.civ.units.Town;
import org.xodia.civ.units.Unit;
import org.xodia.civ.units.Unit.CombatType;
import org.xodia.civ.units.Unit.ID;
import org.xodia.civ.units.Unit.UnitType;
import org.xodia.civ.units.UnitFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
	
	private enum Status {
		AVAILABLE, ONGOING, FULL
	}
	
	public static enum Age {
		Dawn(1), Colonial(125), Industrial(200), Modern(235);
		
		private int turn;
		
		Age(int turn){
			this.turn = turn;
		}
		
		public int getTurn(){
			return turn;
		}
		
	}
	
	private final int DEFAULT_PLAYERS = 6;
	
	private long currentUniqueID;
	private long currentUniqueGameID;
	
	private Server server;

	/**
	 * Hosts ID... Usually the first person to get into the Server is the host in LAN
	 */
	private long hostID;
	
	/**
	 * If the server is closed and no one will be able to connect
	 */
	private boolean isClosed;
	
	/**
	 * Hosts input of the number of players the server allows
	 */
	private int total_players;
	
	/**
	 * Describes the current status of the server and determines
	 * whether or not players can get into it
	 */
	private Status currentStatus;
	/**
	 * We have this map and the game will send it to the clients. The clients
	 * wrap around this map and then wallah, both the clients and the servers has the data
	 */
	private Map gameMap;
	
	/**
	 * The seed of the map is created when the map is created
	 */
	private MapSeed seed;
	
	/**
	 * The Player Data's or Packets
	 * THis keeps track of player data
	 * Gets erased when entering a new game or
	 * going back to lobby!
	 */
	private List<PlayerPacket> packets;
	
	/*
	 * Link to the player's civ object
	 * without having to fiddle around the packets
	 * Safe way to ensuring nothing is changed without
	 * permission
	 */
	private ConcurrentHashMap<Long, CivManager> civManagerList;
	
	/**
	 * Explains whose turn it is
	 */
	private long currentTurn;
	
	/**
	 * Identifies the first person to go, creates a
	 * revolution...
	 */
	private long startTurnID;
	
	private int currentYears;
	private int maxYears;
	
	private Age currentAge;
	
	{
		hostID = -1;
	}
	
	public GameServer(int port, int totalplayers) throws Exception {
		server = new Server(){
			protected Connection newConnection(){
				return new PlayerConnection();
			}
		};
		
		Network.register(server);
		
		server.addListener(new Listener(){
			public void connected(Connection c){
				System.out.println("Connected With Player");
			}
			
			public void received(Connection c, Object object){
				if(object instanceof Client_Data){
					if(hostID == -1){
						hostID = currentUniqueID;
					}
					
					((PlayerConnection) c).username = ((Client_Data) object).username;
					((PlayerConnection) c).uniqueID = currentUniqueID++;
					
					PlayerPacket packet = new PlayerPacket(((PlayerConnection) c).uniqueID);
					packet.username = ((PlayerConnection) c).username;

					packets.add(packet);
					
					return;
				}
				
				if(object instanceof Client_Leave){
					// Makes the player leave!
					c.close();
					
					return;
				}
				
				if(object instanceof Client_Join){
					// Checks to see if there is room
					// First room status
					if(currentStatus == Status.AVAILABLE){
						// Then checks to see if it is full
						synchronized(packets){
							if(packets.size() <= total_players){
								Server_Join_Success success = new Server_Join_Success();
								
								success.id = ((PlayerConnection) c).uniqueID;
								if(hostID == ((PlayerConnection) c).uniqueID){
									success.isHost = true;
								}
								
								c.sendTCP(success);
								
								// Now send it the updated lobby to everyone
								Server_Update_Lobby update = new Server_Update_Lobby();
								String[] playerList = new String[packets.size()];
								
								for(int i = 0; i < packets.size(); i++){
									for(Connection con : server.getConnections()){
										if(((PlayerConnection) con).uniqueID == packets.get(i).id){
											playerList[i] = packets.get(i).username;
										}
									}
								}
								
								update.players = playerList;
								server.sendToAllTCP(update);
								
								Server_Update_Game packet = new Server_Update_Game();
								packet.packets = packets.toArray(new PlayerPacket[packets.size()]);
								
								server.sendToAllTCP(packet);
							}else{
								Server_Join_Failed fail = new Server_Join_Failed();
								fail.reason = "The room is too full!";
								c.sendTCP(fail);
							}
						}
					}else{
						Server_Join_Failed fail = new Server_Join_Failed();
						fail.reason = "Server Status: " + currentStatus;
						c.sendTCP(fail);
					}
					
					return;
				}
				
				if(object instanceof Client_GameSettings){
					
					return;
				}
				
				if(object instanceof Client_Text_PubMessage){
					Client_Text_PubMessage mess = (Client_Text_PubMessage) object;
					Server_Text_PubMessage sMess = new Server_Text_PubMessage();
					sMess.name = mess.name;
					sMess.text = mess.text;
					server.sendToAllTCP(sMess);
					
					return;
				}
				
				if(object instanceof Client_Text_WhisperMessage){
					Client_Text_WhisperMessage mess = (Client_Text_WhisperMessage) object;
					Server_Text_WhisperMessage sMess = new Server_Text_WhisperMessage();
					sMess.from = mess.fromName;
					sMess.fromID = ((PlayerConnection) c).uniqueID;
					sMess.toID = mess.to;
					sMess.to = mess.toName;
					sMess.text = mess.text;
					
					// Send it to a specific one!
					for(Connection con : server.getConnections()){
						PlayerConnection pcon = (PlayerConnection) con;
						if(pcon.uniqueID == mess.to){
							pcon.sendTCP(sMess);
							break;
						}
					}
					
					c.sendTCP(sMess);
					
					return;
				}

				if(object instanceof Client_Start){
					// Create the game and then send map to the clients
					// Before we start it, we have to make sure that all settings are appropriate
					boolean hasFailed = false;
					String reason = "";
					
					// Check if the players have a civ
					for(PlayerPacket p : packets){
						if(p.civAbility == null && p.civName == null && p.civPackage == null){
							reason = "Not all players have a civilization!";
							hasFailed = true;
							break;
						}
					}
					
					if(hasFailed){
						Server_Game_Start_Failed failed = new Server_Game_Start_Failed();
						failed.message = reason;
						c.sendTCP(failed);
						
						return;
					}
					
					currentStatus = Status.ONGOING;
					
					gameMap = new Map(null, MapSize.STANDARD);
					gameMap.generateMap();
					seed = gameMap.getSeed();
					
					HashMap<Long, Civ> civList = new HashMap<Long, Civ>();
					
					synchronized(packets){
						for(PlayerPacket packet : packets){
							Civilization.Civ civilization = new Civilization.Civ(packet.civName, packet.civPackage.getStarterResearch(), packet.civAbility);
							Civilization civ = new Civilization(civilization, packet.username, packet.id);
							civManagerList.put(packet.id, new CivManager(civ));
							civList.put(packet.id, civilization);
						}
					}
					
					CivSpawnManager spawnManager = CivSpawnManager.createManager(gameMap, civList);
					spawnManager.generateSpawns();
					
					// Send the new packets to the clients
					Server_Update_Game update = new Server_Update_Game();
					update.packets = packets.toArray(new PlayerPacket[packets.size()]);
					
					server.sendToAllTCP(update);
					
					// Send the seed to the clients
					Server_Game_Start data = new Server_Game_Start();
					data.seed = seed;
					
					server.sendToAllTCP(data);
					
					// Send the settler units!
					for(PlayerPacket packet : packets){
						// Put in the data into the civManager
						civManagerList.get(packet.id).addNonCombatant(UnitFactory.newInstance().createUnit(spawnManager.getSpawn(packet.id).getTileX(), spawnManager.getSpawn(packet.id).getTileY(), currentUniqueGameID, packet.id, Unit.ID.Settler));
						
						// Then send the data to the client for the same thing
						Server_Create_Unit creation = new Server_Create_Unit();
						creation.playerID = packet.id;
						creation.id = currentUniqueGameID++;
						creation.x = spawnManager.getSpawn(packet.id).getTileX();
						creation.y = spawnManager.getSpawn(packet.id).getTileY();
						creation.unitID = ID.Settler;
						
						server.sendToAllTCP(creation);
					}
					
					// Randomize whose turn it is going to be for the first turn!
					long chosenID = packets.get(new Random().nextInt(packets.size())).id;
					currentTurn = chosenID;
					startTurnID = chosenID;
					
					// We then get the package and send it
					for(Connection con : server.getConnections()){
						if(((PlayerConnection) con).uniqueID == chosenID){
							Server_Client_Turn turn = new Server_Client_Turn();
							con.sendTCP(turn);
							Server_New_Turn turnT = new Server_New_Turn();
							turnT.currentTurn = currentYears;
							turnT.currentAge = currentAge;
							con.sendTCP(turnT);
							break;
						}
					}
					
					return;
				}
				
				if(object instanceof Client_Change_Civ){
					Client_Change_Civ change = (Client_Change_Civ) object;
					
					synchronized(packets){
						for(PlayerPacket p : packets){
							if(p.id == ((PlayerConnection) c).uniqueID){
								p.civAbility = change.abil;
								p.civPackage = change.pack;
								p.civName = change.civName;
								p.version++;
								break;
							}
						}
						
						Server_Update_Game packet = new Server_Update_Game();
						packet.packets = packets.toArray(new PlayerPacket[packets.size()]);
						
						server.sendToAllTCP(packet);
					}
					
					return;
				}
				
				if(object instanceof Client_End_Turn){
					// Resend the Client a message to make it's boolean value false
					c.sendTCP(new Server_Client_End_Turn());
					
					for(Connection con : server.getConnections()){
						PlayerConnection pcon = (PlayerConnection) con;
						if(pcon.uniqueID == currentTurn){
							// For the towns increment its production!
							for(Town t : civManagerList.get(currentTurn).getTownList()){
								t.getProductionManager().incrementProductionTurn();
								t.calculateStats();
								
								// Sieging!
								// Decrease the siege from an equation
								if(t.isSieged()){
									int totalDamage = 0;
									int totalUnits = 0;
									
									for(Unit u : t.getSiegedList()){
										totalDamage += u.getStrength();
										totalUnits++;
									}
									
									int trueDamage = (int) (totalDamage / (totalUnits + (totalUnits * 0.5f)));
									
									t.setCurrentDefense(t.getCurrentDefense() - trueDamage);
									
									if(t.getCurrentDefense() <= 0){
										t.setCurrentDefense(0);
										// IN TROUBLE! NOW RELEASE ALL UNITS ENGAGED BY TOWN
										for(Unit u : t.getSiegedList()){
											u.setAction(false);
											u.setSieging(false);
											u.setTownSiegedID(-1);
											u.setTownSiegedPlayerID(-1);
											
											Server_Siege_Unit_Cancel cancel = new Server_Siege_Unit_Cancel();
											cancel.playerID = u.getPlayerID();
											cancel.unitID = u.getID();
											server.sendToAllTCP(cancel);
										}
										t.clearSieged();
									}
								}else{
									if(t.getCurrentDefense() != t.getMaxDefense()){
										// Increase its defense per turn...
										t.setCurrentDefense(t.getCurrentDefense() + 2);
										
										if(t.getCurrentDefense() > t.getMaxDefense()){
											t.setCurrentDefense(t.getMaxDefense());
										}
									}
								}
								
								Server_Update_Town updateT = new Server_Update_Town();
								updateT.playerID = currentTurn;
								updateT.townID = t.getID();
								updateT.currentIncome = t.getCurrentIncome();
								updateT.maxGrowth = t.getMaxGrowth();
								updateT.currentGrowth = t.getCurrentGrowth();
								updateT.isSieged = t.isSieged();
								updateT.currentDefense = t.getCurrentDefense();
								updateT.isCapital = t.isCapital();
								
								server.sendToAllTCP(updateT);
								
								if(t.getProductionManager().isProductionFinished()){
									if(t.getProductionManager().isBuilding()){
										t.addBuildingToHave((Building) t.getProductionManager().getProduction());
										
										Server_Add_Building_To_Town message = new Server_Add_Building_To_Town();
										message.playerID = currentTurn;
										message.townID = t.getID();
										message.prodID = ((Building) t.getProductionManager().getProduction()).getID();
										
										server.sendToAllTCP(message);
										
										t.getProductionManager().setProduction(null);
										
										Server_Add_Town_Production prod = new Server_Add_Town_Production();
										prod.isBuilding = false;
										prod.playerID = currentTurn;
										prod.prodID = -1;
										prod.townID = t.getID();
										
										server.sendToAllTCP(prod);
									}else{
										// Create a Unit
										Unit u = UnitFactory.newInstance().createUnit(t.getX(), t.getY(), currentUniqueGameID++, currentTurn, ((org.xodia.civ.units.ProductionManager.Unit) t.getProductionManager().getProduction()).getID());
										
										if(!civManagerList.get(currentTurn).containsResearch(Research.Optic)){
											if(civManagerList.get(currentTurn).containsResearch(Research.Compass)){
												u.setUnitType(UnitType.ShallowAmphibious);
											}
										}else{
											u.setUnitType(UnitType.DeepAmphibious);
										}
										
										if(u.getUnitID().isNonCombatant()){
											civManagerList.get(currentTurn).addNonCombatant(u);
										}else{
											civManagerList.get(currentTurn).addCombatant(u);
										}

										t.getProductionManager().setProduction(null);
										
										Server_Create_Unit message = new Server_Create_Unit();
										message.playerID = currentTurn;
										message.unitID = u.getUnitID();
										message.x = u.getX();
										message.y = u.getY();
										message.id = u.getID();
										
										server.sendToAllTCP(message);
										
										Server_Add_Town_Production prod = new Server_Add_Town_Production();
										prod.isBuilding = false;
										prod.playerID = currentTurn;
										prod.prodID = -1;
										prod.townID = t.getID();
										
										server.sendToAllTCP(prod);
									}
								}
								
								// Send a message that the production of the building or unit has incremented
								Server_Update_Town_Production prod = new Server_Update_Town_Production();
								prod.playerID = currentTurn;
								prod.townID = t.getID();
								
								// Update the border growth
								t.setCurrentBorderGrowth(t.getCurrentBorderGrowth() + ((t.getCurrentPopulation() * t.getCurrentFood()) / (t.getCurrentFood() / 2)));
								
								if(t.getCurrentBorderGrowth() >= t.getMaxBorderGrowth()){
									// Get the remainder
									int remainder = t.getMaxBorderGrowth() - t.getCurrentBorderGrowth();
									t.setCurrentBorderGrowth(remainder);
									t.setMaxBorderGrowth(t.getMaxBorderGrowth());
									
									Random random = new Random();
									int tile = random.nextInt(t.getNonBorderSize());
									
									// Get the tile
									Tile tTile = t.getNonBorderList()[tile];
									
									// Add it
									t.addBorder(tTile, currentTurn);
									t.removeNonBorder(tTile);
									
									Server_Town_Add_Border addBorder = new Server_Town_Add_Border();
									addBorder.playerID = currentTurn;
									addBorder.townID = t.getID();
									addBorder.x = tTile.getX();
									addBorder.y = tTile.getY();
									server.sendToAllTCP(addBorder);
									
									if(t.getNonBorderSize() <= 0){
										// Find the new ones
										for(int i = -1; i < t.getMaxBorderY() - t.getMinBorderY() + 2; i++){
											if(t.getMinBorderX() - 1 >= 0){
												Tile tempT = gameMap.getTileAt(t.getMinBorderX() - 1, t.getMinBorderY() + i);
												
												if(tempT != null)
													t.addNonBorder(tempT);
											}
											
											if(t.getMaxBorderX() + 1 < gameMap.getWidth()){
												Tile tempT = gameMap.getTileAt(t.getMaxBorderX() + 1, t.getMinBorderY() + i);
												
												if(tempT != null)
													t.addNonBorder(tempT);
											}
										}
										
										for(int i = -1; i < t.getMaxBorderX() - t.getMinBorderX() + 2; i++){
											if(t.getMinBorderY() - 1 >= 0){
												Tile tempT = gameMap.getTileAt(t.getMinBorderX() + i, t.getMinBorderY() - 1);
												
												if(tempT != null)
													t.addNonBorder(tempT);
											}
											
											if(t.getMaxBorderY() + 1 < gameMap.getHeight()){
												Tile tempT = gameMap.getTileAt(t.getMinBorderX() + i, t.getMaxBorderY() + 1);
												
												if(tempT != null)
													t.addNonBorder(tempT);
											}
										}
									}
								}
								
								server.sendToAllTCP(prod);
							}
							
							// Reset all of the unit's movement points!
							// This is towards ONLY the current player's turn!
							for(Unit u : civManagerList.get(currentTurn).getCombatantList()){
								if(u.isAction())
									u.setCurrentMovePoints(0);
								else
									u.setCurrentMovePoints(u.getMaxMovePoints());
								
								Server_Update_Unit update = new Server_Update_Unit();
								update.playerID = currentTurn;
								update.id = u.getID();
								update.x = u.getX();
								update.y = u.getY();
								update.currentHealth = u.getCurrentHealth();
								update.isAction = u.isAction();
								update.isSieging = u.isSieging();
								update.isCombatant = !u.getUnitID().isNonCombatant();
								update.currentMP = u.getCurrentMovePoints();
								update.townSiegedID = u.getTownSiegedID();
								update.townSiegedPlayerID = u.getTownSiegedPlayerID();
								pcon.sendTCP(update);
							}
							
							for(Unit u : civManagerList.get(currentTurn).getNonCombatantList()){
								if(u.isAction())
									u.setCurrentMovePoints(0);
								else
									u.setCurrentMovePoints(u.getMaxMovePoints());
								
								Server_Update_Unit update = new Server_Update_Unit();
								update.playerID = currentTurn;
								update.id = u.getID();
								update.x = u.getX();
								update.y = u.getY();
								update.currentHealth = u.getCurrentHealth();
								update.isAction = u.isAction();
								update.isSieging = u.isSieging();
								update.isCombatant = !u.getUnitID().isNonCombatant();
								update.currentMP = u.getCurrentMovePoints();
								update.townSiegedID = u.getTownSiegedID();
								update.townSiegedPlayerID = u.getTownSiegedPlayerID();
								pcon.sendTCP(update);
							}
							
							// Get the Civ's Things
							CivManager civ = civManagerList.get(currentTurn);
							// Find out how much gold is there
							int gold = 0;
							for(Town t : civ.getTownList()){
								gold += t.getCurrentIncome();
							}
							civ.setTotalGold(civ.getTotalGold() + gold);
							
							if(civ.getCurrentResearch() != null){
								civ.incrementResearch();
								
								Server_Update_Research rUpdate = new Server_Update_Research();
								rUpdate.playerID = currentTurn;
								server.sendToAllTCP(rUpdate);
								
								if(civ.getResearchTurnsLeft() >= civ.getCurrentResearch().getTurns()){
									Server_Add_Research add = new Server_Add_Research();
									add.playerID = currentTurn;
									server.sendToAllTCP(add);
									
									civ.addResearch(civ.getCurrentResearch());
									civ.clearResearch();
								}
							}
							
							Server_Update_Civ update = new Server_Update_Civ();
							update.playerID = currentTurn;
							update.totalGold = civ.getTotalGold();
							server.sendToAllTCP(update);
							
							// then find the next person
							long chosenID = getNextTurn();
							currentTurn = chosenID;
							
							if(currentTurn == startTurnID){
								currentYears++;
								
								if(currentYears == 125){
									currentAge = Age.Colonial;
								}else if(currentYears == 200){
									currentAge = Age.Industrial;
								}else if(currentYears == 235){
									currentAge = Age.Modern;
								}
								
								if(currentYears >= maxYears){
									// TODO END THE GAME
									// THIS WILL BE BASED ON POINTS
								}
							}
							
							break;
						}
					}
					
					for(Connection con : server.getConnections()){
						PlayerConnection pcon = (PlayerConnection) con;
						if(pcon.uniqueID == currentTurn){
							pcon.sendTCP(new Server_Client_Turn());
							Server_New_Turn turn = new Server_New_Turn();
							turn.currentTurn = currentYears;
							turn.currentAge = currentAge;
							pcon.sendTCP(turn);
							break;
						}
					}
					
					return;
				}
				
				if(object instanceof Client_Move_Unit){
					Client_Move_Unit msg = (Client_Move_Unit) object;
					
					// Change the unit!
					if(msg.isCombatant){
						civManagerList.get(msg.playerID).getCombatantList(msg.id).setX(msg.tx);
						civManagerList.get(msg.playerID).getCombatantList(msg.id).setY(msg.ty);
						civManagerList.get(msg.playerID).getCombatantList(msg.id).setCurrentMovePoints(civManagerList.get(msg.playerID).getCombatantList(msg.id).getCurrentMovePoints() - msg.totalCost);
						
						Server_Update_Unit unit = new Server_Update_Unit();
						unit.playerID = msg.playerID;
						unit.id = msg.id;
						unit.x = msg.tx;
						unit.y = msg.ty;
						unit.currentHealth = civManagerList.get(msg.playerID).getCombatantList(msg.id).getCurrentHealth();
						unit.isCombatant = msg.isCombatant;
						unit.isAction = civManagerList.get(msg.playerID).getCombatantList(msg.id).isAction();
						unit.isSieging = civManagerList.get(msg.playerID).getCombatantList(msg.id).isSieging();
						unit.currentMP = civManagerList.get(msg.playerID).getCombatantList(msg.id).getCurrentMovePoints();
						unit.townSiegedID = civManagerList.get(msg.playerID).getCombatantList(msg.id).getTownSiegedID();
						unit.townSiegedPlayerID = civManagerList.get(msg.playerID).getCombatantList(msg.id).getTownSiegedPlayerID();
						server.sendToAllTCP(unit);
					}else{
						civManagerList.get(msg.playerID).getNonCombatantList(msg.id).setX(msg.tx);
						civManagerList.get(msg.playerID).getNonCombatantList(msg.id).setY(msg.ty);
						civManagerList.get(msg.playerID).getNonCombatantList(msg.id).setCurrentMovePoints(civManagerList.get(msg.playerID).getNonCombatantList(msg.id).getCurrentMovePoints() - msg.totalCost);
						
						Server_Update_Unit unit = new Server_Update_Unit();
						unit.playerID = msg.playerID;
						unit.id = msg.id;
						unit.x = msg.tx;
						unit.y = msg.ty;
						unit.isCombatant = msg.isCombatant;
						unit.currentHealth = civManagerList.get(msg.playerID).getNonCombatantList(msg.id).getCurrentHealth();
						unit.currentMP = civManagerList.get(msg.playerID).getNonCombatantList(msg.id).getCurrentMovePoints();
						unit.isAction = civManagerList.get(msg.playerID).getNonCombatantList(msg.id).isAction();
						unit.isSieging = civManagerList.get(msg.playerID).getNonCombatantList(msg.id).isSieging();
						unit.townSiegedID = civManagerList.get(msg.playerID).getNonCombatantList(msg.id).getTownSiegedID();
						unit.townSiegedPlayerID = civManagerList.get(msg.playerID).getNonCombatantList(msg.id).getTownSiegedPlayerID();
						server.sendToAllTCP(unit);
					}
					
					return;
				}
				
				if(object instanceof Client_Create_Town){
					Client_Create_Town creation = (Client_Create_Town) object;
					
					// Remove the unit
					if(creation.isCombatant){
						civManagerList.get(creation.playerID).removeCombatant(creation.unitID);
					}else{
						civManagerList.get(creation.playerID).removeNonCombatant(creation.unitID);
					}
					
					Server_Remove_Unit removal = new Server_Remove_Unit();
					removal.playerID = creation.playerID;
					removal.id = creation.unitID;
					removal.isCombatant = creation.isCombatant;
					
					server.sendToAllTCP(removal);
					
					// Create the Town and add it to the Civilization
					// Send the message to all other clients
					Town town = new Town(creation.playerID, currentUniqueGameID++, creation.x, creation.y);
					town.setName(creation.tName);
					town.addBorder(gameMap.getTileAt(town.getX(), town.getY()), creation.playerID);
					
					// Create borders
					int x = creation.x;
					int y = creation.y;
					
					if(x - 1 >= 0 || x + 1 < gameMap.getWidth() || y - 1 >= 0 || y + 1 < gameMap.getHeight()){
						for(int i = -1; i < 2; i++){
							for(int j = -1; j < 2; j++){
								Tile t = gameMap.getTileAt(x + i, y + j);
								
								if(t != null)
									town.addBorder(t, creation.playerID);
							}
						}
					}
					
					// Add the "Soon to be Tiles"
					for(int i = -1; i < town.getMaxBorderY() - town.getMinBorderY() + 2; i++){
						if(town.getMinBorderX() - 1 >= 0){
							Tile t = gameMap.getTileAt(town.getMinBorderX() - 1, town.getMinBorderY() + i);
							
							if(t != null)
								town.addNonBorder(t);
						}
						
						if(town.getMaxBorderX() + 1 < gameMap.getWidth()){
							Tile t = gameMap.getTileAt(town.getMaxBorderX() + 1, town.getMinBorderY() + i);
							
							if(t != null)
								town.addNonBorder(t);
						}
					}
					
					for(int i = -1; i < town.getMaxBorderX() - town.getMinBorderX() + 2; i++){
						if(town.getMinBorderY() - 1 >= 0){
							Tile t = gameMap.getTileAt(town.getMinBorderX() + i, town.getMinBorderY() - 1);
							
							if(t != null)
								town.addNonBorder(t);
						}
						
						if(town.getMaxBorderY() + 1 < gameMap.getHeight()){
							Tile t = gameMap.getTileAt(town.getMinBorderX() + i, town.getMaxBorderY() + 1);
							
							if(t != null)
								town.addNonBorder(t);
						}
					}
					
					if(!civManagerList.get(creation.playerID).hasCapital()){
						town.setCapital(true);
					}
					
					civManagerList.get(creation.playerID).addTown(town);
					
					Server_Add_Town add = new Server_Add_Town();
					add.id = town.getID();
					add.playerID = creation.playerID;
					add.name = creation.tName;
					add.x = town.getX();
					add.y = town.getY();
					add.isCapital = town.isCapital();
					
					server.sendToAllTCP(add);
					
					int[] xs = new int[town.getBorderList().length];
					int[] ys = new int[xs.length];
					
					for(int i = 0; i < xs.length; i++){
						Tile t = town.getBorderList()[i];
						xs[i] = t.getX();
						ys[i] = t.getY();
					}
					
					Server_Town_Add_Borders border = new Server_Town_Add_Borders();
					border.playerID = creation.playerID;
					border.townID = town.getID();
					border.xs = xs;
					border.ys = ys;
					server.sendToAllTCP(border);
					
					return;
				}
				
				if(object instanceof Client_Set_Town_Production){
					Client_Set_Town_Production msg = (Client_Set_Town_Production) object;
					
					Town town = null;
					
					for(Town t : civManagerList.get(msg.playerID).getTownList()){
						if(t.getID() == msg.townID){
							town = t;
							break;
						}
					}
					
					if(town != null){
						 town.getProductionManager().setProduction(ProductionManager.getProductionFrom(msg.prodID, msg.isBuilding));
						 // Update this
						 Server_Add_Town_Production add = new Server_Add_Town_Production();
						 add.playerID = msg.playerID;
						 add.townID = msg.townID;
						 add.prodID = msg.prodID;
						 add.isBuilding = msg.isBuilding;
						 
						 server.sendToAllTCP(add);
					}
					
					return;
				}
				
				if(object instanceof Client_Attack_Unit){
					Client_Attack_Unit message = (Client_Attack_Unit) object;
					
					// Find the two Units
					Unit unit = null;
					Unit attUnit = null;
					
					if(message.isCombatant){
						unit = civManagerList.get(message.playerID).getCombatantList(message.unitID);
					}else{
						unit = civManagerList.get(message.playerID).getNonCombatantList(message.unitID);
					}
					
					if(message.isAttCombatant){
						attUnit = civManagerList.get(message.attPlayerID).getCombatantList(message.attUnitID);
					}else{
						attUnit = civManagerList.get(message.attPlayerID).getNonCombatantList(message.attUnitID);
					}
					
					// Unit has spent its turn attacking
					unit.setCurrentMovePoints(0);
					
					// Battle Sequence
					// Get the strength of both parties
					int strUnit = 0;
					
					if(message.combat == CombatType.Melee)
						strUnit = unit.getStrength();
					else if(message.combat == CombatType.Range)
						strUnit = unit.getRangeStrength();
					
					int attStrUnit = attUnit.getStrength();
					
					if(message.combat == CombatType.Melee){
						// Deal damage to individuals
						unit.setCurrentHealth(unit.getCurrentHealth() - attStrUnit);
						attUnit.setCurrentHealth(attUnit.getCurrentHealth() - strUnit);
					}else if(message.combat == CombatType.Range){
						attUnit.setCurrentHealth(attUnit.getCurrentHealth() - strUnit);
					}
					
					if(attUnit.getCurrentHealth() <= 0){
						if(message.combat == CombatType.Melee){
							// Only melee units can do this
							unit.setX(attUnit.getX());
							unit.setY(attUnit.getY());
						}
						
						// Send a Remove Message
						// Update the Unit
						Server_Remove_Unit remove = new Server_Remove_Unit();
						remove.isCombatant = message.isAttCombatant;
						remove.playerID = message.attPlayerID;
						remove.id = message.attUnitID;
						server.sendToAllTCP(remove);
						
						Unit u = null;
						if(message.isAttCombatant){
							u = civManagerList.get(message.attPlayerID).getCombatantList(message.attUnitID);
							civManagerList.get(message.attPlayerID).removeCombatant(message.attUnitID);
						}else{
							u = civManagerList.get(message.attPlayerID).getNonCombatantList(message.attUnitID);
							civManagerList.get(message.attPlayerID).removeNonCombatant(message.attUnitID);
						}
						
						if(u.getUnitID() == ID.Settler){
							// Find out if there are no towns and no other settlers
							if(civManagerList.get(message.attPlayerID).getTownList().length == 0){
								boolean isSettlerFound = false;
								
								for(Unit su : civManagerList.get(message.attPlayerID).getNonCombatantList()){
									if(su.getUnitID() == ID.Settler){
										isSettlerFound = true;
										break;
									}
								}
								
								if(!isSettlerFound){
									Server_Game_Lost lost = new Server_Game_Lost();
									
									civManagerList.get(message.attPlayerID).setAlive(false);
									
									for(Connection con : server.getConnections()){
										PlayerConnection pcon = (PlayerConnection) con;
										if(pcon.uniqueID == message.attPlayerID){
											pcon.sendTCP(lost);
											break;
										}
									}
									
									// Eliminate the packet...
									int index = 0;
									for(int i = 0; i < packets.size(); i++){
										if(packets.get(i).id == message.attPlayerID){
											index = i;
											break;
										}
									}
									
									packets.remove(index);
									
									// Check if all the civilizations are dead!
									int count = 0;
									long id = 0;
									for(int i = 0; i < packets.size(); i++){
										CivManager civ = civManagerList.get(packets.get(i).id);
										if(civ.isAlive()){
											count++;
											id = packets.get(i).id;
										}
									}
									
									if(count == 1){
										for(Connection con : server.getConnections()){
											PlayerConnection pcon = (PlayerConnection) con;
											if(pcon.uniqueID == id){
												pcon.sendTCP(new Server_Game_Won());
												break;
											}
										}
									}
								}
							}
						}
						
						if(u.isAction() && u.isSieging()){
							// Remove itself from the town
							Town t = civManagerList.get(u.getTownSiegedPlayerID()).getTown(u.getTownSiegedID());
							t.removeSieged(u);
							// Check if it is still sieged...
							if(t.getSiegedList().length == 0){
								t.setSieged(false);
								Server_Update_Town updateT = new Server_Update_Town();
								updateT.playerID = t.getPlayerID();
								updateT.townID = t.getID();
								updateT.currentGrowth = t.getCurrentGrowth();
								updateT.currentIncome = t.getCurrentIncome();
								updateT.maxGrowth = t.getMaxGrowth();
								updateT.isSieged = t.isSieged();
								updateT.isCapital = t.isCapital();
								server.sendToAllTCP(updateT);
							}
						}
					}else{
						if(message.combat == CombatType.Melee){
							unit.setX(message.prevX);
							unit.setY(message.prevY);
						}
						
						Server_Update_Unit update2 = new Server_Update_Unit();
						update2.isCombatant = message.isAttCombatant;
						update2.currentMP = attUnit.getCurrentMovePoints();
						update2.currentHealth = attUnit.getCurrentHealth();
						update2.id = attUnit.getID();
						update2.playerID = message.attPlayerID;
						update2.x = attUnit.getX();
						update2.y = attUnit.getY();
						update2.isAction = attUnit.isAction();
						update2.isSieging = attUnit.isSieging();
						update2.townSiegedID = attUnit.getTownSiegedID();
						update2.townSiegedID = attUnit.getTownSiegedPlayerID();
						server.sendToAllTCP(update2);
					}
					
					if(unit.getCurrentHealth() <= 0){
						Server_Remove_Unit remove = new Server_Remove_Unit();
						remove.isCombatant = message.isCombatant;
						remove.playerID = message.playerID;
						remove.id = message.unitID;
						server.sendToAllTCP(remove);
						
						// WE have to see if the unit is sieging a town
						Unit u = null;
						if(message.isCombatant){
							u = civManagerList.get(message.playerID).getCombatantList(message.unitID);
							civManagerList.get(message.playerID).removeCombatant(message.unitID);
						}else{
							u = civManagerList.get(message.playerID).getNonCombatantList(message.unitID);
							civManagerList.get(message.playerID).removeNonCombatant(message.unitID);
						}
						
						if(u.isAction() && u.isSieging()){
							// Remove itself from the town
							Town t = civManagerList.get(u.getTownSiegedPlayerID()).getTown(u.getTownSiegedID());
							t.removeSieged(u);
							// Check if it is still sieged...
							if(t.getSiegedList().length == 0){
								t.setSieged(false);
								Server_Update_Town updateT = new Server_Update_Town();
								updateT.playerID = t.getPlayerID();
								updateT.townID = t.getID();
								updateT.currentGrowth = t.getCurrentGrowth();
								updateT.currentIncome = t.getCurrentIncome();
								updateT.maxGrowth = t.getMaxGrowth();
								updateT.isSieged = t.isSieged();
								updateT.isCapital = t.isCapital();
								server.sendToAllTCP(updateT);
							}
						}
					}else{
						Server_Update_Unit update = new Server_Update_Unit();
						update.isCombatant = message.isCombatant;
						update.currentMP = unit.getCurrentMovePoints();
						update.currentHealth = unit.getCurrentHealth();
						update.id = unit.getID();
						update.playerID = message.playerID;
						update.x = unit.getX();
						update.y = unit.getY();
						update.isAction = unit.isAction();
						update.isSieging = unit.isSieging();
						update.townSiegedID = unit.getTownSiegedID();
						update.townSiegedID = unit.getTownSiegedPlayerID();
						server.sendToAllTCP(update);
					}
					
					return;
				}
				
				if(object instanceof Client_Set_Research){
					Client_Set_Research re = (Client_Set_Research) object;
					
					civManagerList.get(re.playerID).setResearch(re.research);
					
					Server_Set_Research setMessage = new Server_Set_Research();
					setMessage.playerID = re.playerID;
					setMessage.research = re.research;
					server.sendToAllTCP(setMessage);
					
					civManagerList.get(re.playerID).setTotalGold(civManagerList.get(re.playerID).getTotalGold() - re.research.getCost());
					
					Server_Update_Civ update = new Server_Update_Civ();
					update.playerID = re.playerID;
					update.totalGold = civManagerList.get(re.playerID).getTotalGold();
					server.sendToAllTCP(update);
					
					return;
				}
				
				if(object instanceof Client_Siege_Town){
					Client_Siege_Town siege = (Client_Siege_Town) object;
					
					Unit u = civManagerList.get(siege.playerID).getCombatantList(siege.unitID);
					Town t = civManagerList.get(siege.enemyID).getTown(siege.eTownID);
					
					u.setAction(true);
					u.setSieging(true);
					u.setCurrentMovePoints(0);
					u.setTownSiegedID(siege.eTownID);
					u.setTownSiegedPlayerID(siege.enemyID);
					
					if(u.getCombatType() == CombatType.Melee){
						u.setX(siege.prevX);
						u.setY(siege.prevY);
					}
					
					Server_Update_Unit updateU = new Server_Update_Unit();
					updateU.playerID = siege.playerID;
					updateU.id = u.getID();
					updateU.x = u.getX();
					updateU.y = u.getY();
					updateU.currentHealth = u.getCurrentHealth();
					updateU.isCombatant = !u.getUnitID().isNonCombatant();
					updateU.currentMP = u.getCurrentMovePoints();
					updateU.isAction = u.isAction();
					updateU.isSieging = u.isSieging();
					updateU.townSiegedID = u.getTownSiegedID();
					updateU.townSiegedPlayerID = u.getTownSiegedPlayerID();
					server.sendToAllTCP(updateU);
					
					t.setSieged(true);
					t.addSieged(u);
					
					Server_Update_Town updateT = new Server_Update_Town();
					updateT.playerID = siege.enemyID;
					updateT.townID = siege.eTownID;
					updateT.currentGrowth = t.getCurrentGrowth();
					updateT.currentIncome = t.getCurrentIncome();
					updateT.maxGrowth = t.getMaxGrowth();
					updateT.isSieged = t.isSieged();
					updateT.isCapital = t.isCapital();
					updateT.currentDefense = t.getCurrentDefense();
					server.sendToAllTCP(updateT);
					
					return;
				}
				
				if(object instanceof Client_Cancel_Siege){
					Client_Cancel_Siege cancel = (Client_Cancel_Siege) object;
					
					// Remove itself from the town
					Town t = civManagerList.get(cancel.enemyID).getTown(cancel.eTownID);
					Unit u = civManagerList.get(cancel.playerID).getCombatantList(cancel.unitID);
					t.removeSieged(u);
					// Check if it is still sieged...
					if(t.getSiegedList().length == 0){
						t.setSieged(false);
						Server_Update_Town updateT = new Server_Update_Town();
						updateT.playerID = t.getPlayerID();
						updateT.townID = t.getID();
						updateT.currentGrowth = t.getCurrentGrowth();
						updateT.currentIncome = t.getCurrentIncome();
						updateT.maxGrowth = t.getMaxGrowth();
						updateT.isSieged = t.isSieged();
						updateT.isCapital = t.isCapital();
						server.sendToAllTCP(updateT);
					}
					
					return;
				}
				
				if(object instanceof Client_Raze_Town){
					Client_Raze_Town raze = (Client_Raze_Town) object;
					
					Unit u = civManagerList.get(raze.playerID).getCombatantList(raze.unitID);
					Town t = civManagerList.get(raze.enemyID).getTown(raze.eTownID);
					
					// Unit will move in and destory the town
					u.setX(t.getX());
					u.setY(t.getY());
					
					Server_Update_Unit unit = new Server_Update_Unit();
					unit.playerID = raze.playerID;
					unit.id = u.getID();
					unit.x = u.getX();
					unit.y = u.getY();
					unit.currentHealth = u.getCurrentHealth();
					unit.isCombatant = !u.getUnitID().isNonCombatant();
					unit.currentMP = u.getCurrentMovePoints();
					unit.isAction = u.isAction();
					unit.isSieging = u.isSieging();
					unit.townSiegedID = u.getTownSiegedID();
					unit.townSiegedPlayerID = u.getTownSiegedPlayerID();
					server.sendToAllTCP(unit);
					
					// Find out if this town was a capital
					boolean isCapital = t.isCapital();
					
					if(isCapital){
						// If the town is the only one, THEN THAT CIVILIZATION LOSES!
						if(civManagerList.get(raze.enemyID).getTownList().length == 1){
							boolean isSettlersLost = true;
							
							// We have to find out if a settler is alive in the civilization
							for(Unit sunit : civManagerList.get(raze.enemyID).getNonCombatantList()){
								if(sunit.getUnitID() == ID.Settler){
									isSettlersLost = false;
									continue;
								}
							}
							
							if(isSettlersLost){
								Server_Game_Lost lost = new Server_Game_Lost();
								
								civManagerList.get(raze.enemyID).setAlive(false);
								
								for(Connection con : server.getConnections()){
									PlayerConnection pcon = (PlayerConnection) con;
									if(pcon.uniqueID == raze.enemyID){
										pcon.sendTCP(lost);
										break;
									}
								}
								
								// Eliminate the packet...
								int index = 0;
								for(int i = 0; i < packets.size(); i++){
									if(packets.get(i).id == raze.enemyID){
										index = i;
										break;
									}
								}
								
								packets.remove(index);
								
								// Check if all the civilizations are dead!
								int count = 0;
								long id = 0;
								for(int i = 0; i < packets.size(); i++){
									CivManager civ = civManagerList.get(packets.get(i).id);
									if(civ.isAlive()){
										count++;
										id = packets.get(i).id;
									}
								}
								
								if(count == 1){
									for(Connection con : server.getConnections()){
										PlayerConnection pcon = (PlayerConnection) con;
										if(pcon.uniqueID == id){
											pcon.sendTCP(new Server_Game_Won());
											break;
										}
									}
								}
							}
						}else{
							// Find what index it is on...
							int i = 0;
							for(Town town : civManagerList.get(raze.enemyID).getTownList()){
								if(town == t){
									break;
								}
								
								i++;
							}
							
							if(i + 1 >= civManagerList.get(raze.enemyID).getTownList().length){
								Town town = civManagerList.get(raze.enemyID).getTownList()[0];
								town.setCapital(true);
								
								Server_Update_Town updateT = new Server_Update_Town();
								updateT.playerID = town.getPlayerID();
								updateT.townID = town.getID();
								updateT.currentGrowth = town.getCurrentGrowth();
								updateT.currentIncome = town.getCurrentIncome();
								updateT.maxGrowth = town.getMaxGrowth();
								updateT.isSieged = town.isSieged();
								updateT.isCapital = town.isCapital();
								server.sendToAllTCP(updateT);
							}else{
								Town town = civManagerList.get(raze.enemyID).getTownList()[i + 1];
								town.setCapital(true);
								
								Server_Update_Town updateT = new Server_Update_Town();
								updateT.playerID = town.getPlayerID();
								updateT.townID = town.getID();
								updateT.currentGrowth = town.getCurrentGrowth();
								updateT.currentIncome = town.getCurrentIncome();
								updateT.maxGrowth = town.getMaxGrowth();
								updateT.isSieged = town.isSieged();
								updateT.isCapital = town.isCapital();
								server.sendToAllTCP(updateT);
							}
						}
					}
					
					civManagerList.get(raze.enemyID).removeTown(t);
					
					// Delete the town
					Server_Remove_Town remove = new Server_Remove_Town();
					remove.playerID = t.getPlayerID();
					remove.id = t.getID();
					server.sendToAllTCP(remove);
					
					return;
				}
				
				if(object instanceof Client_Confirmed_Winner){
					// End the server...
					close();
				}
				
				/////////////////////////////////////////////////////
				// DEBUG CLIENT MESSAGES
				/////////////////////////////////////////////////////
				if(object instanceof Client_Mouse_Location){
					Client_Mouse_Location message = (Client_Mouse_Location) object;
					
					// Find the packet
					for(PlayerPacket packet : packets){
						if(packet.id == ((PlayerConnection) c).uniqueID){
							packet.mouseX = message.mousex;
							packet.mouseY = message.mousey;
							packet.version++;
						}
					}
					
					Server_Update_Game update = new Server_Update_Game();
					update.packets = packets.toArray(new PlayerPacket[packets.size()]);
					
					server.sendToAllTCP(update);
				}
				
			}
			
			public void disconnected(Connection c){
				System.out.println("Disconnected With Player");
				
				if(currentStatus == Status.AVAILABLE || currentStatus == Status.FULL){
					int index = 0;
					
					for(int i = 0; i < packets.size(); i++){
						if(packets.get(i).id == ((PlayerConnection) c).uniqueID){
							index = i;
							break;
						}
					}
					
					packets.remove(index);
					
					if(((PlayerConnection) c).uniqueID != hostID){
						Server_Update_Lobby update = new Server_Update_Lobby();
						String[] playerList = new String[packets.size()];
						
						for(int i = 0; i < packets.size(); i++){
							for(Connection con : server.getConnections()){
								if(((PlayerConnection) con).uniqueID == packets.get(i).id){
									playerList[i] = packets.get(i).username;
								}
							}
						}
						
						update.players = playerList;
						server.sendToAllTCP(update);
						
						Server_Update_Game update2 = new Server_Update_Game();
						update2.packets = packets.toArray(new PlayerPacket[packets.size()]);
						server.sendToAllTCP(update2);
					}else{
						server.close();
					}
				}else if(currentStatus == Status.ONGOING){
					int index = 0;
					
					for(int i = 0; i < packets.size(); i++){
						if(packets.get(i).id == ((PlayerConnection) c).uniqueID){
							index = i;
							break;
						}
					}
					
					packets.remove(index);
				}
			}
		});
		
		if(port == 0){
			server.bind(Network.DEFAULT_PORT);
		}else{
			server.bind(port);
		}
		server.start();
		
		currentStatus = Status.AVAILABLE;
		currentYears = 0;
		currentAge = Age.Dawn;
		maxYears = 256;
		
		if(totalplayers < 2)
			total_players = DEFAULT_PLAYERS;
		else
			total_players = totalplayers;
		
		packets = new ArrayList<PlayerPacket>();
		civManagerList = new ConcurrentHashMap<Long, CivManager>();
		
		System.out.println("Server IP: " + getIPAddress());
		System.out.println("Server Port: " + port);
	}
	
	public void close(){
		server.close();
		isClosed = true;
	}
	
	public String getIPAddress(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public long getNextTurn(){
		for(int i = 0; i < packets.size(); i++){
			if(packets.get(i).id == currentTurn){
				int index = i;
				if(++index < packets.size()){
					return packets.get(index).id;
				}else{
					return packets.get(0).id;
				}
			}
		}
		
		return -1;
	}
	
	public boolean isClosed(){
		return isClosed;
	}
	
	private class PlayerConnection extends Connection {
		public long uniqueID;
		public String username;
	}
	
}
