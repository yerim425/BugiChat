// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.io.Serializable;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String status;
	private String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image
	private String data;
	private String userList; 
	private ImageIcon profileImg_ori;
	private ImageIcon profileImg_resized;
	private ImageIcon chatImg_ori;
	private ImageIcon chatImg_resized;
	
	
	// Msg 
	public ChatMsg(String id, String code, String msg) {
		this.id = id;
		this.code = code;
		this.data = msg;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setStatud(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}


	public void setData(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	public void setUserList(String list) {
		this.userList = list;
	}
	
	public String getUserList() {
		return this.userList;
	}

	public void setChatImg(ImageIcon img) {
		this.chatImg_ori = img;
		this.chatImg_resized = resizedChatImg(img);
	}
	
	public ImageIcon getChatResizedImg() {
		return chatImg_resized;
	}
	
	public ImageIcon getChatOriginalImg() {
		return chatImg_ori;
	}
	
	public void setProfileImg(ImageIcon img) {
		this.profileImg_ori = img;
		this.profileImg_resized = resizedProfileImg(img);
	}
	
	public ImageIcon getProfileResizedImg() {
		return profileImg_resized;
	}
	
	public ImageIcon getProfileOriginalImg() {
		return profileImg_ori;
	}
	
	// 채팅 이미지의 사이즈 조정하여 리턴
	private ImageIcon resizedChatImg(ImageIcon img) {
		//////////////////////////////////수정
		ImageIcon resizedImg = img;
		return resizedImg;
	}
	// 프로필 이미지의 사이즈 조정하여 리턴
	private ImageIcon resizedProfileImg(ImageIcon img) {
		//////////////////////////////////수정
		ImageIcon resizedImg = img;
		return resizedImg;
	}
}