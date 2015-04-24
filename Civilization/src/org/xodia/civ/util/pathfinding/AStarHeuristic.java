package org.xodia.civ.util.pathfinding;

public interface AStarHeuristic {

	float getCost(int x, int y, int tx, int ty);
	
}
