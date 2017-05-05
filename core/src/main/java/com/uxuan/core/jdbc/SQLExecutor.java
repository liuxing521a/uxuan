package com.uxuan.core.jdbc;

import java.sql.SQLException;
import java.util.List;

import com.uxuan.core.BaseObject;
import com.uxuan.util.Queue;

/**
 * sql执行器
 * 
 * @author liuzhen(liuxing521a@163.com)
 * @date 2017年5月5日 下午4:42:07
 */
public interface SQLExecutor {
	
	
	/**
	 * 插入单个对象
	 * 
	 * @param item
	 * @return
	 */
	public <T extends BaseObject> int insert(T item) throws SQLException;

	/**
	 * 批量插入
	 * 
	 * @param items
	 * @return
	 */
	public <T extends BaseObject> int[] insertBatch(Queue<T> items) throws SQLException;
	
	/**
	 * 修改单个对象
	 * 
	 * @param item
	 * @return
	 */
	public <T extends BaseObject> int update(T item) throws SQLException;

	/**
	 * 批量修改
	 * 
	 * @param items
	 * @return
	 */
	public <T extends BaseObject> int[] updateBatch(Queue<T> items) throws SQLException;
	
	/**
	 * 删除对象
	 * 
	 * @param item
	 * @return
	 */
	public <T extends BaseObject> int delete(T item) throws SQLException;
	
	/**
	 * 批量删除对象
	 * 
	 * @param items
	 * @return
	 */
	public <T extends BaseObject> int deleteBatch(Queue<T> items) throws SQLException;
	
	/**
	 * 批量删除对象
	 * 
	 * @param clazz
	 * @param idList
	 * @return
	 */
	public <T extends BaseObject> int deleteBatch(Class<T> clazz, Queue<String> idList) throws SQLException;
	
	
	/**
	 * 根据id检索
	 * 
	 * @param clazz 检索对象
	 * @param id
	 * @return
	 */
	public <T extends BaseObject> T select(T data, String id) throws SQLException;

	/**
	 * 根据参数检索列表
	 * 
	 * @param data
	 * @param param
	 * @return
	 */
	public <T extends BaseObject> T select(T data, Parameter param) throws SQLException;
	
	/**
	 * 检索列表
	 * 
	 * @param data
	 * @param param
	 * @return
	 */
	public <T extends BaseObject> List<T> selectArray(T data, Parameter param) throws SQLException;
	
	/**
	 * 分页检索
	 * 
	 * @param data
	 * @param param
	 * @return
	 */
	public <T extends BaseObject> Page<T> selectPage(T data, Parameter param) throws SQLException;
	
	/**
	 * 执行sql
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int execute(String sql) throws SQLException;
	
	/**
	 * 执行sql
	 * 
	 * @param sql
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public int execute(String sql, Object[] args) throws SQLException;
	
	/**
	 * 批量执行sql
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int[] executeBatch(String[] sql) throws SQLException;

	/**
	 * 批量执行sql
	 * 
	 * @param sql
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public int[] executeBatch(String sql, Object[][] args) throws SQLException;

	/**
	 * 查询对象
	 * 
	 * @param mapper
	 * @param sql
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public <T> T queryForObject(RowMapper<T> mapper, String sql, Object[] args) throws SQLException;

	/**
	 * 查询列表
	 * 
	 * @param mapper
	 * @param sql
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public <T> T[] queryForArray(RowMapper<T> mapper, String sql, Object[] args) throws SQLException;

}