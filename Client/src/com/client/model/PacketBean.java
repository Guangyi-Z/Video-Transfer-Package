package com.client.model;

import java.io.Serializable;
/**
 * 
 *服务器传回的数据包
 */
public class PacketBean implements Serializable {

	public static final int PRODUCER_LIST = 1001;
	public int packetType;
	public Object data;//数据段为图片
	
	public PacketBean() {
	}

	public PacketBean(int packetType, Object data) {
		super();
		this.packetType = packetType;
		this.data = data;
	}

	public int getPacketType() {
		return packetType;
	}

	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static int getProducerList() {
		return PRODUCER_LIST;
	}

	@Override
	public String toString() {
		return "PacketBean [packetType=" + packetType + ", data=" + data + "]";
	}

	
}
