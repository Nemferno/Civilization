package org.xodia.civ.util;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * 
 * Creates a text that fades at a certain duration...
 * It will show up and then fade per 0.25s
 * 
 * @author Jasper Bae
 *
 */
public class FadeText {

	private final Color DEFAULT_COLOR = Color.white;
	
	private float alpha;
	
	private float x;
	private float y;
	
	private String text;
	
	private Color textColor;
	
	private Timer durationTimer;
	private Timer fadeTimer;
	
	public FadeText(int x, int y, String text, int duration){
		this.text = text;
		this.x = x;
		this.y = y;

		this.durationTimer = new Timer(duration);
		this.durationTimer.start();
		this.fadeTimer = new Timer(.25f);
		this.fadeTimer.start();
		
		alpha = 1;
		
		textColor = DEFAULT_COLOR;
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
	}
	
	public void setTextColor(Color color){
		textColor = color;
	}
	
	public void update(int delta){
		if(durationTimer.isTimeElapsed()){
			if(fadeTimer.isTimeElapsed()){
				if(alpha > 0){
					alpha -= 0.1f;
				}
			}else{
				fadeTimer.tick(delta);
			}
		}else{
			durationTimer.tick(delta);
		}
	}
	
	public void render(Graphics g){
		g.setColor(new Color(textColor.r, textColor.g, textColor.b, alpha));
		g.drawString(text, x, y);
	}
	
	public boolean isFinished(){
		return alpha <= 0;
	}
	
	public void reset(){
		durationTimer.reset();
		fadeTimer.reset();
		alpha = 1f;
	}
	
}
