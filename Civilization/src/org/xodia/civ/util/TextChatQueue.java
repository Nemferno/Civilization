package org.xodia.civ.util;

import java.util.ArrayList;
import java.util.List;

public class TextChatQueue {

	private static TextChatQueue instance;
	
	private List<String> queueList;
	
	private TextChatQueue(){
		this(5);
	}
	
	private TextChatQueue(int capacity){
		queueList = new ArrayList<String>(capacity);
	}
	
	public void addQueue(String text){
		queueList.add(text);
	}
	
	public boolean pollQueue(){
		if(queueList.size() > 0)
			return true;
		else
			return false;
	}
	
	public String pushQueue(){
		String queue = first();
		
		try{
			if(queueList.remove(queue))
				return queue;
			else
				throw new Exception("First Queue was unable to be removed from the block!");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String first(){
		return queueList.get(0);
	}
	
	public String last(){
		return queueList.get(queueList.size() - 1);
	}
	
	public static TextChatQueue getInstance(){
		if(instance == null)
			instance = new TextChatQueue();
		return instance;
	}
	
	public static TextChatQueue getInstance(int capacity){
		if(instance == null)
			instance = new TextChatQueue(capacity);
		return instance;
	}
	
}
