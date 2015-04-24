package org.xodia.civ.util;

/**
 * 
 * Bears similarities to the CustomCountdownTimer,
 * but the sole differences between the two classes
 * is that this is more cleaner and simpler and 
 * uses System.getMillisSecond()
 * 
 * Takes in float instead of second
 * 
 * @author JasperBae
 *
 */
public class Timer {

	private float second;
	private long millisecond;
	
	private boolean isRunning;
	private boolean hasStopped;
	
	private boolean isTimeElapsed;
	
	public Timer(float second){
		this.second = second;
		this.millisecond = (long) (second * 1000);
		
		// Choices are
		// Thread or Method?
	}
	
	public void tick(int delta){
		if(millisecond <= 0){
			isTimeElapsed = true;
			isRunning = false;
			hasStopped = true;
		}else{
			if(isRunning)
				millisecond -= delta;
		}
	}
	
	public void start(){
		try{
			if(!hasStopped)
				isRunning = true;
			else
				throw new Exception("Reset timer or create another timer!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void resume(){
		try{
			if(!hasStopped){
				isRunning = true;
			}else{
				throw new Exception("Reset timer or create another timer!");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void pause(){
		try{
			if(!hasStopped){
				isRunning = false;
			}else{
				throw new Exception("Reset timer or create another timer!");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void reset(){
		hasStopped = false;
		isRunning = true;
		isTimeElapsed = false;
		millisecond = (long) (second * 1000);
	}
	
	public void stop(){
		isRunning = false;
		hasStopped = true;
	}
	
	public long getTimeLeft(){
		return millisecond;
	}
	
	public boolean isTimeElapsed(){
		return isTimeElapsed;
	}
	
	public boolean hasStopped(){
		return hasStopped;
	}
	
}
