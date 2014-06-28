package com.client.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.androidsocket.R;
import com.client.model.ProducerBeans;
import com.test.PacketBean;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 
 * @author caolijie，lican 播放实时视频
 */
public class RealTimeVideoActivity extends Activity {
	private final static boolean DEBUG = true;
	private final static String TAG = "RealTimeVideoActivity";
	private final static int MAX_CACHE_COUNT = 10;
	private final static int MSG_SHOW_IMAGE = 100;
	private ProducerBeans mProducerBeans;
	private ImageView imageView = null;
	private List<Bitmap> mBitmaps = new ArrayList<Bitmap>();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_IMAGE:
				if (!mBitmaps.isEmpty()) {
					Bitmap bitmap = mBitmaps.remove(0);
					imageView.setImageBitmap(bitmap);
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_realtime_video);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBitmaps.clear();
		// if (mRequestVideoThread.isAlive()) {
		// mRequestVideoThread.stop();
		// }
	}

	private void initView() {
		imageView = (ImageView) findViewById(R.id.imgv_realTime); // 绑定图片接收后显示的控件
	}

	private void initData() {
		mProducerBeans = (ProducerBeans) getIntent().getSerializableExtra(
				"producerBeans");
		mRequestVideoThread.start();
		mShowImageThread.start();
	}

	/**
	 * 从图片队列中取出图片
	 */
	Thread mShowImageThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true) {
				if (!mBitmaps.isEmpty()) {
					mHandler.sendEmptyMessage(MSG_SHOW_IMAGE);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});
	
	/**
	 * 接受服务器发送的图片
	 */
	Thread mRequestVideoThread = new Thread(new Runnable() {
		@Override
		public void run() {
			Socket socket = null;
			try {
				socket = new Socket(mProducerBeans.getIp(), mProducerBeans
						.getPort());
				if (DEBUG) {
					Log.e(TAG, "正在连接服务器");
				}
				ObjectInputStream objectInputStream = new ObjectInputStream(
						socket.getInputStream()); // 从socket流中接收数据
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(
						socket.getOutputStream());
				PacketBean data2 = new PacketBean("1", "server");
				try {
					PacketBean dataBean = null; // 将流中数据读出并将其序列化
					while ((dataBean = (PacketBean) objectInputStream
							.readObject()) != null) {
						byte[] bytes = (byte[]) dataBean.getData();
						if (DEBUG) {
							Log.e(TAG,
									"getPacketType:" + dataBean.getPacketType());
						}
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
								bytes.length); // 生成bitmap类型的数据
						if (bitmap != null) {
							if (mBitmaps.size() > MAX_CACHE_COUNT) {
								mBitmaps.remove(0);
							}
							mBitmaps.add(bitmap);
						}
						// String path = Environment.getExternalStorageState()
						// + File.separator + "temp";
						// savaBitmap(path, System.currentTimeMillis()+".png",
						// bmp);
						objectOutputStream.writeObject(data2);
						objectOutputStream.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Connection Close");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	});

	/**
	 * 保存图片到SDCard,测试使用
	 * 
	 * @param path
	 * @param fileName
	 * @param bitmap
	 * @throws IOException
	 */
	public void savaBitmap(String path, String fileName, Bitmap bitmap)
			throws IOException {
		if (bitmap == null) {
			return;
		}
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}
		File file = new File(path, fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}

}