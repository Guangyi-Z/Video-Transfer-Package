package com.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Configuration {

	private static String config_name= "config.properties";
	private static String tag_video_dir= "video_directory";
	
	/** 
	 * return null if property value not exists
	 * @return
	 */
	public String getVideoDir(){
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			String dir= getClass().getClassLoader().getResource(".").getPath()
					.replace(File.separator + "bin" + File.separator, 
							File.separator + "src" + File.separator); // get the src dir of project
	        File file = new File(dir + config_name);
			input = new FileInputStream(file);
	 
			// load a properties file
			prop.load(input);
	 
			// get the property value and print it out
			return prop.getProperty(tag_video_dir);
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		return null;
	}
}
