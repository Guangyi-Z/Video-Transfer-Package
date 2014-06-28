package com.client;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.androidsocket.R;
import com.test.Employee;

import android.app.Activity;
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.provider.Settings.Secure;

public class TestActivity2 extends Activity {  
	  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.image);  
        
        Button btn = (Button) findViewById(R.id.Button01);  
        btn.setOnClickListener(new OnClickListener() {              //按钮点击的监听器，负责开启线程请求server发送实时视频，flag用作接收的开关
            public void onClick(View v) {
            	try {
            		System.out.println("按钮被点击了~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                	Socket socket = new Socket("192.168.191.4", 10000);
                    
                    OutputStream os = socket.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osw);
                    int i=0;
                    while (true) {
                    	String android_id = Secure.getString(TestActivity2.this.getContentResolver(),Secure.ANDROID_ID);   
                        String time = android_id+"---" + i;
                        try {
                        	System.out.println(time);
                            bw.write(time); // 发送当前时间的字符串
                            bw.newLine(); // 需要换行符做readLine()
                            bw.flush(); 
                            i++;
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            System.out.println("Connection Close");
                            break;
                        }
                    }
				} catch (Exception e) {
					// TODO: handle exception
				}
            	
            } 
        });
    }
    
}