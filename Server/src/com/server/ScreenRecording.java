package com.server;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
 
public class ScreenRecording {
     
    public static boolean makeVideo(List<BufferedImage> list, String path, String name, int rate) {
    	if(list.size() == 0)
    		return false;
    	// IMediaWriter to write the file
    	final IMediaWriter writer = ToolFactory.makeWriter(path + name);
    	
        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                   list.get(0).getWidth(), list.get(0).getHeight());
        
        long interval= (long)1000000000 / rate;
        for (int index = 0; index < list.size(); index++) {
            // take the screen shot
            BufferedImage image = list.get(index);
            // convert to the right image type
            BufferedImage bgrScreen = convertToType(image,
                   BufferedImage.TYPE_3BYTE_BGR);
            // encode the image to stream #0
            writer.encodeVideo(0, bgrScreen, index*interval,
                   TimeUnit.NANOSECONDS);
        }
        
        // tell the writer to close and write the trailer if  needed
        writer.close();
        
        return true;
    }
    
    
    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                 sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }
    
}
