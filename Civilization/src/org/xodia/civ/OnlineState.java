package org.xodia.civ;

import java.io.IOException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.net.LANServerManager;
import org.xodia.civ.net.Network;
import org.xodia.usai2d.BasicUIGameState;
import org.xodia.usai2d.Button;
import org.xodia.usai2d.Button.OnClickListener;
import org.xodia.usai2d.Dialog;
import org.xodia.usai2d.DialogFactory;
import org.xodia.usai2d.TextField;
import org.xodia.usai2d.layout.BorderLayout;

public class OnlineState extends BasicUIGameState{

	public OnlineState(int id) {
		super(id);
	}

	public void enter(final GameContainer gc, StateBasedGame game)
			throws SlickException {
		super.enter(gc, game);
		
		Button host = new Button(gc, "Host", 200, 200, 200, 200, new OnClickListener() {
			public void onClick(int button) {
				// Create LAN Server and then connect it in order to go to the lobby state
				LANServerManager.createServer();
				ClientManager.getInstance().createClient();
				
				try {
					ClientManager.getInstance().connect(LANServerManager.getIPAddress(), Network.DEFAULT_PORT);
				} catch (IOException e) {
					DialogFactory.createOKDialog(gc, "Error: " + e.getMessage());
				}
			}
		});
		addUI(host);
		
		Button join = new Button(gc, "Join", 400, 200, 200, 200, new OnClickListener() {
			public void onClick(int button) {
				final TextField edit = new TextField(gc, 0, 0, 0, 0);
				final Dialog dialog = DialogFactory.createYesNoDialog(gc, "Server Address (ip:port)", new OnClickListener(){
					public void onClick(int button){
						String text = edit.getText().trim();
						if(!text.equals("")){
							try{
								// Try to catch errors while doing this
								String ip = text.substring(0, text.indexOf(':'));
								int port = Integer.parseInt(text.substring(text.indexOf(':') + 1, text.length()));
								ClientManager.getInstance().createClient();
								ClientManager.getInstance().connect(ip, port);
							}catch(Exception e){
								DialogFactory.createOKDialog(gc, "Error: " + e.getLocalizedMessage());
							}
						}
					}
				});
				dialog.addChild(edit, BorderLayout.Direction.CENTER);
			}
		});
		addUI(join);
	}
	
	public void init(final GameContainer gc, StateBasedGame arg1)
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
