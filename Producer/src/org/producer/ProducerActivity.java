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
		sView = (SurfaceView) findViewById(R.id.sView);                 		                                   //������ʾcamera����Ƶ
		surfaceHolder = sView.getHolder();                                                                             //��surfaceHolder
		
		surfaceHolder.addCallback(new Callback() {                                                                   //����surfaceHolder�Ļص�����
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {				
			}
			@Override
			public void surfaceCreated(SurfaceHolder holder) {							                  //��surfaceView�ؼ�������ʱ��ĵ��ú���
				initCamera();                                     
				System.out.println("����ͷ����~~~~~~~~~~");
			}
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {                                         //��surfaceView�ؼ����ٵ�ʱ��ĵ��ú���
				if (camera != null) {
					if (isPreview)
						camera.stopPreview();
					camera.release();
					camera = null;
					System.out.println("����ͷ�ر�~~~~~");
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
				parameters.setPreviewSize(screenWidth, screenHeight);    // ����Ԥ����Ƭ�Ĵ�С				
				parameters.setPreviewFpsRange(20,30);                    // ÿ����ʾ20~30֡				
				parameters.setPictureFormat(ImageFormat.NV21);           // ����ͼƬ��ʽ				
				parameters.setPictureSize(screenWidth, screenHeight);    // ������Ƭ�Ĵ�С
				camera.setPreviewDisplay(surfaceHolder);                 // ͨ��SurfaceView��ʾȡ������		
		        camera.setPreviewCallback(streamIt);         // ���ûص�����				
				camera.startPreview();                                   // ��ʼԤ��				
				camera.autoFocus(null);                                  // �Զ��Խ�
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
			Thread postThread2 = new Thread(new postThread());
			postThread2.start();
			System.out.println("�߳�������@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
    }
    
    class postThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Socket tempSocket = new Socket(ipname,port);
	            System.out.println("������PC������~~~~~~~~~~~����");

	            ObjectOutputStream clientOutputStream = new ObjectOutputStream(tempSocket.getOutputStream()); 
				ObjectInputStream clientInputStream = new ObjectInputStream(tempSocket.getInputStream()); 
				
				//����������Producer��Ϣ
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
			            //System.out.println("ͼƬ���ݵĳ��ȣ� " + datas.length);
						PacketBean data = new PacketBean(PacketBean.TYPE_IMAGE,datas);    
			            //PacketBean data = new PacketBean("producer",datas);    
						
						clientOutputStream.writeObject(data);
						//System.out.println("������ͼƬ~~~~~~~~~~~~~~~~~~~~");
						
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

