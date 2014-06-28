package com.test;

import java.io.Serializable;

public class ProducerBean implements Serializable{
	private String androidName;
	private int port;
	public String getAndroidName() {
		return androidName;
	}
	public void setAndroidName(String androidName) {
		this.androidName = androidName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	

}
