package org.xodia.civ.ui.custom;

import org.xodia.civ.CivilizationAppData;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.net.Network.Client_Cancel_Siege;
import org.xodia.civ.net.Network.Client_Create_Town;
import org.xodia.civ.ui.BasicUserInterface;
import org.xodia.civ.ui.Button;
import org.xodia.civ.ui.Dialog;
import org.xodia.civ.ui.EditField;
import org.xodia.civ.ui.Label;
import org.xodia.civ.ui.Button.OnClickListener;
import org.xodia.civ.ui.ModalFactory;
import org.xodia.civ.ui.layout.BorderLayout;
import org.xodia.civ.ui.layout.HorizontalLayout;
import org.xodia.civ.units.Unit;
import org.xodia.civ.units.Unit.Ability;
import org.xodia.civ.units.Unit.CombatType;

public class NewUnitBelt extends BasicUserInterface{

	private Label stat;
	private Unit unit;
	
	public NewUnitBelt() {
		super(0, 0, 575, 25);
		
		setX(CivilizationAppData.getScreenWidth() / 2 - 575 / 2);
		setY(CivilizationAppData.getScreenHeight() - 25);
		setLayout(new HorizontalLayout());
	}
	
	public void setSelection(Unit u){
		if(u == null){
			unit = u;
			setVisible(false);
		}else{
			unit = u;
			clear();
			
			// Add default
			if(stat == null){
				stat = new Label(unit.getUnitID().name() + "[HP: " + unit.getCurrentHealth() + "/" + unit.getMaxHealth() + "][Str: " + (unit.getCombatType() == CombatType.Melee ? unit.getStrength() :
					unit.getRangeStrength()) + "][MP: " + unit.getCurrentMovePoints() + "]", 0, 0, 350, 25);
			}else{
				stat.setText(unit.getUnitID().name() + "[HP: " + unit.getCurrentHealth() + "/" + unit.getMaxHealth() + "][Str: " + (unit.getCombatType() == CombatType.Melee ? unit.getStrength() :
					unit.getRangeStrength()) + "][MP: " + unit.getCurrentMovePoints() + "]");
			}
			addChild(stat);
			
			Button skip = new Button("Skip", 0, 0, 75, 25, new OnClickListener() {
				public void onClick() {
					// Camera changes...
					// Unit skip is true
					unit.setSkipped(true);
					setSelection(null);
				}
			});
			addChild(skip);
			
			if(unit.isAction()){
				if(unit.isSieging()){
					Button cancel = new Button("Cancel", 0, 0, 75, 25, new OnClickListener() {
						public void onClick() {
							Client_Cancel_Siege cancel = new Client_Cancel_Siege();
							cancel.playerID = unit.getPlayerID();
							cancel.unitID = unit.getID();
							cancel.enemyID = unit.getTownSiegedPlayerID();
							cancel.eTownID = unit.getTownSiegedID();
							ClientManager.getInstance().send(cancel);
							
							setSelection(null);
						}
					});
					addChild(cancel);
				}
			}
			
			for(Ability ability : unit.getAbilities()){
				switch(ability){
					case SettleTown:
						
						Button settle = new Button("Settle", 0, 0, 75, 25, new OnClickListener() {
							public void onClick() {
								if(ClientManager.getInstance().isMyTurn()){
									if(unit.getCurrentMovePoints() > 0){
										final EditField field = new EditField(0, 0, 0, 0);
										field.setMaximumCharacters(15);
										Dialog d = ModalFactory.createYesNoDialog("What is your town's name?", new OnClickListener(){
											public void onClick(){
												if(!field.getText().trim().equals("")){
													Client_Create_Town create = new Client_Create_Town();
													create.playerID = ClientManager.getInstance().getID();
													create.unitID = unit.getID();
													create.x = unit.getX();
													create.y = unit.getY();
													create.tName = field.getText().trim();
													create.isCombatant = !unit.getUnitID().isNonCombatant();
													
													ClientManager.getInstance().send(create);
													
													setSelection(null);
												}else{
													ModalFactory.createOKDialog("Please input a valid name!\n");
												}
											}
										});
										d.addChild(field, BorderLayout.CENTER);
									}
								}
							}
						});
						
						addChild(settle);
						
						break;
				}
			}

			setVisible(true);
		}
	}
	
	public void update(){
		if(unit != null){
			stat.setText(unit.getUnitID().name() + "[HP: " + unit.getCurrentHealth() + "/" + unit.getMaxHealth() + "][Str: " + (unit.getCombatType() == CombatType.Melee ? unit.getStrength() :
				unit.getRangeStrength()) + "][MP: " + unit.getCurrentMovePoints() + "]");
		}
	}
	
	public Unit getSelectedUnit(){
		return unit;
	}
	
}
