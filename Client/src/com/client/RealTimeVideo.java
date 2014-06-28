package com.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.androidsocket.R;
import com.test.PacketBean;

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

public class RealTimeVideo extends Activity {  
	  
    private ImageView imageView = null;  
    private boolean flag=false;
    
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.image);  
  
        imageView = (ImageView) findViewById(R.id.image01);          //绑定图片接收后显示的控件
        Button btn = (Button) findViewById(R.id.Button01);  
        btn.setOnClickListener(new OnClickListener() {              //按钮点击的监听器，负责开启线程请求server发送实时视频，flag用作接收的开关
            public void onClick(View v) {  
            	
            	if(flag){
					flag = true;
				}
				else
				{
					flag  = false;
				}
            	t.start();
            }  
        });
        
    }
    
    Thread t=new Thread(new Runnable() {                         //请求server发送实时视频的线程
		@Override
		public void run() {
			int i=0;
			if(!flag)
			{
				handler.sendEmptyMessage(i);
			}else {
				i=1;
				handler.sendEmptyMessage(i);
			}
		}
	});
	
	
    private Handler handler = new Handler() {  
        @Override  
       public void handleMessage(Message msg) {  
            switch(msg.what){
            case 0:
            	Socket socket = null;  
                try {
                	System.out.println("点击了这个按钮++++++++++++++");
    				socket = new Socket("192.168.253.1",9901);  
    				System.out.println("连接服务器。。。。。。。。。。。。。。。");
    				ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());                        //从socket流中接收数据
    				ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    				//System.out.println("得到图片。。1111111111111。。");                               
    				PacketBean data2 = new PacketBean("1","server");
    				try {
    					PacketBean dataBean = null;                                                                                   //将流中数据读出并将其序列化
    					while((dataBean = (PacketBean) objectInputStream.readObject()) != null){
    						System.out.println("转换序列化格式图片成功~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    						byte[] bytes = (byte[]) dataBean.getData();
    						System.out.println("从server端发送过来的字符串： " + dataBean.getPacketType());
    						Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);                         //生成bitmap类型的数据
    						String path = Environment.getExternalStorageState()+File.separator+"temp";
    						//savaBitmap(path, System.currentTimeMillis()+".png", bmp);
    						System.out.println("显示图片了。。。。。。");
    						Log.e("save", "保存图片到绝对路径下。。。。。。。。");
    						imageView.setImageBitmap(bmp);                                                                               //显示图片，以形成视频
    						System.out.println("成功￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥");
    						
    						objectOutputStream.writeObject(data2);
    						objectOutputStream.flush();
    						//Thread.sleep(1000);
    					}
    				} catch (Exception e) {
    					// TODO: handle exception
    					e.printStackTrace();
    					System.out.println("Connection Close");
    					//break;
    				}

                	//objectInputStream.close();
				    //socket.close();
                } catch (IOException e) {  
                    e.printStackTrace();  
                } 
         	   break;
            case 1:
            	System.out.println("不请求服务器了");
            	break;
			}
        }
    };  
    
    public void savaBitmap(String path, String fileName, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return;
        }
//		String path = getStorageDirectory();
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