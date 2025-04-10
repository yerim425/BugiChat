package main.views;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BaseView extends JPanel{

	
	private static final long serialVersionUID = 1L;

		// 프로필에 적용할 사진 resize
		public ImageIcon profileImgResize(ImageIcon img, int size) {
			if (img.getIconWidth() == size && img.getIconHeight() == size) {
				return img;
			} else {
				return (new ImageIcon(img.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
			}

		}
}
