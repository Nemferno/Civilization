package org.xodia.civ.util;

import org.newdawn.slick.Image;

public class ImageUtil {

	private ImageUtil(){}
	
	public static Image getScaledImage(Image image, float scale){
		float width = image.getWidth() * scale;
		float height = image.getHeight() * scale;
		return getScaledImage(image, width, height);
	}
	
	public static Image getScaledImage(Image image, float width, float height){
		return image.getScaledCopy((int) width, (int) height);
	}
	
}
