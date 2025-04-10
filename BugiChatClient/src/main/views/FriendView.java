package main.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import main.BugiClientView;
import main.data.ChatMsg;

public class FriendView extends BaseView {

	private static final long serialVersionUID = 1L;
	private static JLabel lblMeTxt = new JLabel(" 나"); // "나" 텍스트
	private static JLabel lblFriendTxt = new JLabel(" 나"); // "친구" 텍스트
	private static JButton profileImgBtn = new JButton(); // 내 프로필 이미지
	private static JLabel lblUserName = new JLabel(resources.Strings.USER); // 유저 닉네임
	private static JScrollPane friendListScrollPane = new JScrollPane();
	private JPanel friendListpanel;

	private String userName;
	private ImageIcon profileImg;
	private ImageIcon profileImg_resized;
	private BugiClientView parent;

	private int friendListIdx = 0;

	public FriendView(BugiClientView parent, String userName, ImageIcon img) {
		this.parent = parent;
		this.userName = userName;
		this.profileImg = img;

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setBounds(0, 0, 350, 450);
		setBackground(resources.Colors.MAIN_BG_COLOR);
		setLayout(null);
		setVisible(true);

		// "나" 텍스트
		lblMeTxt.setFont(resources.Fonts.MAIN_BOLD_16);
		lblMeTxt.setForeground(resources.Colors.MAIN_WHITE_COLOR);
		lblMeTxt.setBackground(resources.Colors.MAIN_BLUE2_COLOR);
		lblMeTxt.setOpaque(true);
		lblMeTxt.setBounds(10, 10, 330, 25);
		this.add(lblMeTxt);

		// 나의 프로필 사진, 버튼에 이미지를 삽입
		profileImgBtn.setBounds(10, 45, 50, 50); // 38
		profileImgBtn.setPreferredSize(new Dimension(50, 50));
		profileImgBtn.setIcon(profileImg);
		this.add(profileImgBtn);

		profileImgBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Frame frame = new Frame("프로필 사진 설정"); // 프레임을 하나 만들고
				FileDialog fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD); // 이미지 파일 선택하면
				fd.setVisible(true);
				profileImg = new ImageIcon(fd.getDirectory() + fd.getFile());
				profileImg_resized = profileImgResize(profileImg, 50);
				profileImgBtn.setIcon(profileImg_resized);
				ChatMsg obcm = new ChatMsg(userName, "500", "Change Profile Img"); // 프로필 사진 변경
				obcm.setChatImg(profileImg);
				parent.SendObject(obcm);
			}
		});

		// 사용자 닉네임
		lblUserName = new JLabel(this.userName);
		lblUserName.setFont(resources.Fonts.MAIN_BOLD_16);
		lblUserName.setForeground(resources.Colors.MAIN_DARK_BLUE_COLOR);
		lblUserName.setBounds(75, 55, 100, 30);
		this.add(lblUserName);

		// "친구" 텍스트
		lblFriendTxt = new JLabel(resources.Strings.FRIEND);
		lblFriendTxt.setFont(resources.Fonts.MAIN_BOLD_16);
		lblFriendTxt.setForeground(Color.white);
		lblFriendTxt.setBackground(resources.Colors.MAIN_DARK_BLUE_COLOR);
		// lblFriendTxt.setOpaque(true);
		lblFriendTxt.setBounds(10, 105, 330, 25);
		this.add(lblFriendTxt);

		// 친구 리스트(grid)?
		friendListpanel = new JPanel(new GridBagLayout());
		friendListpanel.setBackground(resources.Colors.MAIN_BG_COLOR);
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridwidth = GridBagConstraints.REMAINDER;
//		gbc.weightx = 1;
//		gbc.weighty = 1;
//		JPanel p = new JPanel();
//		p.setBackground(resources.Colors.MAIN_BG_COLOR);
//		friendListpanel.add(p, gbc);

		// 친구 리스트 스크롤 Pane
		friendListScrollPane = new JScrollPane(friendListpanel);
		friendListScrollPane.setBounds(10, 140, 330, 260);
		friendListScrollPane.setBackground(resources.Colors.MAIN_BG_COLOR);
		// userListScrollPane.setPreferredSize(new Dimension(300, 330));
		friendListpanel.add(friendListScrollPane);

		parent.SendObject(new ChatMsg(userName, "600", "All")); // 접속자 리스트 가져오기
		// SendObject(new ChatMsg(userName, "700", "All"));

	}
//
//	private void initFriendList() {
//		friendListpanel = new JPanel(new GridBagLayout());
//		friendListpanel.setBackground(resources.Colors.MAIN_BG_COLOR);
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridwidth = GridBagConstraints.REMAINDER;
//		gbc.weightx = 1;
//		gbc.weighty = 1;
//		JPanel p = new JPanel();
//		p.setBackground(resources.Colors.MAIN_BG_COLOR);
//		friendListpanel.add(p, gbc);
//
//		friendListIdx = 0;
//	}


	public void setFriendListView(ChatMsg friend) { 
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		FriendProfileItemView friendItem = new FriendProfileItemView(friend);
		
		friendListpanel.add(friendItem.getPanel(), gbc, friendListIdx);
		friendListIdx++;
		friendListpanel.revalidate();
		friendListpanel.repaint();
	}
	
	class FriendProfileItemView extends ProfileItemView{

		JButton friendBtn = new JButton();
		
		public FriendProfileItemView(ChatMsg user) {
			super(user);
			profileImgBtn.addActionListener(new ActionListener() { // 이거 Override 로 써보기
				@Override
				public void actionPerformed(ActionEvent e) { // 친구 프로필 사진의 원본을 보여줌
					ImgFrame imgFrame = new ImgFrame(user.getProfileImg());
					imgFrame.setVisible(true);
				}
			});
			
			friendBtn.setPreferredSize(new Dimension(125, 25));
			friendBtn.setFont(resources.Fonts.MAIN_BOLD_15);
			friendBtn.setForeground(resources.Colors.MAIN_DARK_BLUE_COLOR);

			if (user.getData().matches("Friend")) { // 친구
				if (user.getStatus().equals("O")) {
					friendBtn.setText("접속중");
				} else if (user.getStatus().equals("S")) {
					friendBtn.setText("미접속중");
				}
				friendBtn.setBackground(resources.Colors.MAIN_BG_COLOR);
			} else if (user.getData().matches("Recv")) { // 친구x, 요청 받음
				friendBtn.setText("친구 요청 받음");
				friendBtn.setBackground(resources.Colors.MAIN_WHITE_COLOR);
				friendBtn.setEnabled(true);

				friendBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						// 확인 다이얼로그 창 띄움
						int result = JOptionPane.showConfirmDialog(null, user.getId() + "님의 친구 요청을 승낙하겠습니까?", "Confirm",
								JOptionPane.YES_NO_CANCEL_OPTION);

						if (result == JOptionPane.YES_OPTION) { // 친구 요청 승낙
							ChatMsg yes = new ChatMsg(userName, "702", user.getId());
							parent.SendObject(yes);
							// friendBtn.setText("친구");
							friendBtn.setBackground(resources.Colors.MAIN_BG_COLOR);
						} else if (result == JOptionPane.NO_OPTION) { // 친구 요청 거절
							ChatMsg no = new ChatMsg(userName, "703", user.getId());
							parent.SendObject(no);
							friendListpanel.remove(panel);
						}
						friendListpanel.revalidate();
						friendListpanel.repaint();
					}
				});
				panel.add(friendBtn);
			}
			
		}
	}
	
}


