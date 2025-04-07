import java.util.Vector;

import javax.swing.JPanel;

public class ChatRoom { // 채팅방 클래스

	private int roomId = 0;
	private String userNames;
	private String[] userNameList;
	private String roomName;
	private JPanel roomImg;
	private String lastmsg;
	private String lastTime;
	public Vector<ChatMsg> chatMsgVec = new Vector<ChatMsg>();
	
	ChatRoom(int id, String userList){
		roomId = id;
		userNameList = userList.split(" ");
	}
	
	public String getUserNames() {
		return userNames;
	}
}
