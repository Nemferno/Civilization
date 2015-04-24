package org.xodia.civ.ui.custom;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.xodia.civ.GameCamera;
import org.xodia.civ.map.Map;
import org.xodia.civ.map.Tile;
import org.xodia.civ.ui.BasicUserInterface;
import org.xodia.civ.util.MathUtils;

public class MiniMap extends BasicUserInterface{

	private List<TileID> tileList;
	private GameCamera camera;
	private float mapWidth, mapHeight;
	
	public MiniMap(Map map, GameCamera camera, float x, float y) {
		super(x, y, (map.getWidth() * 10) * 0.05f, (map.getHeight() * 10) * 0.05f);
	
		this.camera = camera;
		
		mapWidth = map.getWidth() * 32;
		mapHeight = map.getHeight() * 32;
		tileList = new ArrayList<TileID>();
		
		for(int i = 0; i < map.getTileList().size(); i++){
			Tile t = map.getTileList().get(i);
			TileID id = new TileID(t.getX(), t.getY(), t.getFOVStatus(), t.getID());
			tileList.add(id);
		}
	}
	
	public void setMap(Map map){
		tileList.clear();
		
		for(int i = 0; i < map.getTileList().size(); i++){
			Tile t = map.getTileList().get(i);
			TileID id = new TileID(t.getX(), t.getY(), t.getFOVStatus(), t.getID());
			tileList.add(id);
		}
	}
	
	public void render(Graphics g) {
		super.render(g);
		
		g.translate(getX() * 0.95f, getY() * 0.95f);
		g.scale(0.05f, 0.05f);
		
		for(TileID id : tileList){
			if(id.getFOVType() == Tile.NOT_MET_STATUS){
				g.setColor(Color.black);
			}else if(id.getFOVType() == Tile.MEET_STATUS){
				switch(id.getID()){
				case Tile.DEEP_WATER_ID:
					g.setColor(new Color(81, 81, 255));
					break;
				case Tile.SHALLOW_WATER_ID:
					g.setColor(new Color(158, 158, 158));
					break;
				case Tile.TUNDRA_PLAIN_ID:
					g.setColor(new Color(178, 233, 255));
					break;
				case Tile.TUNDRA_HILL_ID:
					g.setColor(new Color(89, 208, 255));
					break;
				case Tile.TEMPERATE_HILL_ID:
					g.setColor(new Color(115, 205, 97));
					break;
				case Tile.TEMPERATE_PLAIN_ID:
					g.setColor(new Color(115, 206, 97));
					break;
				case Tile.FOREST_HILL_ID:
					g.setColor(new Color(85, 153, 71));
					break;
				case Tile.FOREST_PLAIN_ID:
					g.setColor(new Color(116, 153, 108));
					break;
				case Tile.DESERT_HILL_ID:
					g.setColor(new Color(219, 214, 155));
					break;
				case Tile.DESERT_PLAIN_ID:
					g.setColor(new Color(234, 228, 166));
					break;
				case Tile.JUNGLE_HILL_ID:
					g.setColor(new Color(152, 181, 128));
					break;
				case Tile.JUNGLE_PLAIN_ID:
					g.setColor(new Color(165, 196, 139));
					break;
				case Tile.MOUNTAIN_ID:
					g.setColor(new Color(153, 153, 153));
					break;
				}
			}else if(id.getFOVType() == Tile.MET_STATUS){
				switch(id.getID()){
				case Tile.DEEP_WATER_ID:
					g.setColor(new Color(81, 81, 255, 0.5f));
					break;
				case Tile.SHALLOW_WATER_ID:
					g.setColor(new Color(158, 158, 158, 0.5f));
					break;
				case Tile.TUNDRA_PLAIN_ID:
					g.setColor(new Color(178, 233, 255, 0.5f));
					break;
				case Tile.TUNDRA_HILL_ID:
					g.setColor(new Color(89, 208, 255, 0.5f));
					break;
				case Tile.TEMPERATE_HILL_ID:
					g.setColor(new Color(115, 205, 97, 0.5f));
					break;
				case Tile.TEMPERATE_PLAIN_ID:
					g.setColor(new Color(115, 206, 97, 0.5f));
					break;
				case Tile.FOREST_HILL_ID:
					g.setColor(new Color(85, 153, 71, 0.5f));
					break;
				case Tile.FOREST_PLAIN_ID:
					g.setColor(new Color(116, 153, 108, 0.5f));
					break;
				case Tile.DESERT_HILL_ID:
					g.setColor(new Color(219, 214, 155, 0.5f));
					break;
				case Tile.DESERT_PLAIN_ID:
					g.setColor(new Color(234, 228, 166, 0.5f));
					break;
				case Tile.JUNGLE_HILL_ID:
					g.setColor(new Color(152, 181, 128, 0.5f));
					break;
				case Tile.JUNGLE_PLAIN_ID:
					g.setColor(new Color(165, 196, 139, 0.5f));
					break;
				case Tile.MOUNTAIN_ID:
					g.setColor(new Color(153, 153, 153, 0.5f));
					break;
				}
			}
			
			g.fillRect(getX() + (id.getX() * 10), getY() + (id.getY() * 10), 10, 10);
		}

		g.setColor(Color.red);
		g.drawRect(getX() + ((camera.getCameraX() / 32) * 10), getY() + ((camera.getCameraY() / 32) * 10), 
				((camera.getScreenWidth() / 32) * 10), ((camera.getScreenHeight() / 32) * 10));
		
		g.resetTransform();
		
		g.setColor(Color.gray);
		g.drawRect(getX() - 1, getY() - 1, getWidth() + 1, getHeight() + 1);
	}
	
	public void mousePressed(int button, int x, int y) {
		super.mousePressed(button, x, y);
		
		float newX = x - getX();
		float newY = y - getY();
		
		newX = MathUtils.setXToProprotion(newX, getWidth(), mapWidth);
		newY = MathUtils.setYToProportion(newY, getHeight(), mapHeight);
		
		int tX = ((int) newX / 32) * 32;
		int tY = ((int) newY / 32) * 32;
		
		camera.centerOnPoint(tX, tY);
	}
	
	private class TileID {
		private int x, y;
		private int id;
		private int fovType;
		
		public TileID(int x, int y, int fovType, int id){
			this.x = x;
			this.y = y;
			this.id = id;
			this.fovType = fovType;
		}
		
		public int getFOVType(){
			return fovType;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public int getID(){
			return id;
		}
	}

}
