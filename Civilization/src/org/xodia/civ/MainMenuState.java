package org.xodia.civ;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.xodia.usai2d.BasicUIGameState;
import org.xodia.usai2d.Button;
import org.xodia.usai2d.Button.OnClickListener;
import org.xodia.usai2d.Dialog;
import org.xodia.usai2d.DialogFactory;
import org.xodia.usai2d.DialogManager;
import org.xodia.usai2d.Label;
import org.xodia.usai2d.StateManager;
import org.xodia.usai2d.TextField;
import org.xodia.usai2d.layout.BorderLayout;

public class MainMenuState extends BasicUIGameState{

	public MainMenuState(int id) {
		super(id);
	}
	
	public void enter(final GameContainer gc, StateBasedGame game)
			throws SlickException {
		super.enter(gc, game);
		
		Button menu = new Button(gc, "Multiplayer", 0, gc.getHeight() / 2 + 60, 200, 40,
				new OnClickListener() {
					public void onClick(int button) {
						StateManager.enterState(CivilizationApp.ONLINE);
					}
				});
		addUI(menu);
		
		Button exit = new Button(gc, "Exit Game", 0, gc.getHeight() / 2 + 120, 150, 40, 
				new OnClickListener() {
					public void onClick(int button) {
						// Create a dialog
						DialogFactory.createYesNoDialog(gc, "Do you want to quit?",
								new OnClickListener() {
									public void onClick(int button) {
										CivilizationAppData.saveAppData();
										gc.exit();
									}
								});
					}
				});
		addUI(exit);
		
		if(!CivilizationAppData.isUsernameCreated()){
			final Dialog dialog = new Dialog(gc, 250, 250, true, false);
			dialog.setLayout(new BorderLayout());
			
			final TextField edit = new TextField(gc, 0, 0, 0, 0);
			dialog.addChild(edit, BorderLayout.Direction.CENTER);
			
			Label label = new Label(gc, "Enter new username:", 0, 0, 0, 0);
			dialog.addChild(label, BorderLayout.Direction.NORTH);
			
			Button button = new Button(gc, "Enter!", 0, 0, 0, 0, new OnClickListener() {
				public void onClick(int button) {
					if(!edit.getText().trim().equals("")){
						if(edit.getText().contains(" ")){
							DialogFactory.createOKDialog(gc, "No spaces allowed!");
						}else{
							CivilizationAppData.setUsername(edit.getText().trim());
							DialogManager.getInstance().disposeModal();
						}
					}else{
						DialogFactory.createOKDialog(gc, "Enter in characters!");
					}
				}
			});
			dialog.addChild(button, BorderLayout.Direction.SOUTH);
		}
	}

	public void init(final GameContainer gc, StateBasedGame sg)
			throws SlickException {
		
	}

	public void preUpdate(GameContainer gc, int delta) throws SlickException {
		
	}

	public void preRender(GameContainer gc, Graphics g) throws SlickException {
		g.drawImage(Resource.MenuBackground, 0, 0);
	}

	public void input(Input input) throws SlickException {
		
	}

}
