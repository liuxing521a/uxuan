package com.uxuan.core.jdbc.support;

import com.jolbox.bonecp.BoneCPConfig;
import com.uxuan.core.jdbc.DataSource;
import com.uxuan.util.config.Config;

/**
 * mysql 数据源
 * 
 * @author liuzhen(liuxing521a@163.com)
 * @date 2017年5月5日 下午2:50:39
 */
public class MysqlDataSource extends DataSource {

	public MysqlDataSource(Config config) {
		super(new BoneCPConfig());

		this.config.setJdbcUrl(config.getString("url"));
		this.config.setUsername(config.getString("userName"));
		this.config.setPassword(config.getString("password"));
		this.config.setAcquireRetryAttempts(Short.MAX_VALUE);
		this.config.setPartitionCount(config.getInt("partitionCount"));
		this.config.setAcquireIncrement(config.getInt("acquireIncrement"));
		this.config.setMinConnectionsPerPartition(config.getInt("minConnPerPart"));
		this.config.setMaxConnectionsPerPartition(config.getInt("maxConnPerPart"));
		this.config.setPoolAvailabilityThreshold(config.getInt("poolAvailabilityThreshold"));
		
		//this.config.setAcquireRetryDelay(8000);
		this.config.setLogStatementsEnabled(true);
	}
	
	public MysqlDataSource(BoneCPConfig config) {
		super(new BoneCPConfig());

	}

}
