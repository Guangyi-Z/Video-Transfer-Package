package com.model;

import java.io.Serializable;

public class ProducerBean implements Serializable{
	private String androidName;
	private String ip;
	private int port;
	private String passwd;

	@Override
	public String toString() {
		return "ProducerBean [androidName=" + androidName + ", ip=" + ip
				+ ", port=" + port + ", passwd=" + passwd + "]";
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
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
