package org.xodia.civ.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.xodia.civ.CivilizationApp;
import org.xodia.civ.CivilizationAppData;
import org.xodia.civ.GameState;
import org.xodia.civ.GameState.Init_Package;
import org.xodia.civ.civs.CivManager;
import org.xodia.civ.civs.Civilization;
import org.xodia.civ.civs.Civilization.Research;
import org.xodia.civ.net.Network.Client_Confirmed_Winner;
import org.xodia.civ.net.Network.Client_Data;
import org.xodia.civ.net.Network.Client_Join;
import org.xodia.civ.net.Network.Client_Leave;
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
import org.xodia.civ.units.Unit.UnitType;
import org.xodia.civ.units.UnitFactory;
import org.xodia.civ.util.FadeTextManager;
import org.xodia.civ.util.TextChatQueue;
import org.xodia.usai2d.Button.OnClickListener;
import org.xodia.usai2d.DialogFactory;
import org.xodia.usai2d.StateManager;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

public class GameClient {
	
	private Client client;
	
	private boolean isHost;
	
	/**
	 * Tells the game whether or not the client is
	 * disconnected
	 */
	private boolean isDisconnected;
	/**
	 * Holds its own version of people logged in but is updated by the server
	 */
	private List<String> loggedInList;
	/**
	 * LoggedInList Version
	 */
	private long version;
	/**
	 * Holds the information of the game
	 */
	private List<PlayerPacket> playerInfoList;
	/**
	 * Holds the civ managers which eases the access of the civilization object for each player
	 */
	private ConcurrentHashMap<Long, CivManager> civManagerList;
	/**
	 * Version of the info
	 */
	private long infoVersion;
	/**
	 * Synchronized Boolean that sets whether it is the player's turn
	 */
	private AtomicBoolean isMyTurn;
	/**
	 * ID
	 */
	private long id;
	/**
	 * Tells the clients to update their FOV depends on the unit update
	 */
	private AtomicBoolean canUpdateFOV;
	/**
	 * Queues up the Text Chat for the players that didn't receive it
	 */
	private TextChatQueue chatQueue;
	
	public GameClient(){
		client = new Client();
		client.start();
		
		Network.register(client);
		
		client.addListener(new ThreadedListener(new Listener(){
			public void connected(Connection c){
				Client_Data data = new Client_Data();
				data.username = CivilizationAppData.getUsername();
				sendTCP(data);
				
				Client_Join join = new Client_Join();
				sendTCP(join);
			}
			
			public void received(Connection c, Object o){
				if(o instanceof Server_Join_Failed){
					// Create a dialog!
					Server_Join_Failed failed = (Server_Join_Failed) o;
					DialogFactory.createOKDialog(CivilizationAppData.gc, "Join Failed:\n" + failed.reason);
					
					Client_Leave leave = new Client_Leave();
					sendTCP(leave);
					
					return;
				}
				
				if(o instanceof Server_Join_Success){
					Server_Join_Success message = (Server_Join_Success) o;
					
					isHost = message.isHost;
					id = message.id;
					
					DialogFactory.createOKDialog(CivilizationAppData.gc, "Join Success!", new OnClickListener() {
						public void onClick(int button) {
							// Go to the next game
							StateManager.enterState(CivilizationApp.LOBBY);
						}
					});
					
					return;
				}
				
				if(o instanceof Server_Update_Lobby){
					Server_Update_Lobby update = (Server_Update_Lobby) o;
					
					synchronized(loggedInList){
						loggedInList.clear();
						
						for(String s : update.players)
							loggedInList.add(s);
						
						version++;
					}
					
					return;
				}
				
				if(o instanceof Server_Text_PubMessage){
					Server_Text_PubMessage mess = (Server_Text_PubMessage) o;
					chatQueue.addQueue(mess.name + ": \"" + mess.text + "\"");
					return;
				}
				
				if(o instanceof Server_Text_WhisperMessage){
					Server_Text_WhisperMessage mess = (Server_Text_WhisperMessage) o;
					
					if(mess.fromID == getID()){
						chatQueue.addQueue("To " + mess.to + ": \"" + mess.text + "\"");
					}else{
						chatQueue.addQueue("From " + mess.from + ": \"" + mess.text + "\"");
					}
					
					return;
				}
				
				if(o instanceof Server_Game_Start){
					Server_Game_Start data = (Server_Game_Start) o;
					
					// Set the CivManagers
					for(PlayerPacket packet : playerInfoList){
						Civilization.Civ civ = new Civilization.Civ(packet.civName, packet.civPackage.getStarterResearch(), packet.civAbility);
						civManagerList.put(packet.id, new CivManager(new Civilization(civ, packet.username, packet.id)));
					}
					
					Init_Package pkg = new Init_Package(data.seed);
					
					GameState.setInitPackage(pkg);
					
					StateManager.enterState(CivilizationApp.GAME);
					
					return;
				}
				
				if(o instanceof Server_Game_Start_Failed){
					DialogFactory.createOKDialog(CivilizationAppData.gc, ((Server_Game_Start_Failed) o).message);
					
					return;
				}
				
				if(o instanceof Server_Update_Game){
					Server_Update_Game update = (Server_Update_Game) o;
					
					synchronized(playerInfoList){
						if(playerInfoList.size() == 0){
							for(int i = 0; i < update.packets.length; i++){
								playerInfoList.add(update.packets[i]);
							}
						}else if(playerInfoList.size() != update.packets.length){
							playerInfoList.clear();
							
							for(int i = 0; i < update.packets.length; i++){
								playerInfoList.add(update.packets[i]);
							}
						}else{
							for(PlayerPacket packet : playerInfoList){
								for(PlayerPacket temp : update.packets){
									if(packet.id == temp.id){
										if(packet.version != temp.version){
											packet.mouseX = temp.mouseX;
											packet.mouseY = temp.mouseY;
											packet.civAbility = temp.civAbility;
											packet.civName = temp.civName;
											packet.civPackage = temp.civPackage;
											packet.version = temp.version;
											break;
										}
									}
								}
							}
						}
						
						infoVersion++;
					}
					
					return;
				}
				
				if(o instanceof Server_Create_Unit){
					Server_Create_Unit create = (Server_Create_Unit) o;
					
					synchronized(civManagerList){
						if(create.unitID.isNonCombatant()){
							Unit u = UnitFactory.newInstance().createUnit(create.x, create.y, create.id, create.playerID, create.unitID);
							
							if(!civManagerList.get(create.playerID).containsResearch(Research.Optic)){
								if(civManagerList.get(create.playerID).containsResearch(Research.Compass)){
									u.setUnitType(UnitType.ShallowAmphibious);
								}
							}else{
								u.setUnitType(UnitType.DeepAmphibious);
							}
							
							civManagerList.get(create.playerID).addNonCombatant(u);
						}else{
							Unit u = UnitFactory.newInstance().createUnit(create.x, create.y, create.id, create.playerID, create.unitID);
							
							if(!civManagerList.get(create.playerID).containsResearch(Research.Optic)){
								if(civManagerList.get(create.playerID).containsResearch(Research.Compass)){
									u.setUnitType(UnitType.ShallowAmphibious);
								}
							}else{
								u.setUnitType(UnitType.DeepAmphibious);
							}
							
							civManagerList.get(create.playerID).addCombatant(u);
						}
						
						if(create.playerID == getID()){
							canUpdateFOV.getAndSet(true);
						}
					}
					
					return;
				}
				
				if(o instanceof Server_Update_Unit){
					Server_Update_Unit update = (Server_Update_Unit) o;
					
					synchronized(civManagerList){
						if(update.isCombatant){
							civManagerList.get(update.playerID).getCombatantList(update.id).setX(update.x);
							civManagerList.get(update.playerID).getCombatantList(update.id).setY(update.y);
							civManagerList.get(update.playerID).getCombatantList(update.id).setCurrentMovePoints(update.currentMP);
							civManagerList.get(update.playerID).getCombatantList(update.id).setAction(update.isAction);
							civManagerList.get(update.playerID).getCombatantList(update.id).setSieging(update.isSieging);
							civManagerList.get(update.playerID).getCombatantList(update.id).setTownSiegedID(update.townSiegedID);
							civManagerList.get(update.playerID).getCombatantList(update.id).setTownSiegedPlayerID(update.townSiegedPlayerID);
							civManagerList.get(update.playerID).getCombatantList(update.id).setCurrentHealth(update.currentHealth);
							
							if(update.playerID == getID()){
								civManagerList.get(update.playerID).getCombatantList(update.id).setUpdateFOV(true);
								canUpdateFOV.getAndSet(true);
							}
						}else{
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setX(update.x);
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setY(update.y);
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setCurrentMovePoints(update.currentMP);
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setAction(update.isAction);
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setSieging(update.isSieging);
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setTownSiegedID(update.townSiegedID);
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setTownSiegedPlayerID(update.townSiegedPlayerID);
							civManagerList.get(update.playerID).getNonCombatantList(update.id).setCurrentHealth(update.currentHealth);
							
							if(update.playerID == getID()){
								civManagerList.get(update.playerID).getNonCombatantList(update.id).setUpdateFOV(true);
								canUpdateFOV.getAndSet(true);
							}
						}
					}
					
					return;
				}
				
				if(o instanceof Server_Remove_Unit){
					Server_Remove_Unit remove = (Server_Remove_Unit) o;
					
					synchronized(civManagerList){
						if(remove.isCombatant){
							civManagerList.get(remove.playerID).removeCombatant(remove.id);
						}else{
							civManagerList.get(remove.playerID).removeNonCombatant(remove.id);
						}
						
						if(remove.playerID == getID())
							canUpdateFOV.getAndSet(true);
					}
					
					return;
				}
				
				if(o instanceof Server_Add_Town){
					Server_Add_Town add = (Server_Add_Town) o;
					
					Town town = new Town(add.playerID, add.id, add.x, add.y);
					town.setName(add.name);
					town.setCapital(add.isCapital);
					civManagerList.get(add.playerID).addTown(town);
					
					if(add.playerID == getID())
						canUpdateFOV.getAndSet(true);
					
					return;
				}
				
				if(o instanceof Server_Add_Building_To_Town){
					Server_Add_Building_To_Town message = (Server_Add_Building_To_Town) o;
					
					for(Town t : civManagerList.get(message.playerID).getTownList()){
						if(t.getID() == message.townID){
							t.addBuildingToHave((Building) ProductionManager.getProductionFrom(message.prodID, true));
							break;
						}
					}
					
					return;
				}
				
				if(o instanceof Server_Add_Town_Production){
					Server_Add_Town_Production prod = (Server_Add_Town_Production) o;
					
					for(Town t : civManagerList.get(prod.playerID).getTownList()){
						if(t.getID() == prod.townID){
							if(prod.prodID == -1){
								t.getProductionManager().setProduction(null);
							}else{
								t.getProductionManager().setProduction(ProductionManager.getProductionFrom(prod.prodID, prod.isBuilding));
							}
							break;
						}
					}
					
					return;
				}
				
				if(o instanceof Server_Update_Town_Production){
					Server_Update_Town_Production prod = (Server_Update_Town_Production) o;
					
					for(Town t : civManagerList.get(prod.playerID).getTownList()){
						if(t.getID() == prod.townID){
							t.getProductionManager().incrementProductionTurn();
							break;
						}
					}
					
					return;
				}
				
				if(o instanceof Server_Update_Civ){
					Server_Update_Civ update = (Server_Update_Civ) o;
					
					civManagerList.get(update.playerID).setTotalGold(update.totalGold);
					
					return;
				}
				
				if(o instanceof Server_Update_Research){
					civManagerList.get(((Server_Update_Research) o).playerID).incrementResearch();
					
					return;
				}
				
				if(o instanceof Server_Set_Research){
					Server_Set_Research set = (Server_Set_Research) o;
					
					civManagerList.get(set.playerID).setResearch(set.research);
					
					return;
				}
				
				if(o instanceof Server_Add_Research){
					Server_Add_Research add = (Server_Add_Research) o;
					
					civManagerList.get(add.playerID).addResearch(civManagerList.get(add.playerID).getCurrentResearch());
					civManagerList.get(add.playerID).clearResearch();
					
					return;
				}
				
				if(o instanceof Server_Town_Add_Border){
					Server_Town_Add_Border border = (Server_Town_Add_Border) o;
					
					synchronized(civManagerList){
						Town t = civManagerList.get(border.playerID).getTown(border.townID);
						t.addBorder(GameState.getTileAt(border.x, border.y), border.playerID);
					}
					
					return;
				}
				
				if(o instanceof Server_Town_Add_Borders){
					Server_Town_Add_Borders border = (Server_Town_Add_Borders) o;
					
					synchronized(civManagerList){
						Town t = civManagerList.get(border.playerID).getTown(border.townID);
						
						for(int i = 0; i < border.xs.length; i++){
							t.addBorder(GameState.getTileAt(border.xs[i], border.ys[i]), border.playerID);
						}
					}
					
					return;
				}
				
				if(o instanceof Server_Update_Town){
					Server_Update_Town update = (Server_Update_Town) o;
					
					Town t = civManagerList.get(update.playerID).getTown(update.townID);
					t.setCurrentIncome(update.currentIncome);
					t.setCurrentGrowth(update.currentGrowth);
					t.setMaxGrowth(update.maxGrowth);
					t.setCurrentDefense(update.currentDefense);
					t.setSieged(update.isSieged);
					
					return;
				}
				
				if(o instanceof Server_Siege_Unit_Cancel){
					Server_Siege_Unit_Cancel cancel = (Server_Siege_Unit_Cancel) o;
					
					Unit u = civManagerList.get(cancel.playerID).getCombatantList(cancel.unitID);
					u.setAction(false);
					u.setSieging(false);
					u.setTownSiegedID(-1);
					u.setTownSiegedPlayerID(-1);
					
					return;
				}
				
				if(o instanceof Server_Remove_Town){
					Server_Remove_Town remove = (Server_Remove_Town) o;
					
					Town t = civManagerList.get(remove.playerID).getTown(remove.id);
					
					civManagerList.get(remove.playerID).removeTown(t);
					
					return;
				}
				
				if(o instanceof Server_Game_Lost){
					DialogFactory.createOKDialog(CivilizationAppData.gc, "YOU HAVE LOST!", new OnClickListener(){
						public void onClick(int button){
							StateManager.enterState(CivilizationApp.MENU);
							close();
						}
					});
					
					return;
				}
				
				if(o instanceof Server_Game_Won){
					DialogFactory.createOKDialog(CivilizationAppData.gc, "YOU HAVE WON!", new OnClickListener() {
						public void onClick(int button) {
							sendTCP(new Client_Confirmed_Winner());
							close();
						}
					});
					
					return;
				}
				
				if(o instanceof Server_Client_End_Turn){
					isMyTurn.getAndSet(false);
					
					return;
				}
				
				if(o instanceof Server_Client_Turn){
					isMyTurn.getAndSet(true);
					
					return;
				}
				
				if(o instanceof Server_New_Turn){
					Server_New_Turn turn = (Server_New_Turn) o;
					
					FadeTextManager.getInstance().addFadeText(CivilizationAppData.getScreenWidth() / 2, CivilizationAppData.getScreenHeight() / 2,
							"It is your turn!\n" + turn.currentAge + " Age\nYear " + turn.currentTurn, 3);
					
					return;
				}
				
			}
			
			public void disconnected(Connection c){
				isDisconnected = true;
				
				DialogFactory.createOKDialog(CivilizationAppData.gc, "You were disconnected...", new OnClickListener() {
					public void onClick(int button) {
						StateManager.enterState(CivilizationApp.MENU);
					}
				});
			}
		}));
		
		loggedInList = new ArrayList<String>();
		playerInfoList = new ArrayList<PlayerPacket>();
		civManagerList = new ConcurrentHashMap<Long, CivManager>();
		
		chatQueue = TextChatQueue.getInstance(5);
		
		isMyTurn = new AtomicBoolean(false);
		canUpdateFOV = new AtomicBoolean(false);
	}
	
	public boolean connect(String host, int port) throws IOException {
		try {
			client.connect(1000000000, host, port);
			return true;
		} catch (IOException e) {
			throw e;
		}
	}
	
	public void close(){
		client.close();
	}
	
	public void sendTCP(Object o){
		client.sendTCP(o);
	}
	
	public void setUpdateFOV(boolean can){
		canUpdateFOV.getAndSet(can);
	}
	
	public boolean canUpdateFOV(){
		return canUpdateFOV.get();
	}
	
	public String[] getLoggedInList(){
		synchronized(loggedInList){
			String[] copy = new String[loggedInList.size()];
			return loggedInList.toArray(copy);
		}
	}
	
	public CivManager getCivManager(long id){
		synchronized(civManagerList){
			return civManagerList.get(id);
		}
	}
	
	public long getLoggedInVersion(){
		synchronized(loggedInList){
			return version;
		}
	}
	
	public long getInfoVersion(){
		synchronized(playerInfoList){
			return infoVersion;
		}
	}
	
	public PlayerPacket[] getPlayerInfoList(){
		synchronized(playerInfoList){
			PlayerPacket[] info = new PlayerPacket[playerInfoList.size()];
			return playerInfoList.toArray(info);
		}
	}
	
	public PlayerPacket getPlayerPacket(long id){
		synchronized(playerInfoList){
			for(PlayerPacket p : playerInfoList){
				if(p.id == id){
					return p;
				}
			}
		}
		
		return null;
	}
	
	public boolean isMyTurn(){
		return isMyTurn.get();
	}
	
	public long getID(){
		return id;
	}
	
	public boolean isHost(){
		return isHost;
	}
	
	public boolean isDisconnected(){
		return isDisconnected;
	}

}
