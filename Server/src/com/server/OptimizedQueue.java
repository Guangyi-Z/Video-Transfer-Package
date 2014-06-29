package com.server;

import java.util.LinkedList;

public class OptimizedQueue {

	private final int max= 30;
	
	private LinkedList<Object> queue= null;
	
	public OptimizedQueue(){
		queue= new LinkedList<Object>();
	}
	
	/**
	 * return null if no items reserved
	 * @return
	 */
	public Object getItem(){
		return queue.poll();
	}
	
	/** 
	 * add item at the tail of queue 
	 * @param o
	 */
	public void addItem(Object o){
		if(queue.size() >= max){
			int size= queue.size();
			for(int i= 0; i<size; i+=2){
				queue.remove(i);
			}
		}
		queue.offer(o);
	}
	
}
