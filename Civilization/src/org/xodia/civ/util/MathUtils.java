package org.xodia.civ.util;

public class MathUtils {

	private MathUtils(){}
	
	public static float setXToProprotion(float currentX, float origWidth, float newWidth){
		return (currentX * newWidth) / origWidth;
	}
	
	public static float setYToProportion(float currentY, float origHeight, float newHeight){
		return (currentY * newHeight) / origHeight;
	}
	
}
