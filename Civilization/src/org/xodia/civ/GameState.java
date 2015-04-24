package org.xodia.civ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.xodia.civ.civs.CivManager;
import org.xodia.civ.civs.Civilization.Research;
import org.xodia.civ.civs.TempCivilization;
import org.xodia.civ.map.Map;
import org.xodia.civ.map.MapGenerator.MapSeed;
import org.xodia.civ.map.Tile;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.net.Network.Client_Attack_Unit;
import org.xodia.civ.net.Network.Client_End_Turn;
import org.xodia.civ.net.Network.Client_Move_Unit;
import org.xodia.civ.net.Network.Client_Raze_Town;
import org.xodia.civ.net.Network.Client_Set_Town_Production;
import org.xodia.civ.net.Network.Client_Siege_Town;
import org.xodia.civ.net.PlayerPacket;
import org.xodia.civ.ui.custom.ChatBox;
import org.xodia.civ.ui.custom.CivLabel;
import org.xodia.civ.ui.custom.MiniMap;
import org.xodia.civ.ui.custom.NewUnitBelt;
import org.xodia.civ.ui.custom.TownStat;
import org.xodia.civ.units.ProductionManager;
import org.xodia.civ.units.ProductionManager.Building;
import org.xodia.civ.units.ProductionManager.BuildingRequirement;
import org.xodia.civ.units.ProductionManager.CivRequirement;
import org.xodia.civ.units.ProductionManager.Requirement;
import org.xodia.civ.units.ProductionManager.ResearchRequirement;
import org.xodia.civ.units.ProductionManager.TownRequirement;
import org.xodia.civ.units.Town;
import org.xodia.civ.units.Unit;
import org.xodia.civ.units.Unit.CombatType;
import org.xodia.civ.util.pathfinding.AStarPathFinder;
import org.xodia.civ.util.pathfinding.Path;
import org.xodia.civ.util.pathfinding.PathWrap;
import org.xodia.usai2d.BasicUIGameState;
import org.xodia.usai2d.Button;
import org.xodia.usai2d.Button.OnClickListener;
import org.xodia.usai2d.Label;
import org.xodia.usai2d.SelectionList;

public class GameState extends BasicUIGameState{

	private static Init_Package pkg;
	
	private static Map map;
	
	private GameCamera camera;
	private Unit centerUnit;
	
	private MiniMap miniMap;
	private NewUnitBelt unitBelt;
	
	private ChatBox chatBox;
	
	/**
	 * Sends to the server that it ended his turn
	 */
	private Button endTurnButton;
	
	private long packetInfoVersion;
	private PlayerPacket[] packets;
	
	private PathWrap unitPath;
	
	private AStarPathFinder finder;
	
	private boolean hasReset;
	
	// The town it is currently observing...
	private boolean isObservingTown;
	
	// The loc in the array so that we can switch
	// between towns...
	private int townIndex;
	
	private TownStat tState;
	private Label townLabel;
	private Label townProgLabel;
	private SelectionList townProgList;
	
	private CivLabel civLabel;
	
	// THIS IS FOR FOG OF WAR
	// For now, display BLACK if not met, GRAY if saw and left, NOTHING if seeing
	private List<TempCivilization> tCivList;
	
	// A faster why to know what tiles are visible by the units
	private List<Tile> meetList;
	
	public GameState(int id) {
		super(id);
	}
	
	public void enter(GameContainer container, StateBasedGame sg)
			throws SlickException {
		super.enter(container, sg);
		
		map = new Map(null, pkg.getSeed().getSize());
		map.generateMap(pkg.getSeed());
		
		camera = new GameCamera(CivilizationAppData.getScreenWidth(), CivilizationAppData.getScreenHeight(), map);
		
		// get the new packets
		packets = ClientManager.getInstance().getPlayerInfoList();
		packetInfoVersion = ClientManager.getInstance().getPlayerInfoVersion();
		
		miniMap = new MiniMap(map, camera, 0, 0);
		miniMap.setX(CivilizationAppData.getScreenWidth() - miniMap.getWidth());
		miniMap.setY(CivilizationAppData.getScreenHeight() - miniMap.getHeight());
		addUI(miniMap);
		
		unitBelt = new NewUnitBelt();
		unitBelt.setSelection(null);
		addUI(unitBelt);
		
		endTurnButton = new Button("End Turn", miniMap.getX(), miniMap.getY() - 50, miniMap.getWidth(), 50, new OnClickListener() {
			public void onClick() {
				if(ClientManager.getInstance().isMyTurn()){
					ClientManager.getInstance().send(new Client_End_Turn());
					hasReset = false;
				}
			}
		});
		endTurnButton.setToolTip("Ends your current turn!");
		addUI(endTurnButton);
		
		tState = new TownStat(null, 0, 20);
		tState.setVisible(false);
		townLabel = new Label("", CivilizationAppData.getScreenWidth() / 2 - 150, 20, 300, 50);
		townLabel.setVisible(false);
		townProgLabel = new Label("What to build?!", 0, CivilizationAppData.getScreenHeight() - 200, 150, 50);
		townProgLabel.setVisible(false);
		townProgList = new SelectionList(0, CivilizationAppData.getScreenHeight() - 150, 150, 150);
		townProgList.setVisible(false);
		addUI(tState);
		addUI(townLabel);
		addUI(townProgLabel);
		addUI(townProgList);
		
		civLabel = new CivLabel(ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()));
		addUI(civLabel);
		
		chatBox = new ChatBox(CivilizationAppData.getScreenWidth() - 300, civLabel.getHeight(), 300, 125);
		addUI(chatBox);
		
		finder = new AStarPathFinder(map, 100, true);
		
		Unit u = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()).getNonCombatantList()[0];
		camera.centerOnPoint(u.getX() * 32, u.getY() * 32);
		centerUnit = u;
		
		tCivList = new ArrayList<TempCivilization>();
		meetList = new ArrayList<Tile>();
		
		for(int i = 0; i < packets.length; i++){
			if(packets[i].id != ClientManager.getInstance().getID()){
				CivManager civ = ClientManager.getInstance().getCivManager(packets[i].id);
				tCivList.add(new TempCivilization(civ));
			}
		}
		
		hasReset = false;
	}
	
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.leave(container, game);
		
		removeUI(miniMap);
	}
	
	// Update Client
	// Update UI
	// Update Game
	public void preUpdate(GameContainer gc, int delta) throws SlickException {
		if(packetInfoVersion != ClientManager.getInstance().getPlayerInfoVersion()){
			packets = ClientManager.getInstance().getPlayerInfoList();
		}
		
		if(ClientManager.getInstance().isMyTurn()){
			civLabel.update();
			
			if(!hasReset){
				for(Unit u : ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()).getCombatantList()){
					if(u.hasSkipped()){
						if(!u.isAction()){
							u.setSkipped(false);
						}
					}
				}
				
				for(Unit u : ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()).getNonCombatantList()){
					if(u.hasSkipped()){
						if(!u.isAction()){
							u.setSkipped(false);
						}
					}
				}
				
				hasReset = true;
			}
		}
		
		if(isObservingTown){
			Town t = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()).getTownList()[townIndex];
			
			townProgLabel.setText((t.getProductionManager().getProduction() != null) ? t.getProductionManager().getProduction().toString() + " Left: " + t.getProductionManager().getTurnsLeft() : "WHAT TO BUILD?!");
		}else{
			unitBelt.update();
		}
		
		chatBox.update();
		
		if(centerUnit.hasSkipped()){
			nextUnitCamera();
			centerUnit.setSkipped(false);
		}
		
		// Update Fog of War
		updateFOV();
	}

	public void preRender(GameContainer gc, Graphics g) throws SlickException {
		camera.transform(g);
		
		for(int i = 0; i < map.getTileList().size(); i++){
			int x = (int) (camera.getCameraX() / 32);
			int y = (int) (camera.getCameraY() / 32);
			int h = (int) (camera.getScreenHeight() / 32) + 1;
			int w = (int) (camera.getScreenWidth() / 32);
			if(map.getTileList().get(i).getX() >= x && map.getTileList().get(i).getX() <= x + w &&
				map.getTileList().get(i).getY() >= y && map.getTileList().get(i).getY() <= y + h){
				map.getTileList().get(i).render(0, 0, g);
			}
		}
		
		for(int i = 0; i < packets.length; i++){
			// Get the civilization and render out the units towns first, then combatants, then noncombatants
			CivManager civ = ClientManager.getInstance().getCivManager(packets[i].id);
			
			for(Town t : civ.getTownList()){
				Tile onTile = map.getTileAt(t.getX(), t.getY());

				// For borders, all civilizations needs to know its current border expansion, but really?
				for(Tile tile : t.getBorderList()){
					if(tile.getFOVStatus() == Tile.MEET_STATUS || tile.getFOVStatus() == Tile.MET_STATUS){
						g.setColor(new Color(255, 112, 128, 93));
						g.fillRect(tile.getX() * 32, tile.getY() * 32, 32, 32);
						continue;
					}
				}
				
				if(onTile.getFOVStatus() == Tile.MEET_STATUS){
					t.render(g);
					
					for(TempCivilization c : tCivList){
						if(c.getCivManager() == civ){
							c.update(t);
						}
					}
				}else if(onTile.getFOVStatus() == Tile.MET_STATUS){
					for(TempCivilization c : tCivList){
						if(c.getCivManager() == civ){
							c.render(t, g);
						}
					}
				}
			}
			
			for(Unit u : civ.getCombatantList()){
				if(u.getX() * 32 >= camera.getCameraX() && u.getX() * 32 <= camera.getCameraX() + camera.getScreenWidth() &&
					u.getY() * 32 >= camera.getCameraY() && u.getY() * 32 <= camera.getCameraY() + camera.getScreenHeight()){
					if(map.getTileAt(u.getX(), u.getY()).getFOVStatus() == Tile.MEET_STATUS)
						u.render(g);
				}
			}
			
			for(Unit u : civ.getNonCombatantList()){
				if(u.getX() * 32 >= camera.getCameraX() && u.getX() * 32 <= camera.getCameraX() + camera.getScreenWidth() &&
					u.getY() * 32 >= camera.getCameraY() && u.getY() * 32 <= camera.getCameraY() + camera.getScreenHeight()){
					if(map.getTileAt(u.getX(), u.getY()).getFOVStatus() == Tile.MEET_STATUS)
						u.render(g);
				}
			}
		}
		
		if(unitPath != null){
			int prevNum = 0;
			int x = 0, y = 0;
			int turn = 0;
			Color preColor = null;
			
			for(int j = 0; j < unitPath.getList().size(); j++){
				int[] data = unitPath.getList().get(j);
				x = data[1];
				y = data[2];
				turn = data[0];
				
				Color tileColor = null;
				
				if(unitBelt.getSelectedUnit().getCurrentMovePoints() > 0 && turn == 1 && ClientManager.getInstance().isMyTurn())
					tileColor = Color.blue;
				else
					tileColor = Color.red;
				
				// Change the color if there is an enemy on that tile!
				for(int i = 0; i < packets.length; i++){
					CivManager civ = ClientManager.getInstance().getCivManager(packets[i].id);
					
					boolean isTargeted = false;
					
					for(Town t : civ.getTownList()){
						if(t.getX() == x && t.getY() == y){
							tileColor = Color.magenta;
							isTargeted = true;
							break;
						}
					}
					
					if(isTargeted){
						break;
					}
					
					for(Unit u : civ.getCombatantList()){
						if(u.getX() == x && u.getY() == y){
							tileColor = Color.magenta;
							isTargeted = true;
							break;
						}
					}
					
					if(isTargeted){
						break;
					}
					
					for(Unit u : civ.getNonCombatantList()){
						if(u.getX() == x && u.getY() == y){
							tileColor = Color.magenta;
							isTargeted = true;
							break;
						}
					}
					
					if(isTargeted){
						break;
					}
				}
				
				g.setColor(tileColor);
				g.drawRect(x * 32, y * 32, 32, 32);
				
				if(prevNum == 0){
					prevNum = turn;
					preColor = tileColor;
				}else if(prevNum != turn){
					g.setColor(preColor);
					g.drawString("" + prevNum, unitPath.getList().get(j - 1)[1] * 32, unitPath.getList().get(j - 1)[2] * 32);
					prevNum = turn;
					preColor = tileColor;
				}
				
				if(j == unitPath.getList().size() - 1){
					g.setColor(tileColor);
					g.drawString("" + unitPath.getList().get(j)[0], unitPath.getList().get(j)[1] * 32, unitPath.getList().get(j)[2] * 32);
				}
				
			}
		}
		
		camera.detransform(g);
	}
	
	private void updateFOV(){
		if(ClientManager.getInstance().canUpdateFOV()){
			CivManager civ = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID());
			
			for(Tile i : meetList){
				i.setFOVStatus(Tile.MET_STATUS);
			}
			
			meetList.clear();
			
			for(Unit u : civ.getNonCombatantList()){
				// Get the sight range
				int sight = u.getSight();
				for(int x = u.getX() - sight; x < u.getX() + sight + 1; x++){
					for(int y = u.getY() - sight; y < u.getY() + sight + 1; y++){
						Tile t = map.getTileAt(x, y);
						if(t != null && t.getFOVStatus() != Tile.MEET_STATUS){
							meetList.add(t);
							t.setFOVStatus(Tile.MEET_STATUS);
						}
					}
				}
			}
			
			for(Unit u : civ.getCombatantList()){
				// Get the sight range
				int sight = u.getSight();
				for(int x = u.getX() - sight; x < u.getX() + sight + 1; x++){
					for(int y = u.getY() - sight; y < u.getY() + sight + 1; y++){
						Tile t = map.getTileAt(x, y);
						if(t != null && t.getFOVStatus() != Tile.MEET_STATUS){
							meetList.add(t);
							t.setFOVStatus(Tile.MEET_STATUS);
						}
					}
				}
			}
			
			for(Town u : civ.getTownList()){
				// Get the sight range
				int sight = u.getSight();
				for(int x = u.getX() - sight; x < u.getX() + sight + 1; x++){
					for(int y = u.getY() - sight; y < u.getY() + sight + 1; y++){
						Tile t = map.getTileAt(x, y);
						if(t != null && t.getFOVStatus() != Tile.MEET_STATUS){
							meetList.add(t);
							t.setFOVStatus(Tile.MEET_STATUS);
						}
					}
				}
			}
		
			miniMap.setMap(map);
			
			ClientManager.getInstance().setUpdateFOV(false);
		}
	}
	
	public static void setInitPackage(Init_Package pkg){
		GameState.pkg = pkg;
	}

	public void input(Input input) throws SlickException {
		if(!isObservingTown){
			if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON) && !isMouseOnUI()){
				CivManager civ = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID());
				
				boolean hasSelectedUnit = false;
				
				for(Unit u : civ.getNonCombatantList()){
					if(input.getMouseX() + camera.getCameraX() >= u.getX() * 32 && input.getMouseX() + camera.getCameraX() <= u.getX() * 32 + 32 &&
						input.getMouseY() + camera.getCameraY() >= u.getY() * 32 && input.getMouseY() + camera.getCameraY() <= u.getY() * 32 + 32){
						unitBelt.setSelection(u);
						centerUnit = u;
						hasSelectedUnit = true;
						break;
					}
				}
				
				for(Unit u : civ.getCombatantList()){
					if(input.getMouseX() + camera.getCameraX() >= u.getX() * 32 && input.getMouseX() + camera.getCameraX() <= u.getX() * 32 + 32 &&
						input.getMouseY() + camera.getCameraY() >= u.getY() * 32 && input.getMouseY() + camera.getCameraY() <= u.getY() * 32 + 32){
						unitBelt.setSelection(u);
						centerUnit = u;
						hasSelectedUnit = true;
						break;
					}
				}
				
				if(!hasSelectedUnit){
					for(int i = 0; i < civ.getTownList().length; i++){
						final Town t = civ.getTownList()[i];
						
						if(input.getMouseX() + camera.getCameraX() >= t.getX() * 32 && input.getMouseX() + camera.getCameraX() <= t.getX() * 32 + 32 &&
							input.getMouseY() + camera.getCameraY() >= t.getY() * 32 && input.getMouseY() + camera.getCameraY() <= t.getY() * 32 + 32){
							isObservingTown = true;
							unitBelt.setSelection(null);
							townIndex = i;
							tState.setTown(t);
							tState.setVisible(true);
							townLabel.setText(((t.isCapital()) ? "CAPITAL: " : "") + t.getName());
							townLabel.setVisible(true);
							townProgLabel.setText((t.getProductionManager().getProduction() != null) ? t.getProductionManager().getProduction().toString() + " Left: " + t.getProductionManager().getTurnsLeft() : "WHAT TO BUILD?!");
							townProgLabel.setVisible(true);
							
							townProgList.clear();
							
							townProgList.add("<<Building>>", new OnClickListener(){ public void onClick(){} });
							
							for(final Building b : ProductionManager.Building.values()){
								boolean isRequirementMet = true;
								
								for(Requirement r : b.getRequirements()){
									if(r instanceof TownRequirement){
										switch((TownRequirement) r){
										case TownIsShore:
											if(map.isShore(t.getX(), t.getY())){
												isRequirementMet = true;
											}else{
												isRequirementMet = false;
											}
											
											break;
										}
									}else if(r instanceof ResearchRequirement){
										ResearchRequirement research = (ResearchRequirement) r;
										
										boolean hasRequirement = true;
										
										for(Research re : research.getResearchList()){
											boolean hasIt = false;
											for(Research rre : civ.getHaveResearchList()){
												if(rre == re){
													hasIt = true;
													break;
												}
											}
											
											if(!hasIt){
												hasRequirement = false;
												break;
											}
										}
										
										if(!hasRequirement)
											isRequirementMet = false;
									}else if(r instanceof CivRequirement){
										switch((CivRequirement) r){
										default:
											isRequirementMet = false;
											break;
										}
									}else if(r instanceof BuildingRequirement){
										
									}
									
									if(!isRequirementMet)
										break;
								}
								
								if(isRequirementMet){
									townProgList.add(b.toString(), b.getDescription(), new OnClickListener() {
										public void onClick() {
											Client_Set_Town_Production set = new Client_Set_Town_Production();
											set.isBuilding = true;
											set.prodID = b.getID();
											set.townID = t.getID();
											set.playerID = ClientManager.getInstance().getID();
											ClientManager.getInstance().send(set);
										}
									});
								}
							}

							townProgList.add("<<Unit>>", new OnClickListener(){ public void onClick(){} });
							
							for(final org.xodia.civ.units.ProductionManager.Unit u : org.xodia.civ.units.ProductionManager.Unit.values()){
								if(!u.isObsolete()){
									boolean isRequirementMet = true;
									
									for(Requirement r : u.getRequirements()){
										if(r instanceof TownRequirement){
											switch((TownRequirement) r){
											case TownIsShore:
												if(map.isShore(t.getX(), t.getY())){
													isRequirementMet = true;
												}else{
													isRequirementMet = false;
												}
												
												break;
											default:
												isRequirementMet = false;
												break;
											}
										}else if(r instanceof ResearchRequirement){
											ResearchRequirement research = (ResearchRequirement) r;
											
											boolean hasRequirement = true;
											
											for(Research re : research.getResearchList()){
												boolean hasIt = false;
												for(Research rre : civ.getHaveResearchList()){
													if(rre == re){
														hasIt = true;
														break;
													}
												}
												
												if(!hasIt){
													hasRequirement = false;
													break;
												}
											}
											
											if(!hasRequirement)
												isRequirementMet = false;
										}else if(r instanceof CivRequirement){
											switch((CivRequirement) r){
											default:
												isRequirementMet = false;
												break;
											}
										}else if(r instanceof BuildingRequirement){
											
										}
										
										if(!isRequirementMet)
											break;
									}
									
									if(isRequirementMet){
										townProgList.add(u.toString(), u.getDescription(), new OnClickListener() {
											public void onClick() {
												Client_Set_Town_Production set = new Client_Set_Town_Production();
												set.isBuilding = false;
												set.prodID = u.getIntID();
												set.townID = t.getID();
												set.playerID = ClientManager.getInstance().getID();
												ClientManager.getInstance().send(set);
											}
										});
									}
								}
							}
							
							townProgList.setVisible(true);
							break;
						}
					}
					
					// If it does not hit the belt, then we have to not set it visible
					if(!(input.getMouseX() >= unitBelt.getX() && input.getMouseX() <= unitBelt.getX() + unitBelt.getWidth() &&
						input.getMouseY() >= unitBelt.getY() && input.getMouseY() <= unitBelt.getY() + unitBelt.getHeight())){
						unitBelt.setSelection(null);
					}
				}
			}
			
			if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) && !isMouseOnUI()){
				if(unitBelt.getSelectedUnit() != null){
					// If we have selected a unit and we pressed to a tile, it will go to that position
					int tx = ((int) (input.getMouseX() + camera.getCameraX()) / 32);
					int ty = ((int) (input.getMouseY() + camera.getCameraY()) / 32);
					
					Path path = finder.findPath(unitBelt.getSelectedUnit().getUnitType(), unitBelt.getSelectedUnit().getX(), unitBelt.getSelectedUnit().getY(), tx, ty);
					if(path != null){
						unitPath = new PathWrap(unitBelt.getSelectedUnit(), path);
					}
				}
			}
			
			if(input.isKeyPressed(Input.KEY_SPACE)){
				nextUnitCamera();
			}
		}else{
			if(input.isKeyPressed(Input.KEY_ESCAPE)){
				isObservingTown = false;
				tState.setVisible(false);
				townLabel.setVisible(false);
				townProgLabel.setVisible(false);
				townProgList.setVisible(false);
			}
		}
		
		if(input.isKeyDown(Input.KEY_LEFT) && !isKeyOnUI()){
			camera.setCameraX(camera.getCameraX() - 32.5f);
		}else if(input.isKeyDown(Input.KEY_RIGHT) && !isKeyOnUI()){
			camera.setCameraX(camera.getCameraX() + 32.5f);
		}else if(input.isKeyDown(Input.KEY_DOWN) && !isKeyOnUI()){
			camera.setCameraY(camera.getCameraY() + 32.5f);
		}else if(input.isKeyDown(Input.KEY_UP) && !isKeyOnUI()){
			camera.setCameraY(camera.getCameraY() - 32.5f);
		}
	}
	
	public void mouseReleased(int button, int x, int y) {
		super.mouseReleased(button, x, y);
		
		if(ClientManager.getInstance().isMyTurn()){
			if(button == Input.MOUSE_RIGHT_BUTTON){
				if(unitBelt.getSelectedUnit() != null && unitPath != null){
					if(unitBelt.getSelectedUnit().getCurrentMovePoints() > 0){
						// We have to see if where it's going is attacking!
						
						boolean canAttack = true;
						Unit enemy = null;
						long enemyID = -1;
						
						boolean onBorder = false;
						
						int prevX = 0, prevY = 0;
						
						List<int[]> unitP = unitPath.getTilesAtFirstTurn();
						if(unitP.size() > 0){
							int[] tile = unitPath.getTilesAtFirstTurn().get(unitPath.getTilesAtFirstTurn().size() - 1);
							int tx = tile[0];
							int ty = tile[1];
							
							for(int i2 = 0; i2 < packets.length; i2++){
								if(packets[i2].id != ClientManager.getInstance().getID()){
									CivManager civ = ClientManager.getInstance().getCivManager(packets[i2].id);
									
									for(Town t : civ.getTownList()){
										boolean isFound = false;
										
										for(Tile tt : t.getBorderList()){
											if(tt.getX() == tx && tt.getY() == ty){
												onBorder = true;
												isFound = true;
												enemyID = packets[i2].id;
												break;
											}
										}
										
										if(t.getX() == tx && t.getY() == ty){
											enemy = t;
											enemyID = packets[i2].id;
											break;
										}
										
										if(isFound)
											break;
									}
									
									for(Unit u : civ.getCombatantList()){
										if(tx == u.getX() &&
											ty == u.getY()){
											enemy = u;
											enemyID = packets[i2].id;
											break;
										}
									}
									
									if(enemy != null)
										break;
									
									for(Unit u : civ.getNonCombatantList()){
										if(tx == u.getX() &&
											ty == u.getY()){
											enemy = u;
											enemyID = packets[i2].id;
											break;
										}
									}
									
									if(enemy != null)
										break;
								}
							}
							
							// When we have an enemy, we have to make sure it is an ally or neutral to warn the player that making this decision will declare
							// war on the unit's country
							if(enemy != null){
								// This is for Melee Units
								if(unitBelt.getSelectedUnit().getCombatType() == CombatType.Melee){
									int index = unitPath.getTilesAtFirstTurn().size() - 1;
									
									if(index == 0){
										prevX = unitBelt.getSelectedUnit().getX();
										prevY = unitBelt.getSelectedUnit().getY();
									}else if(index > 0){
										prevX = unitPath.getTilesAtFirstTurn().get(index - 1)[0];
										prevY = unitPath.getTilesAtFirstTurn().get(index - 1)[1];
									}else{
										prevX = unitBelt.getSelectedUnit().getX();
										prevY = unitBelt.getSelectedUnit().getY();
									}
								}else if(unitBelt.getSelectedUnit().getCombatType() == CombatType.Range){
									// We have to see if it is in range with the unit!
									// To do this, we have to create a path to the enemy and see if it
									// Takes less than 1 turn. If it does, then wallah!
									Path p = finder.findPath(unitBelt.getSelectedUnit().getUnitType(), unitBelt.getSelectedUnit().getX(), unitBelt.getSelectedUnit().getY(), enemy.getX(), enemy.getY());
									// We need to find the true range (without any interference)
									int old = unitBelt.getSelectedUnit().getCurrentMovePoints();
									unitBelt.getSelectedUnit().setCurrentMovePoints(unitBelt.getSelectedUnit().getMaxMovePoints());
									PathWrap wrap = new PathWrap(unitBelt.getSelectedUnit(), p);
									unitBelt.getSelectedUnit().setCurrentMovePoints(old);
									
									if(wrap.getNumOfTurns() != 1){
										// We cannot fight...
										canAttack = false;
									}
								}else if(unitBelt.getSelectedUnit().getCombatType() == CombatType.Artillery){
									if(!(enemy instanceof Town)){
										canAttack = false;
									}
								}
							}
							
							if(enemy == null){
								if(onBorder){
									Client_Move_Unit message = new Client_Move_Unit();
									message.playerID = ClientManager.getInstance().getID();
									message.id = unitBelt.getSelectedUnit().getID();
									message.tx = unitPath.getFirstTurn()[0];
									message.ty = unitPath.getFirstTurn()[1];
									message.isCombatant = !unitBelt.getSelectedUnit().getUnitID().isNonCombatant();
									message.totalCost = unitPath.getTotalCostInFirstTurn();
									
									ClientManager.getInstance().send(message);
									
									unitPath = null;
								}else{
									Client_Move_Unit message = new Client_Move_Unit();
									message.playerID = ClientManager.getInstance().getID();
									message.id = unitBelt.getSelectedUnit().getID();
									message.tx = unitPath.getFirstTurn()[0];
									message.ty = unitPath.getFirstTurn()[1];
									message.isCombatant = !unitBelt.getSelectedUnit().getUnitID().isNonCombatant();
									message.totalCost = unitPath.getTotalCostInFirstTurn();
									
									ClientManager.getInstance().send(message);
									
									unitPath = null;
								}
							}else{
								if(canAttack){
									if(!(enemy instanceof Town)){
										Client_Attack_Unit message = new Client_Attack_Unit();
										message.attPlayerID = enemyID;
										message.attUnitID = enemy.getID();
										message.isAttCombatant = !enemy.getUnitID().isNonCombatant();
										message.isCombatant = !unitBelt.getSelectedUnit().getUnitID().isNonCombatant();
										message.playerID = ClientManager.getInstance().getID();
										message.prevX = prevX;
										message.prevY = prevY;
										message.combat = unitBelt.getSelectedUnit().getCombatType();
										message.unitID = unitBelt.getSelectedUnit().getID();
										
										ClientManager.getInstance().send(message);
									}else{
										Town t = (Town) enemy;
										
										if(t.getCurrentDefense() > 0){
											Client_Siege_Town message = new Client_Siege_Town();
											message.enemyID = enemyID;
											message.eTownID = enemy.getID();
											message.playerID = ClientManager.getInstance().getID();
											message.unitID = unitBelt.getSelectedUnit().getID();
											message.prevX = prevX;
											message.prevY = prevY;
											
											ClientManager.getInstance().send(message);
										}else{
											if(unitBelt.getSelectedUnit().getCombatType() == CombatType.Melee){
												Client_Raze_Town raze = new Client_Raze_Town();
												raze.enemyID = enemyID;
												raze.eTownID = enemy.getID();
												raze.playerID = ClientManager.getInstance().getID();
												raze.unitID = unitBelt.getSelectedUnit().getID();
												
												ClientManager.getInstance().send(raze);
											}else{
												ModalFactory.createOKDialog("ONLY MELEE UNITS CAN RAZE TOWNS!");
											}
										}
									}
	
									unitPath = null;
								}
							}
						}else{
							unitPath = null;
						}
					}else{
						unitPath = null;
					}
				}
			}
		}else{
			if(button == Input.MOUSE_RIGHT_BUTTON){
				unitPath = null;
			}
		}
	}
	
	private void nextUnitCamera(){
		Unit[] combList = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()).getCombatantList();
		Unit[] nonCombList = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()).getNonCombatantList();
		Town[] townList = ClientManager.getInstance().getCivManager(ClientManager.getInstance().getID()).getTownList();
		List<Unit> units = new ArrayList<Unit>();
		units.addAll(Arrays.asList(combList));
		units.addAll(Arrays.asList(nonCombList));
		units.addAll(Arrays.asList(townList));
		
		for(int i = 0; i < units.size(); i++){
			if(centerUnit == units.get(i)){
				if(i == units.size() - 1){
					centerUnit = units.get(0);
					camera.centerOnObject(centerUnit);
					break;
				}else{
					centerUnit = units.get(i + 1);
					camera.centerOnObject(centerUnit);
					break;
				}
			}
		}
	}
	
	public static Tile getTileAt(int x, int y){
		return map.getTileAt(x, y);
	}
	
	public static int getWidth(){
		return map.getWidth();
	}
	
	public static int getHeight(){
		return map.getHeight();
	}
	
	/**
	 * 
	 * Contains all the necessities needed to create the game state!
	 * These include:
	 * - Map Seed for Rendering
	 * - Civilization List
	 * - 
	 * 
	 * @author Jasper Bae
	 *
	 */
	public static class Init_Package {
		
		private MapSeed seed;

		public Init_Package(MapSeed seed){
			this.seed = seed;
		}
		
		public MapSeed getSeed(){
			return seed;
		}
		
	}

	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {}
	
}
