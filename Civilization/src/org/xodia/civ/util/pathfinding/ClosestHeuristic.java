package org.xodia.civ.util.pathfinding;

public class ClosestHeuristic implements AStarHeuristic{

	public float getCost(int x, int y, int tx, int ty) {
		float dx = tx - x;
		float dy = ty - y;
		
		return (float) (Math.sqrt((dx * dx) + (dy * dy)));
	}
	
}
