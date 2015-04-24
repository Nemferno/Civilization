package org.xodia.civ.util.pathfinding;

import java.util.ArrayList;

public class Path {

	private ArrayList<Step> steps = new ArrayList<Step>();
	
	public Path(){}
	
	public int getLength(){
		return steps.size();
	}
	
	public Step getStep(int index){
		return (Step) steps.get(index);
	}
	
	public int getX(int index){
		return getStep(index).x;
	}
	
	public int getY(int index){
		return getStep(index).y;
	}
	
	public int getCost(int index){
		return getStep(index).cost;
	}
	
	public void appendStep(int x, int y, int cost){
		steps.add(new Step(x, y, cost));
	}
	
	public void prependStep(int x, int y, int cost){
		steps.add(0, new Step(x, y, cost));
	}
	
	public boolean contains(int x, int y, int cost){
		return steps.contains(new Step(x, y, cost));
	}
	
	public class Step {
		private int x;
		private int y;
		private int cost;
		
		public Step(int x, int y, int cost){
			this.x = x;
			this.y = y;
			this.cost = cost;
		}
		
		public int getCost(){
			return cost;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public int hashCode(){
			return x*y;
		}
		
		public boolean equals(Object other){
			if(other instanceof Step){
				Step o = (Step) other;
				
				return (o.x == x) && (o.y == y);
			}
			
			return false;
		}
	}
	
}
