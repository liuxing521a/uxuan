package com.uxuan.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 映射行
 * 
 * @author liuzhen(liuxing521a@163.com)
 * @date 2017年5月5日 下午4:41:02
 */
public interface RowMapper<T> {

	 public T mapRow(ResultSet rs) throws SQLException;
	 
}
