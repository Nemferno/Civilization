package org.xodia.civ.ui.custom;

import org.xodia.civ.CivilizationAppData;
import org.xodia.civ.net.ClientManager;
import org.xodia.civ.net.Network.Client_Text_WhisperMessage;
import org.xodia.civ.net.PlayerPacket;
import org.xodia.civ.net.Network.Client_Text_PubMessage;
import org.xodia.civ.ui.BasicUserInterface;
import org.xodia.civ.ui.Button;
import org.xodia.civ.ui.Button.OnClickListener;
import org.xodia.civ.ui.Container;
import org.xodia.civ.ui.EditField;
import org.xodia.civ.ui.Label;
import org.xodia.civ.ui.Panel;
import org.xodia.civ.util.TextChatQueue;

public class ChatBox extends BasicUserInterface {

	private Label chat;
	private EditField text;
	private Button send;
	
	public ChatBox(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		Container container = new Container(0, 0, width, height * 0.8f);
		Panel p = new Panel(0, 0, width, height * 0.8f);
		chat = new Label("", 0, 0, width, height * 0.8f);
		addChild(container);
		container.setContent(p);
		p.addChild(chat);
		
		text = new EditField(0, chat.getHeight(), width * 0.75f, height * 0.2f);
		send = new Button("Send", text.getWidth(), chat.getHeight(), width * 0.25f, height * 0.2f, new OnClickListener() {
			public void onClick() {
				// Make sure we are not sending any messages that are empty
				if(!text.getText().trim().equals("")){
					String t = text.getText().trim();
					
					if(t.contains("/w")){
						String[] split = t.split(" ");
						String temp = "";
						for(int i = 2; i < split.length; i++){
							temp = split[i] + " ";
						}
						String string = temp.trim();
						String to = split[1];
						long toID = -1;
						boolean toExists = false;
						
						// We have to see whether a person with that name actually exists
						for(PlayerPacket p : ClientManager.getInstance().getPlayerInfoList()){
							if(p.username.equals(to)){
								toExists = true;
								toID = p.id;
								break;
							}
						}
						
						if(toExists){
							Client_Text_WhisperMessage mess = new Client_Text_WhisperMessage();
							mess.fromName = CivilizationAppData.getUsername();
							mess.to = toID;
							mess.toName = to;
							mess.text = string;
							ClientManager.getInstance().send(mess);
						}else{
							TextChatQueue.getInstance().addQueue("Username [" + to + "] does not exist!");
						}
					}else{
						Client_Text_PubMessage mess = new Client_Text_PubMessage();
						mess.name = CivilizationAppData.getUsername();
						mess.text = t;
						ClientManager.getInstance().send(mess);
						text.setText("");
					}
				}
			}
		});
		addChild(text);
		addChild(send);
	}
	
	public void clearText(){
		text.setText("");
	}
	
	public void update(){
		TextChatQueue queue = TextChatQueue.getInstance();
		
		if(queue.pollQueue()){
			chat.setText(chat.getText().concat(queue.pushQueue() + "\n"));
		}
	}

}
