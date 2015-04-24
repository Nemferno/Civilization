package org.xodia.civ.util.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xodia.civ.map.Map;
import org.xodia.civ.units.Unit.UnitType;

public class AStarPathFinder {

	private List<Object> closed = new ArrayList<Object>();
	private PriorityList open = new PriorityList();
	
	private int maxSearchDistance;
	
	private Node[][] nodes;
	
	private boolean allowDiagMovement;
	
	private AStarHeuristic heuristic;
	
	private Node current;
	
	private int sourceX;
	
	private int sourceY;
	
	private int distance;
	
	private Map map;
	
	public AStarPathFinder(Map map, int maxSearchDistance, boolean allowDiagMovement){
		this.map = map;
		this.maxSearchDistance = maxSearchDistance;
		this.allowDiagMovement = allowDiagMovement;
		this.heuristic = new ClosestHeuristic();
		
		nodes = new Node[map.getWidth()][map.getHeight()];
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++){
				nodes[x][y] = new Node(x, y);
			}
		}
	}
	
	public Path findPath(UnitType type, int sx, int sy, int tx, int ty){
		current = null;
		
		sourceX = tx;
		sourceY = ty;
		distance = 0;
		
		if(map.isBlocked(type, tx, ty)){
			return null;
		}
		
		if(!(tx > -1 && tx < map.getWidth() && ty > -1 && ty < map.getHeight())){
			return null;
		}
		
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++){
				nodes[x][y].reset();
			}
		}
		
		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		addToOpen(nodes[sx][sy]);
		
		nodes[tx][ty].parent = null;
		
		int maxDepth = 0;
		while((maxDepth < maxSearchDistance) && (open.size() != 0)){
			int lx = sx;
			int ly = sy;
			if(current != null){
				lx = current.x;
				ly = current.y;
			}
			
			current = getFirstInOpen();
			distance = current.depth;
			
			if(current == nodes[tx][ty]){
				if(isValidLocation(type, lx, ly, tx, ty))
					break;
			}
			
			removeFromOpen(current);
			addToClosed(current);
			
			for(int x = -1; x < 2; x++){
				for(int y = -1; y < 2; y++){
					if((x == 0) && (y == 0))
						continue;
					
					if(!allowDiagMovement){
						if((x != 0) && (y != 0))
							continue;
					}
					
					int xp = x + current.x;
					int yp = y + current.y;
					
					if(isValidLocation(type, current.x, current.y, xp, yp)){
						float nextStepCost = current.cost + getMovementCost(current.x, current.y, xp, yp);
						int tCost = (int) getMovementCost(current.x, current.y, xp, yp);
						Node neighbor = nodes[xp][yp];
						
						if(nextStepCost < neighbor.cost){
							if(inOpenList(neighbor))
								removeFromOpen(neighbor);
							if(inClosedList(neighbor))
								removeFromClosed(neighbor);
						}
						
						if(!inOpenList(neighbor) && !(inClosedList(neighbor))){
							neighbor.cost = nextStepCost;
							neighbor.tCost = tCost;
							neighbor.heuristic = getHeuristicCost(xp, yp, tx, ty);
							maxDepth = Math.max(maxDepth, neighbor.setParent(current));
							addToOpen(neighbor);
						}
					}
				}
			}
		}
		
		if(nodes[tx][ty].parent == null)
			return null;
		
		Path path = new Path();
		Node target = nodes[tx][ty];
		while(target != nodes[sx][sy]){
			path.prependStep(target.x, target.y, target.tCost);
			target = target.parent;
		}
		
		path.prependStep(sx, sy, target.tCost);
		
		return path;
	}
	
	public int getCurrentX(){
		if(current == null)
			return -1;
		return current.x;
	}
	
	public int getCurrentY(){
		if(current == null)
			return -1;
		return current.y;
	}
	
	public Node getFirstInOpen(){
		return (Node) open.first();
	}
	
	public void addToOpen(Node node){
		node.setOpen(true);
		open.add(node);
	}
	
	public boolean inOpenList(Node node){
		return node.isOpen();
	}
	
	public void removeFromOpen(Node node){
		node.setOpen(false);
		open.remove(node);
	}
	
	public void addToClosed(Node node){
		node.setClosed(true);
		closed.add(node);
	}
	
	public boolean inClosedList(Node node){
		return node.isClosed();
	}
	
	public void removeFromClosed(Node node){
		node.setClosed(false);
		closed.remove(node);
	}
	
	public boolean isValidLocation(UnitType type, int sx, int sy, int x, int y){
		boolean invalid = (x < 0 ) || (y < 0) || (x >= map.getWidth()) || (y >= map.getHeight());
		
		if((!invalid) && ((sx != x) || (sy != y))){
			sourceX = sx;
			sourceY = sy;
			invalid = map.isBlocked(type, x, y);
		}
		
		return !invalid;
	}
	
	public float getMovementCost(int sx, int sy, int tx, int ty){
		sourceX = sx;
		sourceY = sy;
		
		return map.getCost(tx, ty);
	}
	
	public float getHeuristicCost(int x, int y, int tx, int ty){
		return heuristic.getCost(x, y, tx, ty);
	}
	
	public int getSearchDistance(){
		return distance;
	}
	
	public int getSourceX(){
		return sourceX;
	}
	
	public int getSourceY(){
		return sourceY;
	}
	
	private class PriorityList {
		private List<Object> list = new LinkedList<Object>();
		
		public Object first(){
			return list.get(0);
		}
		
		public void clear(){
			list.clear();
		}
		
		public void add(Object o){
			for(int i = 0; i < list.size(); i++){
				if(((Comparable) list.get(i)).compareTo(o) > 0){
					list.add(i, o);
					break;
				}
			}
			
			if(!list.contains(o)){
				list.add(o);
			}
		}
		
		public void remove(Object o){
			list.remove(o);
		}
		
		public int size(){
			return list.size();
		}
		
		public boolean contains(Object o){
			return list.contains(o);
		}
	}
	
	private class Node implements Comparable {
		private int x;
		private int y;
		private float cost;
		private Node parent;
		private float heuristic;
		private int depth;
		private boolean isOpen;
		private boolean isClosed;
		private int tCost; // Tile Cost not the cost as a whole of the algorithm
		
		public Node(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		public int setParent(Node parent){
			depth = parent.depth + 1;
			this.parent = parent;
			
			return depth;
		}
		
		public int compareTo(Object o) {
			Node object = (Node) o;
			
			float f = heuristic + cost;
			float of = object.heuristic + object.cost;
			
			if(f < of){
				return -1;
			}else if(f > of){
				return 1;
			}else{
				return 0;
			}
		}
		
		public void setOpen(boolean open){
			isOpen = open;
		}
		
		public void setClosed(boolean closed){
			isClosed = closed;
		}
		
		public boolean isOpen(){
			return isOpen;
		}
		
		public boolean isClosed(){
			return isClosed;
		}
		
		public void reset(){
			isClosed = false;
			isOpen = false;
			cost = 0;
			depth = 0;
		}
		
		public String toString(){
			return "[Node " + x + ", " + y + "]";
		}
	}
	
}
