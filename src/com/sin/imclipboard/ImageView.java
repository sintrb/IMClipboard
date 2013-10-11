package com.sin.imclipboard;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * A Image Component
 * 
 * @author RobinTang
 * 
 */
public class ImageView extends JPanel {
	private static final long serialVersionUID = 5438377318227799371L;
	private Image image;

	public ImageView() {
		super();
	}

	public ImageView(Image image) {
		super();
		this.image = image;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int tw = this.getWidth();
		int th = this.getHeight();
		if (image != null) {
			int imgw = image.getWidth(null);
			int imgh = image.getHeight(null);
			g.drawImage(image, 0, 0, tw, th, 0, 0, imgw, imgh, null);
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
		this.updateUI();
	}
}
