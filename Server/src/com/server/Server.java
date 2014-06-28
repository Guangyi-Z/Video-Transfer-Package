package com.server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import com.test.PacketBean;
import com.test.ProducerBean;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 在服务器开启情况下，启动客户端，创建套接字接收图像
 */

public class Server {
	private static ServerSocket ss = null;
	private static ArrayList<ProducerBean> arrayList;
	private static int port = 9901;

	public static void main(String args[]) throws IOException {
		try {
			ss = new ServerSocket(7903);
			System.out.println("S>>>>waiting......");
			while (true) {
				Socket socket = ss.accept();             
				System.out.println("S>>>>accepting......");
				Thread serverThread = new Thread(new ServerThread(socket,arrayList,port));
				serverThread.start();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	

}
