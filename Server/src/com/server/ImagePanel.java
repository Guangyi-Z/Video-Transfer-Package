package com.server;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.model.PacketBean;


/**
 * A panel that displays a tiled image
 */
@SuppressWarnings("serial")
class ImagePanel extends JPanel {
	
	private BufferedImage image;

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null) {
			// System.out.println("没有图像~~~~~~~~~~");
			return;
		}
		g.drawImage(image, 0, 0, null);
		// System.out.println("有图像出来的~~~~~~~~~~~");
	}

}

