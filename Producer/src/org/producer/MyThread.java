package org.producer;

/**
 * @author lican 此类已废弃
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.model.PacketBean;

class MyThread extends Thread{	
	private ByteArrayOutputStream myoutputstream;
	private String ipname;
	private String androidId;
	
	public MyThread(ByteArrayOutputStream myoutputstream,String ipname,String androidId2){
		this.myoutputstream = myoutputstream;
		this.ipname = ipname;
		this.androidId = androidId2;
        try {
			myoutputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public void run() {
        try{
            Socket tempSocket = new Socket(ipname, 7803);
            System.out.println("发起与PC的连接~~~~~~~~~~~··");
            byte[] datas = myoutputstream.toByteArray();
//			PacketBean data = new PacketBean(androidId,datas);    
            //PacketBean data = new PacketBean("producer",datas);    
			
            ObjectOutputStream clientOutputStream = new ObjectOutputStream(tempSocket.getOutputStream()); 
			ObjectInputStream clientInputStream = new ObjectInputStream(tempSocket.getInputStream()); 
//			clientOutputStream.writeObject(data);
			//System.out.println("发送了图片~~~~~~~~~~~~~~~~~~~~");
			
//			data = (PacketBean) clientInputStream.readObject();
			clientInputStream.close(); 
			clientOutputStream.flush();
			clientOutputStream.close();
			
            myoutputstream.flush();
            myoutputstream.close();
            tempSocket.close();                   
        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

}