package com.test;

import java.io.Serializable;

public class PacketBean implements Serializable {
	
    public String packetType;
    public Object data;
    
    public PacketBean(){}
    
    public PacketBean(String packetType2,Object data2){
    	packetType = packetType2;
    	data = data2;
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
