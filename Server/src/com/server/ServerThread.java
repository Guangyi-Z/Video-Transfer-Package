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

import com.dao.Configuration;
import com.dao.VideoDao;
import com.model.PacketBean;
import com.model.ProducerBean;
import com.server.ScreenRecording;

public class ServerThread implements Runnable {

	private Socket socket;
	private Timer timer;
	private BufferedImage image;
	private ArrayList<BufferedImage> arrayList = new ArrayList<BufferedImage>();
	private ProducerBean producerBean;
	private ImageFrame mFrame;
	private ObjectInputStream serverInputStream;
	private ObjectOutputStream serverOutputStream;


	public ServerThread(Socket socket, ProducerBean producerBean,
			ObjectInputStream objectInputStream,
			ObjectOutputStream objectOutputStream) {
		super();
		this.socket = socket;
		this.producerBean = producerBean;
		this.serverInputStream = objectInputStream;
		this.serverOutputStream = objectOutputStream;
	}

	@Override
	public void run() {
		mFrame = new ImageFrame();
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.setVisible(true);
		
		makeVideo();
		listenClient();
		listenProducer();
		
	}

	private void listenProducer() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				getImageFormProducer();
			}
		}).start();
	}

	private void listenClient() {
			Thread myThread = new Thread(new MyThread()); // 监听client请求的线程，只启动一次
			myThread.start();
	}

	/**
	 * 从Producer端获取图片信息
	 */
	private void getImageFormProducer() {
//		ObjectInputStream serverInputStream = null;
//		ObjectOutputStream serverOutputStream = null;
		try {
//			serverInputStream = new ObjectInputStream(socket.getInputStream());
//			serverOutputStream = new ObjectOutputStream(
//					socket.getOutputStream());
			while (true) {
				try {
					PacketBean data = null;
					while ((data = (PacketBean) serverInputStream.readObject()) != null) {
						if (data.getPacketType() == PacketBean.TYPE_IMAGE) {
							byte[] bytes = (byte[]) data.getData();
							ByteArrayInputStream bin = new ByteArrayInputStream(
									bytes);
							this.image = ImageIO.read(bin);
							mFrame.panel.setImage(image);
							mFrame.repaint();

							PacketBean data2 = new PacketBean(
									PacketBean.SUCCESS, null);
							serverOutputStream.writeObject(data2);
							arrayList.add(image);

							// serverOutputStream.close();
							serverOutputStream.flush();
							// serverInputStream.close();
							// socket.close();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Connection Close");
					break;
				} finally {
					if (serverOutputStream != null) {
						serverOutputStream.close();
					}
					if (serverInputStream != null) {
						serverInputStream.close();
					}
					this.socket.close();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 合成视频
	 */
	private void makeVideo() {
		timer = new Timer(); // 合成视频的周期计时器
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

				VideoDao videoDao = new VideoDao();
				String path = new Configuration().getVideoDir() + producerBean.getAndroidName();
				File file = new File(path);
				if (!file.exists()) {
					// 如果要创建的多级目录不存在才需要创建。
					file.mkdirs();
				}
				String name = year + "_" + month + "_" + date + "@" + hour
						+ "_" + minute + "_" + second + ".mp4"; // 得到系统当前时间，以该时间作为视频文件的命名
				System.out.println("图片缓存的数据存图片数目为：   " + arrayList.size());
				ScreenRecording sr = new ScreenRecording(); // 调用合成视频的类
				boolean flag = sr.makeVideo(arrayList, path + "/", name, 5); // 合成视频的函数
				if(flag){
					videoDao.addVideoPath(producerBean.getAndroidName(), name);
				}else {
					System.out.println("合成视频失败");
				}
				// 清除数组中的数据，重新获取图像
				arrayList.clear(); // 清空arraylist的数据，从新获取数据
			}
		}, 15 * 1000, 15 * 1000);// 第一参数是运行的动作，第二个参数是等待多少秒，第三个参数是周期性的重复的时间s
	}

	class MyThread implements Runnable {

		@Override
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(
						producerBean.getPort());
				System.out
						.println("Client:waiting..............................................");

				while (true) {
					Socket socket = serverSocket.accept();
					System.out
							.println("Client:connecting.......................................................");
					new Thread(new ClientThread(socket)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ClientThread implements Runnable {

		private Socket socket;
		private ObjectOutputStream objectOutputStream;
		private ObjectInputStream objectInputStream;

		public ClientThread(Socket socket2) {
			this.socket = socket2;
		}

		@Override
		public void run() {
			try {
				objectOutputStream = new ObjectOutputStream(
						socket.getOutputStream()); // 对象流
				objectInputStream = new ObjectInputStream(
						socket.getInputStream());
				PacketBean data = null;
				while (true) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ImageIO.write(image, "jpg", out); // 将实时图片转换成流对象
					byte[] datas = out.toByteArray();
					//System.out.println("图像的大小为：" + datas.length);
					try {
						data = new PacketBean(); // 序列化的类
						data.setData(datas);
						data.setPacketType(PacketBean.TYPE_IMAGE);

						//System.out.println("发送数据出去了~~~~~~~~~~~~~~~~~~");
						objectOutputStream.writeObject(data);
						data = (PacketBean) objectInputStream.readObject();
						//System.out.println("接收到回应的数据+++++++++++++");
						out.flush();
						out.close();
						objectOutputStream.flush();
						// objectOutputStream.close();
					} catch (IOException e) {
						System.out.println("Connection Close");
						e.printStackTrace();
						// break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
