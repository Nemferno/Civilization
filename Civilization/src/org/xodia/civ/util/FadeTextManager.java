package org.xodia.civ.util;

import org.newdawn.slick.Graphics;

public class FadeTextManager {

	private static FadeTextManager instance;
	
	private FadeText text;
	
	public void renderFadeTexts(Graphics g){
		if(text != null)
			text.render(g);
	}
	
	public void updateFadeTexts(int delta){
		if(text != null){
			text.update(delta);
			
			if(text.isFinished())
				text = null;
		}
	}
	
	public void addFadeText(int x, int y, String text, int fadeDuration){
		this.text = new FadeText(x, y, text, fadeDuration);
	}
	
	public static FadeTextManager getInstance(){
		if(instance == null)
			instance = new FadeTextManager();
		return instance;
	}
	
}
