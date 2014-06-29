package com.model;

import java.io.Serializable;
/**
 * 
 *服务器传回的数据包
 */
public class PacketBean implements Serializable {

	public static final int PRODUCER_LIST = 1001;
	public static final int PRODUCER_INFO = 1002;
	public static final int SUCCESS = 200;
	public static final int TYPE_IMAGE = 2000;
	public static final int FAILED = 404;
	public int packetType;
	public Object data;
	
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
