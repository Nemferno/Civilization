package org.xodia.civ.ui.custom;

import org.xodia.civ.civs.CivManager;
import org.xodia.civ.civs.Civilization.Research;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.net.Network.Client_Set_Research;
import org.xodia.civ.ui.Button.OnClickListener;
import org.xodia.civ.ui.Dialog;
import org.xodia.civ.ui.Label;
import org.xodia.civ.ui.ModalFactory;
import org.xodia.civ.ui.SelectionList;

public class ResearchDialog extends Dialog{

	private CivManager manager;
	
	public ResearchDialog(CivManager manager){
		super(350, 225, false);
		
		this.manager = manager;
		
		Label eLabel = new Label("Economic", 0, 0, getWidth() / 2, 25);
		Label mLabel = new Label("Militaristic", getWidth() / 2, 0, getWidth() / 2, 25);
		addChild(eLabel);
		addChild(mLabel);
		
		addEconomic(manager);
		addMilitaristic(manager);
		
		setExitOnESC(true);
	}
	
	private void addMilitaristic(CivManager manager){
		SelectionList list = new SelectionList(175, 25, getWidth() / 2, 200);
		addChild(list);
		
		if(isAvailable(Research.Sailing)){
			list.add("Sailing", "Sailing (" + Research.Sailing.getCost() + " G)\n"
					+ Research.Sailing.getTurns() + " turns\n"
					+ "- Unlocks Schooner\n"
					+ "- Unlocks Optic"
					, new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Sailing);
				}
			});
		}
		
		if(isAvailable(Research.Optic)){
			list.add("Optic", "Optic (" + Research.Optic.getCost() + " G)\n" +
					"- Unlocks Destroyer\n" +
					"- Unlocks Water Crossing (Deep)\n" +
					"- Obsoletes Frigate",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Optic);
				}
			});
		}
		
		if(isAvailable(Research.Compass)){
			list.add("Compass", "Compass (" + Research.Compass.getCost() + " G)\n" +
					Research.Compass.getTurns() + " turns\n" +
					"- Unlocks Frigate\n" +
					"- Unlocks Water Crossing (Shallow)" +
					"- Unlocks Optic\n" +
					"- Obsoletes Schooner",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Compass);
				}
			});
		}
		
		if(isAvailable(Research.Fletching)){
			list.add("Fletching", "Fletching (" + Research.Fletching.getCost() + " G)\n" +
					Research.Fletching.getTurns() + " turns\n" +
					"- Unlocks Archer",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Fletching);
				}
			});
		}
		
		if(isAvailable(Research.Tools)){
			list.add("Tools", "Tools (" + Research.Fletching.getCost() + " G)\n" +
					Research.Tools.getTurns() + " turns\n" +
					"- Unlocks Warrior\n" +
					"- Unlocks Iron Works",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Tools);
				}
			});
		}
		
		if(isAvailable(Research.HorsebackRiding)){
			list.add("Horseback Riding", "Horseback Riding (" + Research.HorsebackRiding.getCost() + " G)\n" +
					Research.HorsebackRiding.getTurns() + " turns\n" +
					"- Unlocks Horseman",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.HorsebackRiding);
				}
			});
		}
		
		if(isAvailable(Research.IronWorks)){
			list.add("Iron Works", "Iron Works (" + Research.IronWorks.getCost() + " G)\n" +
					Research.IronWorks.getTurns() + " turns\n" +
					"- Unlocks Trebuchet\n" +
					"- Unlocks Gun Powder\n" +
					"- Unlocks Combustion\n" +
					"- Unlocks Ballistic",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.IronWorks);
				}
			});
		}
		
		if(isAvailable(Research.Combustion)){
			list.add("Combustion", "Combustion (" + Research.Combustion.getCost() + " G)\n" +
					Research.Combustion.getTurns() + " turns\n" +
					"- Unlocks Ballistic\n" +
					"- Unlocks Armor Plating",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Combustion);
				}
			});
		}
		
		if(isAvailable(Research.Ballistic)){
			list.add("Ballistic", "Ballistic (" + Research.Ballistic.getCost() + " G)\n" +
					Research.Ballistic.getTurns() + " turns\n" +
					"- Unlocks Gun Powder",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Ballistic);
				}
			});
		}
		
		if(isAvailable(Research.GunPowder)){
			list.add("Gunpowder", "Gun Powder (" + Research.GunPowder.getCost() + " G)\n" +
					Research.GunPowder.getTurns() + " turns\n" +
					"- Unlocks Rifleman\n" +
					"- Unlocks Cannon\n" +
					"- Unlocks Breach Loading\n" +
					"- Obsoletes Archer, Warrior, Horseman, Trebuchet",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.GunPowder);
				}
			});
		}
		
		if(isAvailable(Research.ArmorPlating)){
			list.add("Armor Plating", "Armor Plating (" + Research.ArmorPlating + " G)\n" +
					Research.ArmorPlating.getTurns() + " turns\n" +
					"- Unlocks Tank\n" +
					"- Unlocks Artillery\n" +
					"- Unlocks Modern Tech",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.ArmorPlating);
				}
			});
		}
		
		if(isAvailable(Research.BreachLoading)){
			list.add("Breech Loading", "Breach Loading (" + Research.BreachLoading + " G)\n" +
					Research.BreachLoading.getTurns() + " turns\n" +
					"- Unlocks Infantry\n" +
					"- Unlocks Modern Tech" +
					"- Obsoletes Infantry",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.BreachLoading);
				}
			});
		}

		if(isAvailable(Research.ModernTech)){
			list.add("Modern Tech", "Modern Tech (" + Research.ModernTech + " G)\n" +
					Research.ModernTech.getTurns() + " turns\n" +
					"- Unlocks Modern Infantry\n" +
					"- Unlocks Modern Artillery\n" +
					"- Unlocks Modern Tank\n" +
					"- Obsoletes Infantry, Tank, Artillery",
					new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.ModernTech);
				}
			});
		}
	}
	
	private void addEconomic(CivManager manager){
		SelectionList list = new SelectionList(0, 25, getWidth() / 2, 200);
		addChild(list);
		
		if(isAvailable(Research.Agriculture)){
			list.add("Agriculture", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Agriculture);
				}
			});
		}
		
		if(isAvailable(Research.Wheel)){
			list.add("Wheel", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Wheel);
				}
			});
		}
		
		if(isAvailable(Research.AnimalHusbandry)){
			list.add("Animal Husbandry", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.AnimalHusbandry);
				}
			});
		}
		
		if(isAvailable(Research.Mathematics)){
			list.add("Mathematics", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Mathematics);
				}
			});
		}
		
		if(isAvailable(Research.Calendar)){
			list.add("Calendar", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Calendar);
				}
			});
		}
		
		if(isAvailable(Research.Currency)){
			list.add("Currency", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Currency);
				}
			});
		}
		
		if(isAvailable(Research.Engineering)){
			list.add("Engineering", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Engineering);
				}
			});
		}
		
		if(isAvailable(Research.MetalCasting)){
			list.add("Metal Casting", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.MetalCasting);
				}
			});
		}
		
		if(isAvailable(Research.Banking)){
			list.add("Banking", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Banking);
				}
			});
		}
		
		if(isAvailable(Research.Machinery)){
			list.add("Machinery", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Machinery);
				}
			});
		}
		
		if(isAvailable(Research.Steel)){
			list.add("Steel", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Steel);
				}
			});
		}
		
		if(isAvailable(Research.Industrialization)){
			list.add("Industrialization", new OnClickListener() {
				public void onClick() {
					purchaseResearch(Research.Industrialization);
				}
			});
		}
		
	}
	
	private void purchaseResearch(final Research r){
		if(manager.getTotalGold() >= r.getCost()){
			ModalFactory.createYesNoDialog("Are you sure you want to research this tech?", new OnClickListener(){
				public void onClick(){
					// Send a message to Server
					Client_Set_Research re = new Client_Set_Research();
					re.playerID = ClientManager.getInstance().getID();
					re.research = r;
				
					ClientManager.getInstance().send(re);
				}
			});
		}else{
			ModalFactory.createOKDialog("Do not have enough money!");
		}
	}
	
	private boolean isAvailable(Research r){
		int index = 0;
		
		if(manager.getCurrentResearch() == r){
			return false;
		}
		
		if(!manager.containsResearch(r)){
			for(Research re : r.getRequiredList()){
				if(manager.containsResearch(re))
					index++;
			}
			
			if(index == r.getRequiredList().length){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
}
