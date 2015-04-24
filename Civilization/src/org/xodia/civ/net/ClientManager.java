package org.xodia.civ.net;

import java.io.IOException;

import org.xodia.civ.civs.CivManager;

public class ClientManager {

	private GameClient client;
	
	private static ClientManager instance;
	
	private ClientManager(){}
	
	/**
	 * It will create a new client
	 */
	public boolean createClient(){
		if(client == null){
			// we can create a new client
			client = new GameClient();
			return true;
		}else if(client != null && client.isDisconnected()){
			client = new GameClient();
			return true;
		}else{
			return false;
		}
	}
	
	public boolean connect(String serverIP, int port) throws IOException{
		return client.connect(serverIP, port);
	}
	
	public void close(){
		if(client != null){
			client.close();
		}
	}
	
	public void send(Object o){
		if(client != null){
			client.sendTCP(o);
		}
	}
	
	public void setUpdateFOV(boolean can){
		if(client != null){
			client.setUpdateFOV(can);
		}
	}
	
	public boolean canUpdateFOV(){
		if(client != null){
			return client.canUpdateFOV();
		}
		
		return false;
	}
	
	public boolean isHost(){
		if(client != null){
			return client.isHost();
		}else{
			return false;
		}
	}
	
	public String[] getLoggedInList(){
		return client.getLoggedInList();
	}
	
	public PlayerPacket[] getPlayerInfoList(){
		return client.getPlayerInfoList();
	}
	
	public PlayerPacket getPlayerPacket(long id){
		return client.getPlayerPacket(id);
	}
	
	public CivManager getCivManager(long id){
		return client.getCivManager(id);
	}
	
	public boolean isMyTurn(){
		return client.isMyTurn();
	}
	
	public long getID(){
		return client.getID();
	}
	
	public long getPlayerInfoVersion(){
		return client.getInfoVersion();
	}
	
	public long getLoggedInVersion(){
		return client.getLoggedInVersion();
	}
	
	public static ClientManager getInstance(){
		if(instance == null)
			instance = new ClientManager();
		
		return instance;
	}
	
}
