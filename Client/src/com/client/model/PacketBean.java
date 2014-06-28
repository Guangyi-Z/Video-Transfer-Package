package com.client.model;

import java.io.Serializable;
/**
 * 
 *服务器传回的数据包
 */
public class PacketBean implements Serializable {

	public String packetType;
	public Object data;//数据段为图片
	
	public PacketBean() {
	}

	public PacketBean(String packetType2, Object data2) {
		packetType = packetType2;
		data = data2;
	}

	
	@Override
	public String toString() {
		return "PacketBean [packetType=" + packetType + ", data=" + data + "]";
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
