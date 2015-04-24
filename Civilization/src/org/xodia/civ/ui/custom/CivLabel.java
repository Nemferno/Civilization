package org.xodia.civ.ui.custom;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.xodia.civ.CivilizationAppData;
import org.xodia.civ.civs.CivManager;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.net.LANServerManager;
import org.xodia.civ.ui.BasicUserInterface;
import org.xodia.civ.ui.Button;
import org.xodia.civ.ui.Dialog;
import org.xodia.civ.ui.Label;
import org.xodia.civ.ui.ModalFactory;
import org.xodia.civ.ui.Button.OnClickListener;
import org.xodia.civ.ui.layout.HorizontalLayout;
import org.xodia.civ.ui.layout.VerticalLayout;

/**
 * 
 * Labels the Civ's data
 * 
 * @author Jasper Bae
 *
 */
public class CivLabel extends BasicUserInterface{

	private Label nameLabel;
	private Label goldLabel;
	
	private Button researchButton;
	private Button menuButton;
	
	private CivManager civ;
	
	public CivLabel(final CivManager civ){
		super(0, 0, CivilizationAppData.getScreenWidth(), 20);
		
		this.civ = civ;
		
		setLayout(new HorizontalLayout());
		
		nameLabel = new Label(civ.getCivName(), 0, 0, CivilizationAppData.getScreenWidth() * 0.1f, 20);
		goldLabel = new Label(String.valueOf(civ.getTotalGold()), 0, 0, CivilizationAppData.getScreenWidth() * 0.25f, 20);
		researchButton = new Button("Research!", 0, 0, CivilizationAppData.getScreenWidth() * 0.2f, 20, new OnClickListener() {
			public void onClick() {
				new ResearchDialog(civ);
			}
		});
		menuButton = new Button("Menu", 0, 0, CivilizationAppData.getScreenWidth() * 0.25f, 20, new OnClickListener(){
			public void onClick(){
				final Dialog dialog = new Dialog(100, 99, false);
				dialog.setLayout(new VerticalLayout());
				Label pause = new Label("Pause", 0, 0, 100, 33f);
				Button resume = new Button("Resume", 0, 0, 100, 33f, new OnClickListener() {
					public void onClick() {
						dialog.selfDispose();
					}
				});
				Button quit = new Button("Leave", 0, 0, 100, 33f, new OnClickListener() {
					public void onClick() {
						ModalFactory.createYesNoDialog("Are you sure you want to quit this world?", new OnClickListener() {
							public void onClick() {
								ClientManager.getInstance().close();
								
								if(ClientManager.getInstance().isHost()){
									LANServerManager.closeServer();
								}
								
								dialog.selfDispose();
							}
						});
					}
				});
				dialog.addChild(pause);
				dialog.addChild(resume);
				dialog.addChild(quit);
			}
		});
		
		addChild(nameLabel);
		addChild(researchButton);
		addChild(goldLabel);
		addChild(menuButton);
	}
	
	public void update(){
		goldLabel.setText(String.valueOf(civ.getTotalGold()) + "( +" + civ.getTotalIncome() + " )");
		researchButton.setText(civ.getCurrentResearch() != null ? civ.getCurrentResearch().toString() + "( " + (civ.getCurrentResearch().getTurns() - civ.getResearchTurnsLeft()) + " Turns)" : "Research!");
	}
	
	public void render(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
		
		super.render(g);
	}
	
}
