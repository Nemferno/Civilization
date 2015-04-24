package org.xodia.civ.map;

public enum Resource {

	Horses(2, 5);
	
	int min, max;
	
	Resource(int minQuantity, int maxQuantity){
		min = minQuantity;
		max = maxQuantity;
	}
	
	public int getQuantity(){
		return min;
	}
	
	public int getMaxQuantity(){
		return max;
	}
	
}
