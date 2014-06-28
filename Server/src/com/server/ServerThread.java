package com.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.test.PacketBean;
import com.test.ProducerBean;

public class ServerThread implements Runnable{
	
	private Socket socket;
	private Timer timer;
	private BufferedImage image;
	private ArrayList<BufferedImage> arrayList = new ArrayList<BufferedImage>();
	private String androidName;
	private ArrayList<ProducerBean> arrayList2;
	private int port;
	
	public ServerThread(Socket socket2,ArrayList<ProducerBean> arrayList,int port2){
		this.socket = socket2;
		this.arrayList2 = arrayList;
		this.port = port2;
	}

	@Override
	public void run() {
		ImageFrame frame = new ImageFrame();
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		timer = new Timer();                                                          //�ϳ���Ƶ�����ڼ�ʱ��
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// System.out.println("Your ~~~~~~~~~~~~~~~~");
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int date = c.get(Calendar.DATE);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				int second = c.get(Calendar.SECOND);
				
				String path = "G:/video2/" + androidName + "/";
				File file =new File(path);
				if(!file.exists()){
					//���Ҫ�����Ķ༶Ŀ¼�����ڲ���Ҫ������
				    file.mkdirs();
		        }
				String name = year + "_" + month + "_" + date + "@" + hour + "_" + minute   + "_" + second + ".mp4";                   //�õ�ϵͳ��ǰʱ�䣬�Ը�ʱ����Ϊ��Ƶ�ļ�������						
				System.out.println("ͼƬ��������ݴ�ͼƬ��ĿΪ��   " + arrayList.size());
				ScreenRecording sr = new ScreenRecording();                                           //���úϳ���Ƶ����
				sr.makeVideo(arrayList, path, name, 5);                                  //�ϳ���Ƶ�ĺ���

				// ��������е����ݣ����»�ȡͼ��
				arrayList.clear();                                                                    //���arraylist�����ݣ����»�ȡ����
			}
		}, 15 * 1000, 15 * 1000);// ��һ���������еĶ������ڶ��������ǵȴ������룬�����������������Ե��ظ���ʱ��
		
		//System.out.println("���ӳɹ�!");
		
		try {
			ObjectInputStream serverInputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
			int i=0;
			while(true){
				try {
					PacketBean data = null;
					
					while((data = (PacketBean) serverInputStream.readObject()) != null){
					
						byte[] bytes = (byte[])data.getData();
						this.androidName = data.getPacketType();
						//System.out.println("producer�ǣ� " + androidId);
						/*if(i == 0){
							ProducerBean producerBean = new ProducerBean();
							producerBean.setAndroidName(androidName);
							producerBean.setPort(port);
							arrayList2.add(producerBean);
						}*/
						ByteArrayInputStream bin = new ByteArrayInputStream(bytes); 
						this.image = ImageIO.read(bin);  
						
						frame.panel.setImage(image);
						frame.repaint();
						 
						PacketBean data2 = new PacketBean("1","server");
						serverOutputStream.writeObject(data2);
						arrayList.add(image);
		
						
						//serverOutputStream.close();
						serverOutputStream.flush();
						//serverInputStream.close();
						//socket.close();
						
						
						if (i == 0) {
							Thread myThread = new Thread(new MyThread());                                     //����client������̣߳�ֻ����һ��          
							/*try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}*/
							myThread.start();
							i = 1;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Connection Close");
		            break;
				}/*finally{
					serverOutputStream.close();
					serverInputStream.close();
					this.socket.close();
				}*/
				
			} 
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyThread implements Runnable {


		@Override
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(9901);
				System.out.println("S:waiting..............................................");

				while (true) {
					Socket socket = serverSocket.accept();
					System.out.println("S:connecting.......................................................");
					new Thread(new ClientThread(socket)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class ClientThread implements Runnable{
		
		private Socket socket;
		private ObjectOutputStream objectOutputStream;
		private ObjectInputStream objectInputStream;
		
		public ClientThread(Socket socket2){
			this.socket = socket2;
		}

		@Override
		public void run() {
			try {
				objectOutputStream = new ObjectOutputStream(socket.getOutputStream());                                  //������
				objectInputStream = new ObjectInputStream(socket.getInputStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while(true){
					ImageIO.write(image, "jpg", out);                                                                                             //��ʵʱͼƬת����������
					byte[] datas = out.toByteArray();
					try {
						PacketBean data = new PacketBean();                                                                             //���л�����
						data.setData(datas);
						data.setPacketType("sendshots");

						
						objectOutputStream.writeObject(data);
						data = (PacketBean) objectInputStream.readObject();
						out.flush();
						objectOutputStream.flush();
						// objectOutputStream.close();
					}catch (IOException e) {
						System.out.println("Connection Close");
						e.printStackTrace();
						//break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

	}
}
