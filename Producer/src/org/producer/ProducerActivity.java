package org.producer;


import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.wanghai.CameraTest.R;

import com.model.PacketBean;
import com.model.ProducerBean;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class ProducerActivity extends Activity {
	private SurfaceView sView;
	private SurfaceHolder surfaceHolder;
	private int screenWidth, screenHeight;	
	private Camera camera;                    
	private boolean isPreview = false;        
	private String ipname;
	private String passwd;
	private int port;
	private String androidId;
	private StreamIt streamIt;

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
     	requestWindowFeature(Window.FEATURE_NO_TITLE);
     	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preview);
        
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        ipname = data.getString("ipname");
        passwd = data.getString("passwd");
        //ipname = "192.168.253.1";
        port = 9901;

        String android_id = Secure.getString(ProducerActivity.this.getContentResolver(),Secure.ANDROID_ID);   
        this.androidId = android_id;
        		
		screenWidth = 640;
		screenHeight = 480;
		sView = (SurfaceView) findViewById(R.id.sView);                 		                                   //用于显示camera的视频
		surfaceHolder = sView.getHolder();                                                                             //绑定surfaceHolder
		
		surfaceHolder.addCallback(new Callback() {                                                                   //增加surfaceHolder的回调函数
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {				
			}
			@Override
			public void surfaceCreated(SurfaceHolder holder) {							                  //当surfaceView控件产生的时候的调用函数
				initCamera();                                     
				System.out.println("摄像头打开了~~~~~~~~~~");
			}
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {                                         //当surfaceView控件销毁的时候的调用函数
				if (camera != null) {
					if (isPreview)
						camera.stopPreview();
					camera.release();
					camera = null;
					System.out.println("摄像头关闭~~~~~");
				}
			    System.exit(0);
			}		
		});
		
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
    }
    
    private void initCamera() {
    	if (!isPreview) {
			camera = Camera.open();
		}
		if (camera != null && !isPreview) {
			streamIt = new StreamIt();
			try{
				Camera.Parameters parameters = camera.getParameters();				
				parameters.setPreviewSize(screenWidth, screenHeight);    // 设置预览照片的大小				
				parameters.setPreviewFpsRange(20,30);                    // 每秒显示20~30帧				
				parameters.setPictureFormat(ImageFormat.NV21);           // 设置图片格式				
				parameters.setPictureSize(screenWidth, screenHeight);    // 设置照片的大小
				camera.setPreviewDisplay(surfaceHolder);                 // 通过SurfaceView显示取景画面		
		        camera.setPreviewCallback(streamIt);         // 设置回调的类				
				camera.startPreview();                                   // 开始预览				
				camera.autoFocus(null);                                  // 自动对焦
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
			Thread postThread2 = new Thread(new postThread());
			postThread2.start();
			System.out.println("线程启动了@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
    }
    
    class postThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Socket tempSocket = new Socket(ipname,port);
	            System.out.println("发起与PC的连接~~~~~~~~~~~··");

	            ObjectOutputStream clientOutputStream = new ObjectOutputStream(tempSocket.getOutputStream()); 
				ObjectInputStream clientInputStream = new ObjectInputStream(tempSocket.getInputStream()); 
				
				//向服务器输出Producer信息
				PacketBean packetBean = new PacketBean();
				ProducerBean producerBean = new ProducerBean();
				producerBean.setAndroidName(androidId);
				producerBean.setPasswd(passwd);
				packetBean.setPacketType(PacketBean.PRODUCER_INFO);
				packetBean.setData(producerBean);
				clientOutputStream.writeObject(packetBean);
				clientOutputStream.flush();
				clientInputStream.readObject();
				ByteArrayOutputStream myoutputstream = new ByteArrayOutputStream();
				
	            while(true){
	            	try {
	            		myoutputstream = streamIt.getOutstream();
			            byte[] datas = myoutputstream.toByteArray();
			            //System.out.println("图片数据的长度： " + datas.length);
						PacketBean data = new PacketBean(PacketBean.TYPE_IMAGE,datas);    
			            //PacketBean data = new PacketBean("producer",datas);    
						
						clientOutputStream.writeObject(data);
						//System.out.println("发送了图片~~~~~~~~~~~~~~~~~~~~");
						
						data = (PacketBean) clientInputStream.readObject();
						//clientInputStream.close(); 
						clientOutputStream.flush();
						//clientOutputStream.close();
						
			            myoutputstream.flush();
			            //myoutputstream.close();
			            //Thread.sleep(2000);
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Connection Close~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		                //break;
					}/*finally{
						clientInputStream.close();
						clientOutputStream.close();
						tempSocket.close();
					}*/
	            }
	            //tempSocket.close();   
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			 
		}
    	
    }
    
}

