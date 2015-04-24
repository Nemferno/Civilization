package org.xodia.civ;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class CivilizationApp extends StateBasedGame{

	public static final int MENU = 1,
							LOBBY = 2,
							ONLINE = 3,
							GAME = 4;
	
	public static Font DEFAULT_FONT;
	
	public CivilizationApp() {
		super("XodiaEntertainment: Civilization");
	}

	public void initStatesList(GameContainer gc) throws SlickException {
		Resource.load();
		DEFAULT_FONT = gc.getDefaultFont();
		CivilizationAppData.setGameObjects(gc);
		
		addState(new MainMenuState(MENU));
		addState(new OnlineState(ONLINE));
		addState(new LobbyState(LOBBY));
		//addState(new GameState(GAME));
		enterState(MENU);
	}
	
	public static void main(String[] args) throws SlickException {
		CivilizationAppData.loadAppData();
		AppGameContainer app = new AppGameContainer(new CivilizationApp(), CivilizationAppData.getScreenWidth(), CivilizationAppData.getScreenHeight(), false);
		app.setTargetFrameRate(60);
		app.setAlwaysRender(true);
		app.start();
	}

}
