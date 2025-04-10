package main.views;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImgFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	ImgFrame(ImageIcon img) {
		setBounds(this.getLocation().x + 100, this.getLocation().y, 350, 350);
		setTitle("원본 사진");

		JPanel contentPane = new JPanel(null);
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);

		Image ori_img = img.getImage();
		int width, height;
		double ratio;
		width = img.getIconWidth();
		height = img.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 300 기준으로 축소시킨다.
		if (width > 300 || height > 300) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 300;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 300;
				width = (int) (height * ratio);
			}
			Image new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			img = new ImageIcon(new_img);
		}

		JLabel lblImg = new JLabel();
		lblImg.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
		lblImg.setIcon(img);
		contentPane.add(lblImg);

		//setVisible(true);
	}
}
