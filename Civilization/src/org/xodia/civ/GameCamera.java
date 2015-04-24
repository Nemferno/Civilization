package org.xodia.civ;

import org.newdawn.slick.Graphics;
import org.xodia.civ.map.Map;
import org.xodia.civ.units.Unit;

public class GameCamera {

	private float screenWidth, screenHeight;
	private float cameraX, cameraY;
	private float centerX, centerY;
	private float mapWidth, mapHeight;
	
	public GameCamera(float screenWidth, float screenHeight,
			Map map){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.mapWidth = map.getWidth() * 32;
		this.mapHeight = map.getHeight() * 32;
	}
	
	public void centerOnObject(Unit unit){
		centerOnPoint(unit.getX() * 32, unit.getY() * 32);
	}
	
	public void centerOnPoint(float x, float y){
		centerX = x;
		centerY = y;
		
		cameraX = centerX - (screenWidth / 2);
		cameraY = centerY - (screenHeight / 2);
		
		if(cameraX < 0){
			cameraX = 0;
		}else if(cameraX + screenWidth > mapWidth){
			cameraX = mapWidth - screenWidth;
		}
		
		if(cameraY < 0){
			cameraY = 0;
		}else if(cameraY + screenHeight > mapHeight){
			cameraY = mapHeight - screenHeight;
		}
	}
	
	public void setCameraX(float x){
		cameraX = x;
		
		if(cameraX < 0)
			cameraX = 0;
		if(cameraX + screenWidth > mapWidth)
			cameraX = mapWidth - screenWidth;
	}
	
	public void setCameraY(float y){
		cameraY = y;
		
		if(cameraY < 0)
			cameraY = 0;
		if(cameraY + screenHeight > mapHeight)
			cameraY = mapHeight - screenHeight;
	}
	
	public float getCameraX(){
		return cameraX;
	}
	
	public float getCameraY(){
		return cameraY;
	}
	
	public float getScreenWidth(){
		return screenWidth;
	}
	
	public float getScreenHeight(){
		return screenHeight;
	}
	
	public void transform(Graphics g){
		g.translate(-cameraX, -cameraY);
	}
	
	public void detransform(Graphics g){
		g.translate(cameraX, cameraY);
	}
	
}
