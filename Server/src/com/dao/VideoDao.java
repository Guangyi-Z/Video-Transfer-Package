package com.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mysql.jdbc.log.Log;
import com.server.utils.JdbcUtils;

public class VideoDao {
	// private MyDaoTemplate template = new MyDaoTemplate();
	static JdbcTemplate jdbc = new JdbcTemplate(JdbcUtils.getDataSource());

	@Test
	public void testAddVideoPath(){
		String direct = "G:/video2/aa";
		String name = "video100.mp4";
		VideoDao videoDao = new VideoDao();
		videoDao.addVideoPath(direct, name);
	}
	
	@Test
	public void testGetVideoDirs(){
		VideoDao videoDao = new VideoDao();
		List<String> dirs = videoDao.getVideoDirs();
		System.out.println(dirs);
	}
	
	@Test
	public void testGetVideoName(){
		VideoDao videoDao = new VideoDao();
		List<String> names = videoDao.getVideoNames("G:/video2/aa");
		System.out.println(names);
	}
	
	@Test
	public void testDeleteDirPath(){
		VideoDao videoDao = new VideoDao();
		boolean flag = videoDao.deleteDirPath("G:/video2/aa");
		Assert.assertEquals(true, flag);
	}
	
	/**
	 * 增加一条视频记录
	 * @param direct
	 * @param name
	 * @return
	 */
	public boolean addVideoPath(String direct, String name) {
		int id = findVideoDir(direct);
		if (id == -1) {
			addVideoDir(direct);
			id = findVideoDir(direct);
		}
		String sql = "insert into video_name(id,name) values (?,?);";
		Object[] args = new Object[] {id,name};
		return jdbc.update(sql, args) != 0;
	}

	/**
	 * 增加一个目录
	 * @param direct
	 * @return
	 */
	public boolean addVideoDir(String direct) {
		String sql = "insert into video_direct(direct_path) values (?);";
		Object[] args = new Object[] { direct };
		return jdbc.update(sql,args) != 0;
	}

	/**
	 * 根据目录名找到ID
	 * @param direct
	 * @return
	 */
	public int findVideoDir(String direct) {
		String sql = "select id from video_direct where direct_path =?";
		Object[] args = new Object[] { direct };
		List<Map<String, Object>> list = jdbc.queryForList(sql, args);
		if (list == null || list.isEmpty()) {
			return -1;
		} else {
			Map<String, Object> map = list.get(0);
			int id = (Integer) map.get("id");
			return id;
		}
	}
	
	/**
	 * 获取所有目录名
	 * @return
	 */
	public List<String> getVideoDirs(){
		String sql = "select direct_path from video_direct;";
		List<Map<String, String>> list = jdbc.queryForList(sql);
		List<String> results = new ArrayList<String>();
		for (Map<String, String> map : list) {
			String dir = map.get("direct_path");
			results.add(dir);
		}
		return results;
	}
	/**
	 * 获取某个目录下的所有文件名
	 */
	public List<String> getVideoNames(String dirPath){
		int id = findVideoDir(dirPath);
		if (id == -1) {
			return null;
		}
		String sql = "select name from video_name where id = ?;";
		Object[] args = new Object[]{id};
		List<Map<String, String>> list = jdbc.queryForList(sql,args);
		List<String> results = new ArrayList<String>();
		for (Map<String, String> map : list) {
			String name = map.get("name");
			results.add(name);
		}
		return results;
	}
	
	/**
	 * 删除某个目录
	 * @param dirPath
	 * @return
	 */
	public boolean deleteDirPath(String dirPath){
		int id = findVideoDir(dirPath);
		if (id == -1) {
			return true;
		}
		String sql = "delete from video_name where id = ?;";
		Object[] args = new Object[]{id};
		jdbc.update(sql,args);
		sql = "delete from video_direct where id = ?;";
		jdbc.update(sql,args);
		return true;
	}
}
