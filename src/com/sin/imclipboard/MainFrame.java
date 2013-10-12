package com.sin.imclipboard;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Main Frame of IMClipboard
 * 
 * @author RobinTang
 * 
 */
public class MainFrame extends JFrame implements UploaderCallback {
	private static final long serialVersionUID = 2269971701250845501L;
	private ImageView imageView = null;
	private static final String AppName = "IMClipboard";
	private static String imagePath = "/tmp/images/";
	private static final String formatname = "png";

	private Uploader uploader = null;
	private TextField urlText = null;

	public MainFrame() throws HeadlessException {
		super(AppName);

		this.setLayout(new BorderLayout());

		imagePath = System.getProperty("java.io.tmpdir") + "/images/";

		urlText = new TextField();
		urlText.setEditable(false);

		this.imageView = new ImageView();
		this.add(imageView, BorderLayout.CENTER);
		this.add(urlText, BorderLayout.NORTH);
		this.imageView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					updateImage();
				}
			}
		});

		new File(imagePath).mkdirs();

		this.updateImage();
		this.setSize(300, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.uploader = new Uploader(this);
	}

	private String getImageName() {
		return imagePath + System.currentTimeMillis() + "." + formatname;
	}

	private void updateImage() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = clipboard.getContents(null);
		if (transferable != null) {
			if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
//				System.out.println("img");
				try {
					Image img = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
					BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
					Graphics g = bi.getGraphics();
					g.drawImage(img, 0, 0, null);
					imageView.setImage(img);

					final String imagefile = getImageName();
					ImageIO.write(bi, formatname, new File(imagefile));
					this.setTitle(imagefile);

					if (this.uploader.getStatus() != Uploader.Status.Uploadding) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								String imgurl = uploader.uploadImage(imagefile);
								if (imgurl != null) {
									urlText.setText(String.format("%s%s", Uploader.uploadUrl, imgurl));
								}
							}
						}).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
//				System.out.println("str");
			}
		}
	}

	@Override
	public boolean updateStatsuChanged(long transize, long filesize, String filename) {
		return true;
	}

	@Override
	public boolean updateStatsuChangedString(String status) {
		urlText.setText(status);
		return true;
	}
}
