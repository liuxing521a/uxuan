package com.uxuan.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.uxuan.util.Objects;

/**
 * 数据库连接池
 * 
 * @author liuzhen(liuxing521a@163.com)
 * @date 2017年5月5日 下午2:23:55
 */
public abstract class DataSource {
	
	/** 连接池 */
	private BoneCP dbPool;

	/** 连接池配置*/
	protected BoneCPConfig config;

	protected DataSource(BoneCPConfig config) {
		this.config = config;
	}

	/**
	 * 初始化连接
	 * 
	 * @return
	 */
	public boolean initialize() throws SQLException {
		if (Objects.isNull(dbPool)) {
			return false;
		}
		
		dbPool = new BoneCP(config);
		return true;
	}
	
	/**
	 * 销毁连接池
	 * 
	 * @return
	 */
	public boolean destroy() {
		if (Objects.nonNull(dbPool)) {
			dbPool.shutdown();
			dbPool = null;
			return true;
		}

		return false;
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return dbPool.getConnection();
	}
	

}

