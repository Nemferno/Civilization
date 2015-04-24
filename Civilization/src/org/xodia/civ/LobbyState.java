package org.xodia.civ;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.xodia.civ.civs.Civilization.Civ.CivAbility;
import org.xodia.civ.civs.Civilization.Civ.CivPackage;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.net.Network.Client_Change_Civ;
import org.xodia.civ.net.Network.Client_Leave;
import org.xodia.civ.net.Network.Client_Start;
import org.xodia.civ.net.PlayerPacket;
import org.xodia.usai2d.BasicUIGameState;
import org.xodia.usai2d.Button;
import org.xodia.usai2d.Button.OnClickListener;
import org.xodia.usai2d.SelectionList;
import org.xodia.usai2d.TextField;

public class LobbyState extends BasicUIGameState{

	/**
	 * The current version of the loggedin from the client's loggedin
	 */
	private long loggedInVersion;
	/**
	 * The list of players that are logged in
	 */
	private String[] loggedIn;
	/**
	 * The current version of the game
	 */
	private long packetVersion;
	/**
	 * The packet list
	 */
	private PlayerPacket[] packets;
	
	private Button leave;
	private Button start;
	
	private TextField nameField;
	private SelectionList packList;
	private SelectionList abiList;
	private Button submit;
	
	public LobbyState(int id) {
		super(id);
	}

	public void enter(GameContainer gc, StateBasedGame game)
			throws SlickException {
		super.enter(gc, game);
		
		packets = null;
		loggedIn = null;
		loggedInVersion = -1;
		packetVersion = -1;
		
		nameField = new TextField(gc, CivilizationAppData.getScreenWidth() - 185, 110, 175, 40);
		registerKeyUI(nameField);
		nameField.setMaxCharacters(26);
		
		packList = new SelectionList(gc, CivilizationAppData.getScreenWidth() - 185, 200, 175, 40, 3);
		for(CivPackage p : CivPackage.values()){
			packList.addItem(p.getName(), 
					"Package Includes:\n" +
					"- " + p.getStarterResearch()[0] + "\n" +
					"- " + p.getStarterResearch()[1]);
		}
		
		abiList = new SelectionList(gc, CivilizationAppData.getScreenWidth() - 185, 290, 175, 40);
		for(CivAbility a : CivAbility.values()){
			abiList.addItem(a.name().replace('_', ' '), a.getDescription());
		}
		
		submit = new Button(gc, "Submit Civ", CivilizationAppData.getScreenWidth() - 185, 340, 175, 40, new OnClickListener() {
			public void onClick(int button) {
				if(nameField.getText() != null && !nameField.getText().trim().equals("") && abiList.getSelected() != null && packList.getSelected() != null){
					CivAbility ab = null;
					CivPackage pck = null;
					
					for(CivAbility civ : CivAbility.values()){
						if(civ.name().replace('_', ' ').equals(abiList.getSelected())){
							ab = civ;
							break;
						}
					}
					
					for(CivPackage pack : CivPackage.values()){
						if(pack.getName().equals(packList.getSelected())){
							pck = pack;
							break;
						}
					}
					
					if(ab != null && pck != null){
						Client_Change_Civ change = new Client_Change_Civ();
						change.abil = ab;
						change.pack = pck;
						change.civName = nameField.getText().trim();
						ClientManager.getInstance().send(change);
					}
				}
			}
		});
		
		leave = new Button(gc, "Leave", CivilizationAppData.getScreenWidth() - 150, 10, 125, 40, new OnClickListener(){
			public void onClick(int button){
				Client_Leave leaver = new Client_Leave();
				ClientManager.getInstance().send(leaver);
			}
		});
		
		addUI(submit);
		addUI(abiList);
		addUI(packList);
		addUI(nameField);
		addUI(leave);
		
		if(ClientManager.getInstance().isHost()){
			if(start == null){
				start = new Button(gc, "Start", CivilizationAppData.getScreenWidth() - 300, 10, 125, 40, new OnClickListener(){
					public void onClick(int button){
						// START
						Client_Start start = new Client_Start();
						ClientManager.getInstance().send(start);
					}
				});
			}
			
			addUI(start);
		}
			
	}
	
	public void init(GameContainer gc, StateBasedGame sg)
			throws SlickException {
		nameField = new TextField(gc, CivilizationAppData.getScreenWidth() - 185, 110, 175, 40);
		nameField.setMaxCharacters(26);
		
		packList = new SelectionList(gc, CivilizationAppData.getScreenWidth() - 185, 200, 175, 40);
		for(CivPackage p : CivPackage.values()){
			packList.addItem(p.getName(), 
					"Package Includes:\n" +
					"- " + p.getStarterResearch()[0] + "\n" +
					"- " + p.getStarterResearch()[1]);
		}
		
		abiList = new SelectionList(gc, CivilizationAppData.getScreenWidth() - 185, 290, 175, 40);
		for(CivAbility a : CivAbility.values()){
			abiList.addItem(a.name().replace('_', ' '), a.getDescription());
		}
		
		submit = new Button(gc, "Submit Civ", CivilizationAppData.getScreenWidth() - 185, 340, 175, 40, new OnClickListener() {
			public void onClick(int button) {
				if(nameField.getText() != null && !nameField.getText().trim().equals("") && abiList.getSelected() != null && packList.getSelected() != null){
					CivAbility ab = null;
					CivPackage pck = null;
					
					for(CivAbility civ : CivAbility.values()){
						if(civ.name().replace('_', ' ').equals(abiList.getSelected())){
							ab = civ;
							break;
						}
					}
					
					for(CivPackage pack : CivPackage.values()){
						if(pack.getName().equals(packList.getSelected())){
							pck = pack;
							break;
						}
					}
					
					if(ab != null && pck != null){
						Client_Change_Civ change = new Client_Change_Civ();
						change.abil = ab;
						change.pack = pck;
						change.civName = nameField.getText().trim();
						ClientManager.getInstance().send(change);
					}
				}
			}
		});
		
		leave = new Button(gc, "Leave", CivilizationAppData.getScreenWidth() - 150, 10, 125, 40, new OnClickListener(){
			public void onClick(int button){
				Client_Leave leaver = new Client_Leave();
				ClientManager.getInstance().send(leaver);
			}
		});
		
		addUI(submit);
		addUI(abiList);
		addUI(packList);
		addUI(nameField);
		addUI(leave);
	}

	public void preUpdate(GameContainer gc, int delta) throws SlickException {
		// Checks the loggedInVersion
		if(loggedInVersion != ClientManager.getInstance().getLoggedInVersion()){
			// Get the correct version
			loggedInVersion = ClientManager.getInstance().getLoggedInVersion();
			loggedIn = ClientManager.getInstance().getLoggedInList();
		}
		
		if(packetVersion != ClientManager.getInstance().getPlayerInfoVersion()){
			packets = ClientManager.getInstance().getPlayerInfoList();
			packetVersion = ClientManager.getInstance().getPlayerInfoVersion();
		}
	}

	public void preRender(GameContainer gc, Graphics g) throws SlickException {
		g.drawImage(Resource.MenuBackground, 0, 0);
		
		if(loggedIn != null){
			for(int i = 0; i < loggedIn.length; i++){
				g.setColor(Color.white);
				g.drawString(loggedIn[i], 10, 250 + i * 50);
				
				if(packets != null){
					if(packets[i].civName != null && packets[i].civAbility != null && packets[i].civPackage != null){
						g.setColor(Color.orange);
						g.drawString("Ready!", g.getFont().getWidth(loggedIn[i]) + 20, 250 + i * 50);
					}
				}
			}
		}
		
		g.setColor(Color.yellow);
		g.drawString("Civilization Name", nameField.getX(), nameField.getY() - g.getFont().getLineHeight());
		g.drawString("Research Package", packList.getX(), packList.getY() - g.getFont().getLineHeight());
		g.drawString("Civilization Ability", abiList.getX(), abiList.getY() - g.getFont().getLineHeight());
	}

	public void input(Input input) throws SlickException {
		
	}

}
