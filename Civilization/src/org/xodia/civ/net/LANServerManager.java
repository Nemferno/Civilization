package org.xodia.civ.net;


/**
 * 
 * Similar to the ClientManager's job to have access to one server. This serves a similar purpose.
 * This holds the server and "manages" it like closing it or creating it.
 * 
 * @author Jasper Bae
 *
 */
public class LANServerManager {
	
	private static GameServer lanServer;
	
	private LANServerManager(){}
	
	public static void createServer(){
		// TODO Create a Save for the Server and a Load Option
		closeServer();
		
		try{
			lanServer = new GameServer(Network.DEFAULT_PORT, 3);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getIPAddress(){
		if(lanServer != null)
			return lanServer.getIPAddress();
		else
			return null;
	}
	
	public static void closeServer(){
		if(lanServer != null && lanServer.isClosed())
			lanServer.close();
	}

}
