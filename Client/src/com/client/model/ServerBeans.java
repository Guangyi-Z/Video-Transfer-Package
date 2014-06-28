package com.client.model;

import java.io.Serializable;

public class ServerBeans implements Serializable{
	private String ip;//服务器的IP地址
	private int port;//服务器的端口号
	private String name;//服务器的名称
	
	
	@Override
	public String toString() {
		return "ServerBeans [ip=" + ip + ", port=" + port + ", name=" + name
				+ "]";
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
