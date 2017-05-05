package com.uxuan.core.jdbc;

public interface JDBCType {
	
	JdbcBean getBoolBean();
	JdbcBean getBoolWrapBean();
	JdbcBean getByteBean();
	JdbcBean getByteWrapBean();
	JdbcBean getCharBean();
	JdbcBean getCharWrapBean();
	JdbcBean getShortBean();
	JdbcBean getShortWrapBean();
	JdbcBean getIntBean();
	JdbcBean getIntWrapBean();
	JdbcBean getLongBean();
	JdbcBean getLongWrapBean();
	JdbcBean getFloatBean();
	JdbcBean getFloatWrapBean();
	JdbcBean getDoubleBean();
	JdbcBean getDoubleWrapBean();
	JdbcBean getDecimalBean();
	JdbcBean getStringBean();
	JdbcBean getTimeBean();
	JdbcBean getDateBean();
	JdbcBean getTimeStampBean();
	JdbcBean getLocalTimeBean();
	JdbcBean getLocalDateBean();
	JdbcBean getLocalDateTimeBean();
	JdbcBean getPairBean();
	JdbcBean getSetBean();
	JdbcBean getListBean();
	JdbcBean getMapBean();
	JdbcBean getEnumBean();
	JdbcBean getByteArrayBean();
	JdbcBean getSimpleBean();
	
}
