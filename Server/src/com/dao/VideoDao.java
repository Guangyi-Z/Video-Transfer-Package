package com.dao;
import java.io.File;
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
		String direct = "G:/video2/aa3";
		String name = "video3.mp4";
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
	
	@Test
	public void testGetVideoDirect(){
		VideoDao videoDao = new VideoDao();
		String string = videoDao.getVideoDirect(2);
		System.out.println(string);
	}
	
	@Test
	public void testDeleteVideoName(){
		VideoDao videoDao = new VideoDao();
		boolean flag = videoDao.deleteVideo("G:/video2/aa", "video100.mp4");
		if (flag) {
			System.err.println("删除视频成功了！！");
		}else {
			System.out.println("删除视频失败~~~~~~");
		}
	}
	
	/**
	 * 在数据库中增加一条视频记录
	 * @param direct
	 * @param name
	 * @return
	 */
	public boolean addVideoPath(String direct, String name) {
		int id = getVideoDir(direct);
		if (id == -1) {
			addVideoDir(direct);
			id = getVideoDir(direct);
		}
		String sql = "insert into video_name(id,name) values (?,?);";
		Object[] args = new Object[] {id,name};
		return jdbc.update(sql, args) != 0;
	}

	/**
	 * 在数据库中video_direct表增加一个目录记录
	 * @param direct
	 * @return
	 */
	public boolean addVideoDir(String direct) {
		String sql = "insert into video_direct(direct_path) values (?);";
		Object[] args = new Object[] { direct };
		return jdbc.update(sql,args) != 0;
	}

	/**
	 * 根据目录名找到数据库中对应的id
	 * @param direct
	 * @return
	 */
	public int getVideoDir(String direct) {
		String sql = "select id from video_direct where direct_path =?;";
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
	 * 获取数据库video_direct表中所有目录名
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
	 * 输入目录名，获取某个数据库中video_direct表的目录下的所有视频文件名
	 */
	public List<String> getVideoNames(String dirPath){
		int id = getVideoDir(dirPath);
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
	 * 输入视频文件名，得到数据库中对应的目录id号
	 * @param videoName
	 * @return
	 */
	public int getVideoId(String videoName){
		String sql = "select id from video_name where name = ?;";
		Object[] args = new Object[]{videoName};
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
	 * 输入id号，查询数据库中video_direct表中对应的目录名
	 * @param id
	 * @return
	 */
	public String getVideoDirect(int id){
		String sql = "select direct_path from video_direct where id = ?;";
		Object[] args = new Object[]{id};
		List<Map<String, String>> list = jdbc.queryForList(sql,args);
		List<String> results = new ArrayList<String>();
		for (Map<String, String> map : list) {
			String dir = map.get("direct_path");
			results.add(dir);
		}
		if (results == null || results.isEmpty()) {
			return "nothing";
		} else {
			String path = results.get(0);
			return path;
		}
	}

	/**
	 * 输入目录名，删除数据库中video_direct表某个目录名记录
	 * @param dirPath
	 * @return
	 */
	public boolean deleteDirPath(String dirPath){
		int id = getVideoDir(dirPath);
		if (id == -1) {
			return true;
		}
		String sql = "delete from video_name where id = ?;";
		Object[] args = new Object[]{id};
		jdbc.update(sql,args);
		sql = "delete from video_direct where id = ?;";
		jdbc.update(sql,args);
		File file = new File(dirPath);
		deleteFile(file);
		return true;
	}
	
	/**
	 * 输入文件名，删除数据库中video_name表中的视频文件记录
	 * @param videoName
	 * @return
	 */
	public boolean deleteVideo(String direct,String videoName){
		int id = getVideoDir(direct);
		if (id == -1) {
			return true;
		}
		String sql = "delete from video_name where id = ? and name = ?;";
		Object[] args = new Object[]{id,videoName};
		jdbc.update(sql,args);
		File file = new File(direct + "/" + videoName);
		deleteFile(file);
		return true;
	}
	
	/**
	 * 在本地删除文件或目录
	 * @param file
	 */
	public void deleteFile(File file) {
		if (file.exists()){ // 判断文件是否存在
			if (file.isFile()){ // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()){ // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for(int i = 0; i < files.length; i++){ // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			System.out.println("所删除的文件不存在！" + '\n');
		}
	}
}
