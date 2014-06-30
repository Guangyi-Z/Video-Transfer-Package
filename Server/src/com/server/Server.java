package com.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.dao.VideoDao;
import com.model.PacketBean;
import com.model.ProducerBean;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.xuggle.ferry.Logger;

/**
 * @author lican, caolijie
 * 在服务器开启情况下，启动客户端，创建套接字接收图像
 */

public class Server {
	private static ArrayList<ProducerBean> arrayList = new ArrayList<ProducerBean>();
	private static int producerPort = 9901;
	private static int clientPort = 9902;
	private static int randomPort = 10000;

	public static void main(String args[]) throws IOException {
		final Server server = new Server();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				server.producerListener();
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				server.clientListener();
			}
		}).start();
		
		
	}

	/**
	 * 开启Producer的监听
	 */
	private  void producerListener() {
		ServerSocket serverSocket =null;
		try {
			serverSocket = new ServerSocket(producerPort);
			System.out.println("S>>>>waiting......");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ObjectInputStream objectInputStream = null;
		ObjectOutputStream objectOutputStream = null;
		while (true) {
			Socket socket;
			try {
				socket = serverSocket.accept();
				System.out.println("ProducerServerSocket>>>>accepting......");
				 objectInputStream = new ObjectInputStream(
						socket.getInputStream());
				 objectOutputStream = new ObjectOutputStream(
						socket.getOutputStream());
				PacketBean packetBean;
				packetBean = (PacketBean) objectInputStream.readObject();
				if (packetBean != null) {
					if (packetBean.getPacketType() == PacketBean.PRODUCER_INFO) {
						ProducerBean producerBean = (ProducerBean) packetBean
								.getData();
						int port = getRandomPort();
						producerBean.setPort(port);
						producerBean.setIp("192.168.253.1");
//					System.out.println(producerBean.toString());
						arrayList.add(producerBean);
						//启动一个线程开始监听Client端与对应的Producer的连接
						Thread serverThread = new Thread(new ServerThread(
								socket, producerBean,objectInputStream,objectOutputStream));
						serverThread.start();
						packetBean = new PacketBean(PacketBean.SUCCESS, null);
						objectOutputStream.writeObject(packetBean);
						objectOutputStream.flush();
					}
				} else {
					packetBean = new PacketBean(PacketBean.FAILED, null);
					objectOutputStream.writeObject(packetBean);
					objectOutputStream.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开启Client的监听
	 */
	private  void clientListener() {
		ServerSocket serverSocket =null;
		try {
			serverSocket = new ServerSocket(clientPort);
			System.out.println("ClientServerSocket>>>>waiting......");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ObjectInputStream objectInputStream = null;
		ObjectOutputStream objectOutputStream = null;
		while (true) {
			Socket socket;
			try {
				socket = serverSocket.accept();
				System.out.println("ClientServerSocket>>>>accepting......");
				 objectInputStream = new ObjectInputStream(
						socket.getInputStream());
				 objectOutputStream = new ObjectOutputStream(
						socket.getOutputStream());
				PacketBean packetBean;
				packetBean = (PacketBean) objectInputStream.readObject();
				if (packetBean != null) {
					if (packetBean.getPacketType() == PacketBean.PRODUCER_LIST) {
					  //返回Producer列表
						packetBean = new PacketBean(PacketBean.PRODUCER_LIST, arrayList);
						objectOutputStream.writeObject(packetBean);
						objectOutputStream.flush();
					}else if(packetBean.getPacketType() == PacketBean.CATALOG_LIST){
						List<String> catalogList = new ArrayList<String>();
						VideoDao videoDao = new VideoDao();
						catalogList = videoDao.getVideoDirs();
						//System.out.println(catalogList.toString());
						packetBean = new PacketBean(PacketBean.CATALOG_LIST,catalogList);
						objectOutputStream.writeObject(packetBean);
						objectOutputStream.flush();
					}else if(packetBean.getPacketType() == PacketBean.VIDEO_LIST){
						String dirPath = (String) packetBean.getData();
						List<String> VideoList = new ArrayList<String>();
						VideoDao videoDao = new VideoDao();
						VideoList = videoDao.getVideoNames(dirPath);
						//System.out.println(VideoList.toString());
						packetBean = new PacketBean(PacketBean.VIDEO_LIST,VideoList);
						objectOutputStream.writeObject(packetBean);
						objectOutputStream.flush();
					}
				} else {
					packetBean = new PacketBean(PacketBean.FAILED, null);
					objectOutputStream.writeObject(packetBean);
					objectOutputStream.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(objectInputStream!=null){
						objectInputStream.close();
					}
					if(objectOutputStream!=null){
						objectOutputStream.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	private static synchronized int getRandomPort() {
		return randomPort++;
	}
}
