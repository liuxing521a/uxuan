package com.uxuan.core.jdbc;

import com.uxuan.util.Objects;

public final class JdbcBean {
	

	private final Class<?> javaType;
	private final String dialect;
	private final String defaultLength;
	private final String defaultValue;
	
	public JdbcBean(Class<?> javaType, String dialect, String defaultLength, String defaultValue) {
		this.javaType = javaType;
		this.dialect = dialect;
		this.defaultLength = defaultLength;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * 对应java类型
	 * 
	 * @return
	 */
	public Class<?> getJavaType() {
		return javaType;
	}

	/**
	 * sql方言（对应数据类型名）
	 * 
	 * @return
	 */
	public String getDialect() {
		return dialect;
	}

	/**
	 * 默认长度
	 * 
	 * @return
	 */
	public String getDefaultLength() {
		return defaultLength;
	}

	/**
	 * 缺省值
	 * 
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * 转换修改|创建sql后缀  <br>
	 * 
	 * @param name
	 * @return
	 */
	public String getLineSQL(String name) {
		return getLineSQL(name, defaultLength, null);
	}

	/**
	 * 转换修改|创建sql后缀  <br>
	 * 
	 * @param name
	 * @param length
	 * @return
	 */
	public String getLineSQL(String name, String length) {
		return getLineSQL(name, length, null);
	}


	/**
	 * 转换修改|创建sql后缀  <br>
	 * 例如SQL: alter table t_test add name varchar(32) default null comment '姓名';
	 * 获取内容为 “name varchar(32) default null comment '姓名'”
	 * 
	 * @param name
	 * @param length
	 * @param comment
	 * @return
	 */
	public String getLineSQL(String name, String length, String comment) {
		StringBuffer buf = new StringBuffer();
		buf.append("`").append(name).append("` ").append(dialect);
		
		if (Objects.nonEmpty(length)) {
			buf.append("(").append(length).append(")");
		}
		
		if (Objects.nonNull(defaultValue)) {
			buf.append(" NOT NULL DEFAULT '").append("defaultValue").append("'");
		}
		
		if (Objects.nonEmpty(comment)) {
			buf.append(" comment ").append(comment);
		}
		
		return buf.toString();
	}
	
}
