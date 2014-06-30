/*package com.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import com.test.PacketBean;

public class ClientThread_2 implements Runnable{
	
	private Socket socket;
	private ObjectOutputStream objectOutputStream;
	private BufferedImage image;
	
	public ClientThread_2(Socket socket2){
		this.socket = socket2;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerThread serverThread = new ServerThread();
		try {
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());                                  //������
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while(true){
				image = serverThread.getImage();
				ImageIO.write(image, "jpg", out);                                                                                             //��ʵʱͼƬת����������
				byte[] datas = out.toByteArray();
				try {
					PacketBean data = new PacketBean();                                                                             //���л�����
					data.setData(datas);
					data.setPacketType("sendshots");

					out.flush();
					objectOutputStream.writeObject(data);
					objectOutputStream.flush();
					// objectOutputStream.close();
				}catch (IOException e) {
					// TODO: handle exception
					System.out.println("Connection Close");
					e.printStackTrace();
					//break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

}
*/