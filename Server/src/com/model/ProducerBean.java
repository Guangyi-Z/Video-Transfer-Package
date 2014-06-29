package com.model;

import java.io.Serializable;

public class ProducerBean implements Serializable{
	private String androidName;
	private String ip;
	private int port;
	
	
	@Override
	public String toString() {
		return "ProducerBean [androidName=" + androidName + ", ip=" + ip
				+ ", port=" + port + "]";
	}
	public String getAndroidName() {
		return androidName;
	}
	public void setAndroidName(String androidName) {
		this.androidName = androidName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	

}
