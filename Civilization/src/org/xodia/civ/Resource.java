package org.xodia.civ;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

public class Resource {

	private Resource(){}
	
	public static Image ArcherUnit,
						ArtilleryUnit,
						CannonUnit,
						DestroyerUnit,
						FrigateUnit,
						HorsemanUnit,
						InfantryUnit,
						ModernArtilleryUnit,
						ModernInfantryUnit,
						ModernTankUnit,
						RiflemanUnit,
						SchoonerUnit,
						SettlerUnit,
						TankUnit,
						TrebuchetUnit,
						WarriorUnit;
	
	public static Image MenuBackground;
	
	public static Image UI_MultiHButton,
						UI_MultiNButton,
						UI_MultiPButton,
						UI_ExitHButton,
						UI_ExitPButton,
						UI_ExitNButton;
	
	public static void load() throws SlickException {
		ArcherUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/ArcherUnit.png"), "Archer Unit", false);
		ArtilleryUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/ArtilleryUnit.png"), "Artillery Unit", false);
		CannonUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/CannonUnit.png"), "Cannon Unit", false);
		DestroyerUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/DestroyerUnit.png"), "Destroyer Unit", false);
		FrigateUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/FrigateUnit.png"), "Frigate Unit", false);
		HorsemanUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/HorsemanUnit.png"), "Horseman Unit", false);
		InfantryUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/InfantryUnit.png"), "Infantry Unit", false);
		ModernArtilleryUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/ModernArtilleryUnit.png"), "Modern Artillery Unit", false);
		ModernInfantryUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/ModernInfantryUnit.png"), "Modern Infantry Unit", false);
		ModernTankUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/ModernTankUnit.png"), "Modern Tank Unit", false);
		RiflemanUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/RiflemanUnit.png"), "Rifleman Unit", false);
		SchoonerUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/SchoonerUnit.png"), "Schooner Unit", false);
		SettlerUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/SettlerUnit.png"), "Settler Unit", false);
		TankUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/TankUnit.png"), "Tank Unit", false);
		TrebuchetUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/TrebuchetUnit.png"), "Trebuchet Unit", false);
		WarriorUnit = new Image(ResourceLoader.getResourceAsStream("assets/units/WarriorUnit.png"), "Warrior Unit", false);
		
		MenuBackground = new Image(ResourceLoader.getResourceAsStream("assets/ui/MenuBackground.png"), "Menu Background", false);
		
		UI_MultiHButton = new Image(ResourceLoader.getResourceAsStream("assets/ui/MultiplayerHButton.png"), "Multiplayer HButton", false);
		UI_MultiPButton = new Image(ResourceLoader.getResourceAsStream("assets/ui/MultiplayerPButton.png"), "Multiplayer PButton", false);
		UI_MultiNButton = new Image(ResourceLoader.getResourceAsStream("assets/ui/MultiplayerNButton.png"), "Multiplayer NButton", false);
		UI_ExitHButton = new Image(ResourceLoader.getResourceAsStream("assets/ui/ExitHButton.png"), "Exit HButton", false);
		UI_ExitPButton = new Image(ResourceLoader.getResourceAsStream("assets/ui/ExitPButton.png"), "Exit PButton", false);
		UI_ExitNButton = new Image(ResourceLoader.getResourceAsStream("assets/ui/ExitNButton.png"), "Exit NButton", false);
	} 
	
}
