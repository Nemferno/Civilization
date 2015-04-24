package org.xodia.civ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

/**
 * 
 * Stores all important information like
 * save locations
 * graphic settings
 * audio settings
 * username
 * 
 * @author Jasper Bae
 *
 */
public class CivilizationAppData {

	private static String fileLocation;
	
	private static int screenWidth, screenHeight;
	
	private static String username;
	
	private static boolean isUsernameCreated;
	
	public static Input input;
	public static GameContainer gc;
	
	static {
		fileLocation = "AppData.data";
		screenWidth = 800;
		screenHeight = 600;
		isUsernameCreated = false;
	}
	
	private CivilizationAppData(){}
	
	public static void setGameObjects(GameContainer gc){
		CivilizationAppData.gc = gc;
		CivilizationAppData.input = gc.getInput();
	}
	
	public static void loadAppData(){
		File file = new File(fileLocation);
		
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line = "";
			
			while((line = reader.readLine()) != null){
				if(line.startsWith("Username: ")){
					String username = line.substring("Username: ".length() - 1, line.length()).trim();
					CivilizationAppData.username = username;
					
					if(!username.equals("null")){
						CivilizationAppData.isUsernameCreated = true;
					}
				}else if(line.startsWith("Resolution: ")){
					String resolution = line.substring("Resolution: ".length() - 1, line.length()).trim();
					int width = Integer.parseInt(resolution.substring(0, resolution.indexOf('x')));
					int height = Integer.parseInt(resolution.substring(resolution.indexOf('x') + 1, resolution.length()));
					CivilizationAppData.screenWidth = width;
					CivilizationAppData.screenHeight = height;
				}
			}
			
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void saveAppData(){
		File file = new File(fileLocation);
		
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			writer.write("Username: " + username);
			writer.newLine();
			writer.append("Resolution: " + screenWidth + "x" + screenHeight);
			
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void setUsername(String username){
		if(!isUsernameCreated){
			CivilizationAppData.username = username;
		}
	}
	
	public static int getScreenWidth(){
		return screenWidth;
	}
	
	public static int getScreenHeight(){
		return screenHeight;
	}
	
	public static boolean isUsernameCreated(){
		return isUsernameCreated;
	}
	
	public static String getUsername(){
		return username;
	}
	
}
