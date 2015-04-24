package org.xodia.civ.util.pathfinding;

import java.util.ArrayList;
import java.util.List;

import org.xodia.civ.units.Unit;

/**
 * 
 * The Role is to wrap on a Path object and label
 * how many turns it takes for one unit to go to its destination
 * This is going to be used mainly by both the Client and Server
 * to distinguish where it can go...
 * 
 * First: Iterate the path and calculate how many turns it will take
 * Second: Find out what tiles the unit can go to in its current turn
 * Third: Store it in a 3D int array.
 * 
 * NOTE: Third: Display the result and a boolean value if the player can go at its current turn towards its goal (If path is null, then its automatically not going to work!)
 * 
 * @author Jasper Bae
 *
 */
public class PathWrap {

	/**
	 * Number of turns it will take to get to the goal
	 */
	private int numOfTurns;
	
	/**
	 * The list will only put in valid tiles depending on if it doesn't go over its max cost
	 * int[] = the current turn
	 * int[][] = the number of tiles
	 * int[][][] = the coordinates of tile
	 */
	private List<int[]> pathsList;
	
	public PathWrap(Unit unit, Path path){
		// We have to find out how many turns and how many tiles are going to be in each turn
		//System.out.println("UNIT COORD: " + unit.getX() + ", " + unit.getY());
		numOfTurns = 1;
		int currentPoints = 0;
		List<int[]> paths = new ArrayList<int[]>();
		// We have to set i to 1 because we have to discount its current tile it is on!
		for(int i = 1; i < path.getLength(); i++){
			currentPoints += path.getCost(i);
			
			if(currentPoints > unit.getCurrentMovePoints()){
				// Exclude this index
				currentPoints = path.getCost(i);
				numOfTurns++;
				paths.add(new int[]{ numOfTurns, path.getX(i), path.getY(i), path.getCost(i) });
			}else if(currentPoints == unit.getCurrentMovePoints()){
				// Include this index
				currentPoints = 0;
				paths.add(new int[]{ numOfTurns, path.getX(i), path.getY(i), path.getCost(i) });
				numOfTurns++;
			}else{
				paths.add(new int[]{ numOfTurns, path.getX(i), path.getY(i), path.getCost(i) });
			}
		}
		
		pathsList = new ArrayList<int[]>();
		
		for(int i = 1; i <= numOfTurns; i++){
			for(int j = 0; j < paths.size(); j++){
				int turn = paths.get(j)[0];
				
				if(turn == i){
					// Add it
					pathsList.add(paths.get(j));
				}
			}
		}
	}
	
	public int[] getFirstTurn(){
		int tIndex = 0;
		for(int i = 0; i < getList().size(); i++){
			if(getList().get(i)[0] == 1){
				tIndex = i;
			}else{
				break;
			}
		}
		
		int[] coord = new int[2];
		coord[0] = getList().get(tIndex)[1];
		coord[1] = getList().get(tIndex)[2];
		
		return coord;
	}
	
	/**
	 * Returns the list of tiles it takes for the unit to get to getFirstTurn()
	 */
	public List<int[]> getTilesAtFirstTurn(){
		List<int[]> turn = new ArrayList<>();
		for(int i = 0; i < getList().size(); i++){
			if(getList().get(i)[0] == 1){
				int[] a = new int[2];
				a[0] = getList().get(i)[1];
				a[1] = getList().get(i)[2];
				turn.add(a);
			}else{
				break;
			}
		}
		
		return turn;
	}
	
	public int getTotalCostInFirstTurn(){
		int totalCost = 0;
		for(int i = 0; i < getList().size(); i++){
			if(getList().get(i)[0] == 1){
				totalCost += getList().get(i)[3];
			}else{
				break;
			}
		}
		
		return totalCost;
	}
	
	public List<int[]> getList(){
		return pathsList;
	}
	
	public int getNumOfTurns(){
		return numOfTurns;
	}
	
}
