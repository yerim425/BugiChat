package main.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.data.ChatMsg;

public class ProfileItemView {

	JPanel panel =  new JPanel();;
	JButton profileImgBtn = new JButton();
	JLabel lblUserName;
	String userName = "";

	public ProfileItemView(ChatMsg user) {
		panel.setBackground(resources.Colors.MAIN_BG_COLOR);
		
		profileImgBtn.setPreferredSize(new Dimension(50, 50));
		profileImgBtn.setBackground(Color.white);
		profileImgBtn.setIcon(profileImgResize(user.getProfileImg(), 50));
		
		lblUserName = new JLabel("  " + user.getId());
		lblUserName.setFont(resources.Fonts.MAIN_BOLD_16);
		lblUserName.setForeground(resources.Colors.MAIN_DARK_BLUE_COLOR);
		lblUserName.setPreferredSize(new Dimension(115, 30));
		panel.add(lblUserName);
		
	}

	// 프로필에 적용할 사진 resize
	public ImageIcon profileImgResize(ImageIcon img, int size) {
		if (img.getIconWidth() == size && img.getIconHeight() == size) {
			return img;
		} else {
			return (new ImageIcon(img.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		}

	}
	
	public JPanel getPanel() {
		return this.panel;
	}
	
}
