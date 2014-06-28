package org.producer;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Queue;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;


class StreamIt implements Camera.PreviewCallback {
//	private ByteArrayOutputStream outstream;
	private Queue<ByteArrayOutputStream> queue= new LinkedList<ByteArrayOutputStream>();
	
	public ByteArrayOutputStream getOutstream() {
		return queue.poll();
	}

//	public void setOutstream(ByteArrayOutputStream outstream) {
//		this.outstream = outstream;
//	}

	
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Size size = camera.getParameters().getPreviewSize();          
        try{
        	//调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);  
            if(image!=null){
            	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, outstream); 
                
                //System.out.println("给我发数据过去！！！！！！！");
                outstream.flush();                        
                queue.add(outstream);
//                outstream.close();
               /* Thread th = new MyThread(outstream,ipname,androidId);                                //启动线程，通过socket发送图片到server
                th.start();    */           
            }  
        }catch(Exception ex){  
            Log.e("Sys","Error:"+ex.getMessage());  
        }        
    }
}
    