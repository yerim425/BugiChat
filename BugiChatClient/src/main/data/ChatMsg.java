// ChatMsg.java 채팅 메시지 ObjectStream 용.
package main.data;


import java.io.Serializable;
import javax.swing.ImageIcon;

public class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image
	private String data;
	private String status;
	private ImageIcon img;
	private ImageIcon profileImg;
	
	

	public ChatMsg(String id, String code, String msg) {
		this.id = id;
		this.code = code;
		this.data = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setChatImg(ImageIcon img) {
		this.img = img;
	}
	
	public ImageIcon getChatImg() {
		return img;
	}
	
	public void setProfileImg(ImageIcon img) {
		this.img = img;
	}
	
	public ImageIcon getProfileImg() {
		return img;
	}
	

}