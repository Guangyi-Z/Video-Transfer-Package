package com.server;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JFrame;


/**
 * A frame with an image panel
 */
@SuppressWarnings("serial")
class ImageFrame extends JFrame {
	public ImagePanel panel;
	public JButton jb;

	public ImageFrame() {
		// get screen dimensions
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		setTitle("ImageTest");
		setLocation((screenWidth - DEFAULT_WIDTH) / 2,
				(screenHeight - DEFAULT_HEIGHT) / 2);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		this.getContentPane().setLayout(null);
		panel = new ImagePanel();
		panel.setSize(640, 480);
		panel.setLocation(0, 0);
		add(panel);
	}

	public static final int DEFAULT_WIDTH = 640;
	public static final int DEFAULT_HEIGHT = 560;
}
