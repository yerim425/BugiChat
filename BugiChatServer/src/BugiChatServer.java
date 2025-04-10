//JavaObjServer.java ObjectStream 기반 채팅 Server
// 

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class BugiChatServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector<UserService> UserVec = new Vector<UserService>(); // 연결된 사용자를 저장할 벡터
	private Vector<UserService> SleepUserVec = new Vector<UserService>(); // logout 상태인 사용자를 저장할 벡터
	private Vector<ChatRoom> ChatRoomVec = new Vector<ChatRoom>(); // 채팅방(ChatRoom)을 저장할 벡터
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BugiChatServer frame = new BugiChatServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	// 서버 생성 및 시작
	public BugiChatServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(new Color(122, 178, 211)); // bg color
		setContentPane(contentPane); 
		contentPane.setLayout(null);

		JLabel lblbugiIcon = new JLabel(new ImageIcon("src/main_logo_small.png"));
		lblbugiIcon.setVerticalAlignment(JLabel.CENTER);
		lblbugiIcon.setBounds(20, 15, 149, 100);
		contentPane.add(lblbugiIcon);
		
		JLabel lblProjectName = new JLabel("BGUI TALK");
		lblProjectName.setFont(new Font("", Font.BOLD, 24));
		lblProjectName.setForeground(new Color(12, 39, 108));
		lblProjectName.setHorizontalAlignment(JLabel.CENTER);
		lblProjectName.setBounds(180, 30, 150, 30);
		contentPane.add(lblProjectName);
		JLabel lblServer = new JLabel("SERVER");
		lblServer.setFont(new Font("", Font.BOLD, 24));
		lblServer.setForeground(new Color(12, 39, 108));
		lblServer.setHorizontalAlignment(JLabel.CENTER);
		lblServer.setBounds(180, 60, 150, 30);
		contentPane.add(lblServer);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(15, 120, 320, 350);
		scrollPane.setBackground(new Color(246, 244, 235));
		contentPane.add(scrollPane);
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JLabel lblPortLabel = new JLabel("Port Number");
		lblPortLabel.setBounds(15, 480, 100, 30);
		contentPane.add(lblPortLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(110, 480, 220, 30);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.setBackground(new Color(246, 244, 235));
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(15, 515, 320, 40);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("id = " + msg.getId() + "\n");
		textArea.append("data = " + msg.getData() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector<UserService> user_vc; // 사용자 벡터 
		
		public String userName = ""; // 사용자 이름
		public String userStatus; // 사용자 연결 상태
		public ImageIcon userProfileImg = new ImageIcon("src/profile_default.png"); // 프로필 기본 이미지
		public ImageIcon userProfileImg_resized = new ImageIcon("src/profile_default.png"); 
		private Vector<String> friendNameVec = new Vector<String>(); // 해당 유저의 친구 벡터
		private Vector<String> friendWaitVec = new Vector<String>(); // 나 -> 유저 (친구요청) 답변 기다리기 위한 이름 벡터(답변기다림)
		private Vector<String> friendRcvVec = new Vector<String>(); // 유저 -> 나 (친구요청) 받은 상대 이름 벡터(요청받은상태)
		private Vector<ChatRoom> userRoomVec = new Vector<ChatRoom>(); // 내가 활동중인 채팅방 벡터
		

		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());

			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Login() {
			AppendText("새로운 참가자 " + userName + " 입장.");
			String msg = "[" + userName + "]님이 입장 하였습니다.\n";
		}

		public void Logout() {
			String msg = "[" + userName + "]님이 퇴장 하였습니다.\n";
			userStatus = "S"; // Sleep 상태로 변경
			SleepUserVec.addElement(this); // 서버가 관리하는 Sleep 유저 리스트에 추가
			// 다른 유저들의 프로그램에 해당 유저가 로그아웃 했다는 표시를 함
			WriteOthersObject(new ChatMsg(userName, "710", "FriendListRefresh")); // 친구 화면 리스트 수정
			WriteOthersObject(new ChatMsg(userName, "610", "UserListRefresh"));  // 접속자 화면 리스트 수정
			
			AppendText("접속자 " + "[" + userName + "] 종료. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteOne() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.WriteOne(str);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteOne() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.userStatus == "O")
					user.WriteOne(str);
			}
		}

		// 나를 제외한  User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteOthersObject(Object ob) {
			for(int i=0;i<user_vc.size();i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(user!=this && user.userStatus == "O"){
						user.WriteOneObject(ob);
				}
			}
		}
		// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void WriteOne(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("[WriteOne] - dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 귓속말 전송
		public void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("귓속말", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("[WritePrivate] - dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		public void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("[WriteOneObject] - oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		
		// client의 list 화면에 user list 출력
		public void WriteOneUserList(Object obcm) {
			try {
				oos.writeObject(obcm);
			}catch(IOException e) {
				AppendText("[WriteOneUserList] - dos.writeObject(0 error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				}catch(IOException e1) {
					e1.printStackTrace();
				}
				Logout(); //에러가 난 현재 객체를 백터에서 지운다
			}
		}
		
		// 현재 시간 계산하여 출력
		private String clacTime() {
			String time;
			LocalTime nowTime = LocalTime.now();
			int hour = nowTime.getHour();
			int minute = nowTime.getMinute();
			
			String min;
			if(minute<10) min = "0"+minute;
			else min = ""+minute;
			
			if(hour < 12) time = "오전 " + hour + ": " + min;
			else if(hour == 12) time = "오후 12:" + min;
			else time = "오후 "+(hour-12) + ":" + min;
			return time;
		}
		
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					} else
						continue;
					
					if (cm.getCode().matches("100")) { // 100: 로그인
						userName = cm.getId();
						
						for(int i=0;i<user_vc.size();i++) {
							UserService user = user_vc.elementAt(i); // 유저 객체
							if(user.userName.matches(this.userName) && this != user){
								// 해당 유저와 닉네임이 동일하면,  
								this.userProfileImg = user.userProfileImg; // 프로필 이미지 가져오기
								this.friendNameVec = user.friendNameVec; // 친구 이름 리스트 가져오기
								this.friendWaitVec = user.friendWaitVec; // 친구요청 승낙 기다림 리스트 가져오기
								this.friendRcvVec = user.friendRcvVec; // 친구요청 추가 전 리스트 가져오기
								UserVec.remove(user); // 이전 나의 객체가 남아있다면 삭제함
								user_vc = UserVec;
							}
						}
						userStatus = "O"; // Online 상태로 변경
						ChatMsg cm2 = new ChatMsg(userName, "500", "ProfileImg"); // 프로필 이미지 set
						cm2.setProfileImg(userProfileImg);
						WriteOneObject(cm2);
						
						Login(); // 로그인 
						
					} else if (cm.getCode().matches("200")) {
						msg = String.format("[%s] %s", cm.getId(), cm.getData());
						AppendText(msg); // server 화면에 출력
						String[] args = msg.split(" "); // 단어들을 분리한다.
						if (args.length == 1) { // Enter key 만 들어온 경우 Wakeup 처리만 한다.
							userStatus = "O";
						} else if (args[1].matches("/exit")) {
							Logout();
							break;
						} else if (args[1].matches("/list")) {
							WriteOne("User list\n");
							WriteOne("Name\tStatus\n");
							WriteOne("-----------------------------\n");
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								WriteOne(user.userName + "\t" + user.userStatus + "\n");
							}
							WriteOne("-----------------------------\n");
						} else if (args[1].matches("/sleep")) {
							userStatus = "S";
						} else if (args[1].matches("/wakeup")) {
							userStatus = "O";
						} else if (args[1].matches("/to")) { // 귓속말
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.userName.matches(args[2]) && user.userStatus.matches("O")) {
									String msg2 = "";
									for (int j = 3; j < args.length; j++) {// 실제 message 부분
										msg2 += args[j];
										if (j < args.length - 1)
											msg2 += " ";
									}
									// /to 빼고.. [귓속말] [user1] Hello user2..
									user.WritePrivate(args[0] + " " + msg2 + "\n");
									//user.WriteOne("[귓속말] " + args[0] + " " + msg2 + "\n");
									break;
								}
							}
						} else { // 일반 채팅 메시지
							userStatus = "O";
							//WriteAll(msg + "\n"); // Write All
							WriteAllObject(cm);
						}
					} else if (cm.getCode().matches("300")) {
						//WriteAllObject(cm);
						
					} else if (cm.getCode().matches("400")) {
						
					}  else if (cm.getCode().matches("500")) { // 프로필 이미지 set
						this.userProfileImg = cm.getProfileImg();
						//this.userProfileImg_resized = cm.getProfileResizedImg();
						
						// 유저들의 친구화면, 접속자화면 Refresh
						for(int i=0;i<user_vc.size();i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if(user.userStatus.matches("O")) { 
								user.WriteOneObject(new ChatMsg(user.userName, "610", "UserListRefresh"));
								user.WriteOneObject(new ChatMsg(user.userName, "710", "FriendListRefresh"));
							}
						}
						
						// 채팅방들 중 내 프로필 사진 Refresh
						for(int j=0;j<ChatRoomVec.size();j++) {
							ChatRoom cr = ChatRoomVec.elementAt(j);
							if(cr.getUserNames().contains(userName)){
								for(int k=0;k<cr.chatMsgVec.size();k++){
									ChatMsg c = cr.chatMsgVec.elementAt(k);
									if(c.getId().equals(userName)) {
										c.setProfileImg(userProfileImg);
									}
								}
							}
							
						}
						
					} else if (cm.getCode().matches("510")) { // original 프로필 이미지 가져오기 요청
						String[] data = cm.getId().split("\\["); // "", "user1]"
						data = data[1].split("\\]"); // "user1"
						for(int i=0;i<user_vc.size();i++) {
							UserService user = user_vc.elementAt(i);
							if(user.userName.equals(data[0])) { // 해당 유저의 original 프로필 이미지 전송
								ChatMsg c = new ChatMsg(userName, "510", "Get Original Image");
								c.setProfileImg(user.userProfileImg);
								WriteOneObject(c);
							}
						}
					}else if(cm.getCode().matches("600")) { // user list 출력을 위해 user 정보 보내기
						if(cm.getData().matches("all")) {
							WriteOthersObject(new ChatMsg(userName, "610", "UserListRefresh"));
						}
						else {
							ChatMsg up = new ChatMsg(this.userName, "600", "Me");
							up.setProfileImg(this.userProfileImg);
							WriteOneObject(up); // 내 profile 정보 보내기
							
							for (int i = 0; i < user_vc.size(); i++) { // 다른 유저들 리스트 보내기
								UserService user = (UserService) user_vc.elementAt(i);
								if(user != this && user.userStatus.matches("O")) {
									up = new ChatMsg(user.userName, "600", "None"); // 친구x, 신청 상태x
									up.setProfileImg(user.userProfileImg);
									
									if(friendNameVec.contains(user.userName)){ // 친구일 경우
										up = new ChatMsg(user.userName, "600", "Friend");
										up.setProfileImg(user.userProfileImg);
									}
									if(friendWaitVec.contains(user.userName)){ // 친구x, 신청 상태o
										up = new ChatMsg(user.userName, "600", "Wait");
										up.setProfileImg(user.userProfileImg);
										
									}
									WriteOneObject(up);
									
								}
							}
						}
						
					}
					else if (cm.getCode().matches("700")) { // 친구 list 출력

						if(cm.getData().matches("all")) {
							WriteAllObject(new ChatMsg(userName, "710", "UserListRefresh"));
						}
						else {
							ChatMsg fp;
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if(user != this) {
									if(friendNameVec.contains(user.userName)){ // 친구일 경우
										fp = new ChatMsg(user.userName, "700", "Friend");
										fp.setProfileImg(user.userProfileImg);
										//fp.setProfileImg_resized(user.UserProfileImg_resized);
										fp.setStatus(user.userStatus);
										WriteOneObject(fp);
									}
									if(friendRcvVec.contains(user.userName)){ // 친구x, 신청 상태o
										fp = new ChatMsg(user.userName, "700", "Receive");
										fp.setProfileImg(user.userProfileImg);
										WriteOneObject(fp);
									}
								}	
							}
						}
						
					}
					else if (cm.getCode().matches("701")) { // 친구 추가 신청

						// 신청을 받은 user의 name
						String friend = cm.getData();
						
						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if(user.userName.matches(friend)) {
								this.friendWaitVec.add(friend); // 나는 상대방(friend)의 승낙을 기다림(wait)
								user.friendRcvVec.add(userName); // 상대방(friend)는 나의 친구 요청을 받음(receive)
								
								if(user.userStatus.matches("O")) { // 상대방의 친구 목록 새로고침(내가 보낸 친구 요청을 버튼으로 띄우기 위해)
									user.WriteOneObject(new ChatMsg(user.userName, "710", "FriendListRefresh"));
								}	
		
							}
						}	
					}
					else if (cm.getCode().matches("702")) { // 친구 추가 승낙

						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if(user.userName.matches(cm.getData())) {
								this.friendRcvVec.remove(this.friendRcvVec.indexOf(user.userName)); // 내 Recv 리스트에서 제거
								user.friendWaitVec.remove(user.friendWaitVec.indexOf(this.userName)); // 상대방의 Wait 리스트에서 제거
								if(user.friendRcvVec.contains(this.userName)) // 상대방의 Recv 리스트에 내가 있다면 제거(혹시 모를 상황)
									user.friendRcvVec.remove(user.friendRcvVec.indexOf(this.userName));
								if(this.friendWaitVec.contains(user.userName)) // 내 Wait 리스트에 상대방이 있었다면 제거(혹시 모를 상황)
									this.friendWaitVec.remove(this.friendWaitVec.indexOf(user.userName));
								
								this.friendNameVec.add(user.userName); // 내 친구 리스트에 상대방 이름 추가
								user.friendNameVec.add(this.userName); // 상대방 친구 리스트에 내 이름 추가
								
								if(user.userStatus.matches("O")) { // 상대방이 온라인 상태라면, 상대방의 유저 리스트들 리프레시
									user.WriteOneObject(new ChatMsg(user.userName, "610", "UserListRefresh"));
									user.WriteOneObject(new ChatMsg(user.userName, "710", "friendListRefresh"));
								}
								this.WriteOneObject(new ChatMsg(userName, "710", "friendListRefresh"));
								// 상대방의 친구 목록과 접속자 목록 새로고침, 나의 친구 목록 새로고침
							}
						}
					}
					else if (cm.getCode().matches("703")) { // 친구 추가 거절

						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if(user.userName.matches(cm.getData())) {
								this.friendRcvVec.remove(this.friendRcvVec.indexOf(user.userName)); 
								user.friendWaitVec.remove(user.friendWaitVec.indexOf(this.userName));
								if(user.friendRcvVec.contains(this.userName))
									user.friendRcvVec.remove(user.friendRcvVec.indexOf(this.userName));
								if(this.friendWaitVec.contains(user.userName))
									this.friendWaitVec.remove(this.friendWaitVec.indexOf(user.userName));
								
								if(user.userStatus.matches("O")) {// 상대방의 친구 목록과 접속자 목록 새로고침
									user.WriteOneObject(new ChatMsg(user.userName, "610", "UserListRefresh"));
									user.WriteOneObject(new ChatMsg(user.userName, "710", "friendListRefresh"));
								}
								
							}
						}
					}
					else if(cm.getCode().matches("900")) { // 프로그램 종료(로그아웃)
						Logout();
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}
