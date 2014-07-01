package com.dao;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.server.utils.JdbcUtils;

public class CreateTables {
	static JdbcTemplate jdbc = new JdbcTemplate(JdbcUtils.getDataSource());
	static MyDaoTemplate mDaoTemplate = new MyDaoTemplate();
	
	/*private static void main(String[] args){
		CreateTables ct = new CreateTables();
		ct.createVideoDirect();
		ct.createVideoName();
	}*/

	/**
	 * 使用 自己写的MyDaoTemplate 创建video的目录表
	 */
	@Test
	public void createVideoDirect() {
		String sql = "create table producer"
					+ "(id integer not null auto_increment primary key,"
					+ "producer_id varchar(1024),"
					+ "nickname varchar(1024),"
					+ "passwd_hashvalue varchar(1024));";
		try {
			mDaoTemplate.update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 使用 开源Sping框架  创建video的目录表
	 */
	@Test
	public void createVideoDirect2() {
		String sql = "create table producer"
				+ "(id integer not null auto_increment primary key,"
				+ "producer_id varchar(1024),"
				+ "nickname varchar(1024),"
				+ "passwd_hashvalue varchar(1024));";
		jdbc.update(sql);
	}
	
	/**
	 * 使用 自己写的MyDaoTemplate  创建video的名称表
	 */
	@Test
	public void createVideoName() {
		String sql = "create table video"
				+ "(id int not null ,"
				+ "name varchar(128)," 
				+ "primary key(id,name)," 
				+ "foreign key(id) references producer(id));";
		try {
			mDaoTemplate.update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		}
	}
	/**
	 *  使用 开源Sping框架  创建video的名称表
	 */
	@Test
	public void createVideoName2() {
		String sql = "create table video"
				+ "(id int not null ,"
				+ "name varchar(128)," 
				+ "primary key(id,name)," 
				+ "foreign key(id) references producer(id));";
		jdbc.update(sql);
	}

}
