package com.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.androidsocket.R;
import com.test.Employee;

import android.app.Activity;
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.provider.Settings.Secure;

public class TestActivity extends Activity {  
	  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.image);  
        
        Button btn = (Button) findViewById(R.id.Button01);  
        btn.setOnClickListener(new OnClickListener() {              //按钮点击的监听器，负责开启线程请求server发送实时视频，flag用作接收的开关
            public void onClick(View v) {
            	System.out.println("按钮被点击了~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            	String android_id = Secure.getString(TestActivity.this.getContentResolver(),Secure.ANDROID_ID);
            	System.out.println(android_id+"----------------------------+++++++++++++---------------------------");
            	
            	try {
        			Employee joe = new Employee(150, "client"); 
        			/*Employee joe = null;
        			joe.setEmployeeNumber(123);
        			joe.setEmployeeName("nimei");*/
        			
        			//System.out.println("employeeNumber= " + joe .getEmployeeNumber()); 
        			//System.out.println("employeeName= " + joe .getEmployeeName()); 
        			Socket socketConnection = new Socket("172.27.35.11", 11100); 
        			
        			ObjectOutputStream clientOutputStream = new ObjectOutputStream(socketConnection.getOutputStream()); 
        			ObjectInputStream clientInputStream = new ObjectInputStream(socketConnection.getInputStream()); 
        			clientOutputStream.writeObject(joe); 
        			
        			joe= (Employee)clientInputStream.readObject(); 
        			System.out.println("+++++++++++++++++++++++++++++employeeNumber= " + joe .getEmployeeNumber()); 
        			System.out.println("+++++++++++++++++++++++++++++employeeName= " + joe .getEmployeeName());
        			
        			clientOutputStream.close(); 
        			clientInputStream.close(); 
        		} catch (Exception e) {
        			System.out.println(e); 
        		} 
            } 
        });
    }
    
}