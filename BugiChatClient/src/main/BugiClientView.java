// ObjecStram 기반 Client
package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import main.data.ChatRoom;
import main.views.FriendView;
import main.data.ChatMsg;

import javax.swing.JToggleButton;
import javax.swing.JList;

public class BugiClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BugiClientView clientView;
	private JPanel contentPane;
	private JPanel viewPanel; // [친구, 채팅, 접속자, 설정] 뷰 패널
	private JPanel bottomPanel; // [친구 채팅 접속자, 설정] 바텀 버튼 패널

	private FriendView friendViewPanel; // 친구 화면
	private JPanel chatListViewPanel; // 채팅 화면
	private JPanel userListViewPanel; // 접속자 화면
	private JPanel settingViewPanel; // 설정 화면

	private JButton friendViewBtn; // 친구 화면 버튼
	private JButton chatListViewBtn; // 채팅 화면 버튼
	private JButton userListViewBtn; // 접속자 화면 버튼
	private JButton settingViewBtn; // 채팅화면 버튼

	private JScrollPane friendListScrollPane; // 친구 리스트 스크롤
	private JScrollPane chatListScrollPane; // 채팅방 리스트 스크롤
	private JScrollPane userListScrollPane; // 접속자 리스트 스크롤

	// GridBagLayout
	private JPanel friendListScrollPanel; // 친구 리스트의 아이템
	private JPanel chatListScrollPanel; // 채팅방 리스트의 아이템
	private JPanel userListScrollPanel; // 접속자 리스트의 아이템

	// 채팅방 생성 체크박스
	private JScrollPane checkBoxScrollPane = new JScrollPane(); //
	private JPanel checkBoxContentPane = new JPanel();
	private JPanel checkBoxListPanel = new JPanel();

	private int checkBoxPosY = 5;
	private Vector<JCheckBox> checkBoxVec = new Vector<JCheckBox>();
	private Vector<BugiClientChat> chatRoomViewVec = new Vector<BugiClientChat>();
	private Vector<ChatRoom> chatRoomVec = new Vector<ChatRoom>();

	int friendListIdx = 0;
	int chatListIdx = 0;
	int userListIdx = 0;
	int checkBoxIdx = 0;

	private String userName; // 닉네임
	private String userStatus; // 접속 상태
	private String IpAddr;
	private String PortN;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	// ------친구 화면 GUI ------------------
	private JLabel lbluserName; // 나의 닉네임
	private JLabel lblmetxt; // "나" txt
	private JLabel lblfriendtxt; // "친구" txt
	private Frame frame;
	private FileDialog fd;
	private JButton profileImgBtn;
	private ImageIcon profileImg = new ImageIcon("src/images/profile_default.png"); // original
	private ImageIcon profileImg_resized; // 50x50
	// -------------------------------------

	String lastMsg;
	String lastTime;

	int check = 0;

	/**
	 * Create the frame.
	 */
	public BugiClientView(String username, String ip_addr, String port_no) {
		this.setTitle("BUGI TALK");
		this.setResizable(false);
		clientView = this;
		this.userName = username;
		this.IpAddr = ip_addr;
		this.PortN = port_no;
		this.userStatus = "O";
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel(null);
		contentPane.setBackground(resources.Colors.MAIN_BG_COLOR);

		bottomPanel = new JPanel(); // 버튼 패널
		bottomPanel.setBackground(resources.Colors.MAIN_BG_COLOR);
		bottomPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		bottomPanel.setLayout(new GridLayout(1, 4, 3, 3));
		bottomPanel.setBounds(0, 500, 350, 60);

		// View 버튼 4개 만들기--------------------------------------------------

		friendViewBtn = new JButton(resources.Strings.FRIEND);
		friendViewBtn.setFont(resources.Fonts.MAIN_BOLD_15);
		friendViewBtn.setForeground(resources.Colors.MAIN_DARK_BLUE_COLOR);
		// friendViewBtn.setBackground(resources.Colors.MAIN_DARK_BLUE_COLOR);
		friendViewBtn.setSelected(true);
		friendViewBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		// friendViewBtn.addActionListener(new MyActionListener());
		bottomPanel.add(friendViewBtn);

		chatListViewBtn = new JButton(resources.Strings.CHATTING);
		chatListViewBtn.setFont(resources.Fonts.MAIN_BOLD_15);
		chatListViewBtn.setForeground(resources.Colors.MAIN_WHITE_COLOR);
		// chatListViewBtn.setBackground(resources.Colors.MAIN_BG_COLOR);
		// chatListViewBtn.addActionListener(new MyActionListener());
		chatListViewBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		bottomPanel.add(chatListViewBtn);

		userListViewBtn = new JButton(resources.Strings.ACCESSOR);
		userListViewBtn.setFont(resources.Fonts.MAIN_BOLD_15);
		userListViewBtn.setForeground(resources.Colors.MAIN_WHITE_COLOR);
		// userListViewBtn.setBackground(resources.Colors.MAIN_BG_COLOR);
		// userListViewBtn.addActionListener(new MyActionListener());
		userListViewBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		bottomPanel.add(userListViewBtn);

		settingViewBtn = new JButton(resources.Strings.SETTING);
		settingViewBtn.setFont(resources.Fonts.MAIN_BOLD_15);
		settingViewBtn.setForeground(resources.Colors.MAIN_WHITE_COLOR);
		// settingViewBtn.setBackground(resources.Colors.MAIN_BG_COLOR);
		// settingViewBtn.addActionListener(new MyActionListener());
		settingViewBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		bottomPanel.add(settingViewBtn);
		// ------------------------------------------------------------------------

		// viewPanel = new JPanel(); // 뷰 패널
		friendViewPanel = new FriendView(this, userName, profileImg); // 친구 화면 생성 및 display

		contentPane.add(friendViewPanel);
		contentPane.add(bottomPanel); // 바텀 패널 추가
		setContentPane(contentPane);

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg(userName, "100", "Hello(Login)");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(userName + " : connect error");
		}

	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm; // Chat Massege
					int idx;

					try {
						obcm = ois.readObject(); // 데이터 읽음
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}

					if (obcm == null) // ChatMsg is null
						break;
					if (obcm instanceof ChatMsg) { // obcm이 ChatMsg로 대신할 수 있다면,
						cm = (ChatMsg) obcm;
						msg = String.format("[%s] %s", cm.getId(), cm.getData());// "[닉네임], Data"

					} else
						continue;
					
					
					// Code 마다의 기능 수행
					switch(cm.getCode()) {
					
					case "500": // 나의 프로필 이미지 수정, 유저들의 리스트에 나의 프로필 이미지 수정
						profileImg = cm.getChatImg();
						friendViewPanel.refreshFriendView();
						break;
					}
					
					
					
					
					
				} catch (IOException e) {
					System.out.println("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			}
		}
	}

	/*
	 * // keyboard enter key 치면 서버로 전송 class TextSendAction implements
	 * ActionListener{
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { // Send button을 누르거나
	 * 메시지 입력하고 Enter key 치면 if (e.getSource() == btnSend || e.getSource() ==
	 * txtInput) { String msg = null; // msg = String.format("[%s] %s\n", UserName,
	 * txtInput.getText()); msg = txtInput.getText(); SendMessage(msg);
	 * txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다. txtInput.requestFocus();
	 * // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다 if (msg.contains("/exit")) // 종료 처리
	 * System.exit(0); } }
	 * 
	 * }
	 * 
	 * 
	 * class ImageSendAction implements ActionListener {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { // 액션 이벤트가 sendBtn일때
	 * 또는 textField 에세 Enter key 치면 if (e.getSource() == imgBtn) { frame = new
	 * Frame("이미지첨부"); fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD); //
	 * frame.setVisible(true); // fd.setDirectory(".\\"); fd.setVisible(true); //
	 * System.out.println(fd.getDirectory() + fd.getFile()); ChatMsg obcm = new
	 * ChatMsg(UserName, "300", "IMG"); ImageIcon img = new
	 * ImageIcon(fd.getDirectory() + fd.getFile()); obcm.setImg(img);
	 * SendObject(obcm); } } }
	 * 
	 * ImageIcon icon1 = new ImageIcon("src/icon1.jpg");
	 * 
	 * public void AppendIcon(ImageIcon icon) { int len =
	 * textArea.getDocument().getLength(); // 끝으로 이동 textArea.setCaretPosition(len);
	 * textArea.insertIcon(icon); }
	 * 
	 * // 화면에 출력 public void AppendText(String msg) { // textArea.append(msg +
	 * "\n"); // AppendIcon(icon1); msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다. int len
	 * = textArea.getDocument().getLength(); // 끝으로 이동
	 * textArea.setCaretPosition(len); textArea.replaceSelection(msg + "\n"); }
	 * 
	 * public void AppendImage(ImageIcon ori_icon) { int len =
	 * textArea.getDocument().getLength(); textArea.setCaretPosition(len); // place
	 * caret at the end (with no selection) Image ori_img = ori_icon.getImage(); int
	 * width, height; double ratio; width = ori_icon.getIconWidth(); height =
	 * ori_icon.getIconHeight(); // Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다. if
	 * (width > 200 || height > 200) { if (width > height) { // 가로 사진 ratio =
	 * (double) height / width; width = 200; height = (int) (width * ratio); } else
	 * { // 세로 사진 ratio = (double) width / height; height = 200; width = (int)
	 * (height * ratio); } Image new_img = ori_img.getScaledInstance(width, height,
	 * Image.SCALE_SMOOTH); ImageIcon new_icon = new ImageIcon(new_img);
	 * textArea.insertIcon(new_icon); } else textArea.insertIcon(ori_icon); len =
	 * textArea.getDocument().getLength(); textArea.setCaretPosition(len);
	 * textArea.replaceSelection("\n"); // ImageViewAction viewaction = new
	 * ImageViewAction(); // new_icon.addActionListener(viewaction); // 내부클래스로 액션
	 * 리스너를 상속받은 클래스로 }
	 * 
	 * // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수 public byte[]
	 * MakePacket(String msg) { byte[] packet = new byte[BUF_LEN]; byte[] bb = null;
	 * int i; for (i = 0; i < BUF_LEN; i++) packet[i] = 0; try { bb =
	 * msg.getBytes("euc-kr"); } catch (UnsupportedEncodingException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); System.exit(0); } for (i = 0;
	 * i < bb.length; i++) packet[i] = bb[i]; return packet; }
	 * 
	 * // Server에게 network으로 전송 public void SendMessage(String msg) { try { //
	 * dos.writeUTF(msg); // byte[] bb; // bb = MakePacket(msg); // dos.write(bb, 0,
	 * bb.length); ChatMsg obcm = new ChatMsg(UserName, "200", msg);
	 * oos.writeObject(obcm); } catch (IOException e) { //
	 * AppendText("dos.write() error"); AppendText("oos.writeObject() error"); try {
	 * // dos.close(); // dis.close(); ois.close(); oos.close(); socket.close(); }
	 * catch (IOException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); System.exit(0); } } }
	 * 
	 * public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드 try {
	 * oos.writeObject(ob); } catch (IOException e) { //
	 * textArea.append("메세지 송신 에러!!\n"); AppendText("SendObject Error"); } }
	 */
	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
			oos.reset();
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			System.out.println(userName + " : SendObject Error");
		}
	}

}
