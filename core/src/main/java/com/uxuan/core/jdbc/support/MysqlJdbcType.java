package com.uxuan.core.jdbc.support;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.uxuan.core.Simple;
import com.uxuan.core.jdbc.JDBCType;
import com.uxuan.core.jdbc.JdbcBean;
import com.uxuan.util.Pair;

public class MysqlJdbcType implements JDBCType {

	@Override
	public JdbcBean getBoolBean() {
		return new JdbcBean(boolean.class, "TINYINT", "1", "0");
	}

	@Override
	public JdbcBean getBoolWrapBean() {
		return new JdbcBean(Boolean.class, "TINYINT", "1", "0");
	}

	@Override
	public JdbcBean getByteBean() {
		return new JdbcBean(byte.class, "TINYINT", "3", "0");
	}

	@Override
	public JdbcBean getByteWrapBean() {
		return new JdbcBean(Byte.class, "TINYINT", "3", "0");
	}

	@Override
	public JdbcBean getCharBean() {
		return new JdbcBean(char.class, "CHAR", "1", " ");
	}

	@Override
	public JdbcBean getCharWrapBean() {
		return new JdbcBean(Character.class, "CHAR", "1", null);
	}

	@Override
	public JdbcBean getShortBean() {
		return new JdbcBean(short.class, "SMALLINT", "5", "0");
	}


	@Override
	public JdbcBean getShortWrapBean() {
		return new JdbcBean(Short.class, "SMALLINT", "5", null);
	}

	@Override
	public JdbcBean getIntBean() {
		return new JdbcBean(int.class, "INT", "10", "0");
	}

	@Override
	public JdbcBean getIntWrapBean() {
		return new JdbcBean(Integer.class, "INT", "10", null);
	}

	@Override
	public JdbcBean getLongBean() {
		return new JdbcBean(long.class, "BIGINT", "18", "0");
	}

	@Override
	public JdbcBean getLongWrapBean() {
		return new JdbcBean(Long.class, "BIGINT", "18", null);
	}

	@Override
	public JdbcBean getFloatBean() {
		return new JdbcBean(float.class, "FLOAT", "8,2", "0.0");
	}

	@Override
	public JdbcBean getFloatWrapBean() {
		return new JdbcBean(Float.class, "FLOAT", "8,2", null);
	}

	/* (non-Javadoc)
	 * @see com.uxuan.core.jdbc.JDBCType#getDoubleBean()
	 */
	@Override
	public JdbcBean getDoubleBean() {
		return new JdbcBean(double.class, "DOUBLE", "14,4", "0.0");
	}

	@Override
	public JdbcBean getDoubleWrapBean() {
		return new JdbcBean(Double.class, "DOUBLE", "14,4", null);
	}

	@Override
	public JdbcBean getDecimalBean() {
		return new JdbcBean(BigDecimal.class, "DOUBLE", "16,8", null);
	}

	@Override
	public JdbcBean getStringBean() {
		return new JdbcBean(String.class, "VARCHAR", "40", null);
	}

	@Override
	public JdbcBean getTimeBean() {
		return new JdbcBean(Time.class, "TIME", null, null);
	}

	@Override
	public JdbcBean getDateBean() {
		return new JdbcBean(Date.class, "DATE", null, null);
	}

	@Override
	public JdbcBean getTimeStampBean() {
		return new JdbcBean(Timestamp.class, "TIMESTAMP", null, null);
	}

	@Override
	public JdbcBean getLocalTimeBean() {
		return new JdbcBean(Time.class, "TIME", null, null);
	}


	@Override
	public JdbcBean getLocalDateBean() {
		return new JdbcBean(Date.class, "DATE", null, null);
	}


	@Override
	public JdbcBean getLocalDateTimeBean() {
		return new JdbcBean(Timestamp.class, "TIMESTAMP", null, null);
	}


	@Override
	public JdbcBean getPairBean() {
		return new JdbcBean(Pair.class, "TEXT", null, null);
	}

	@Override
	public JdbcBean getSetBean() {
		return new JdbcBean(Set.class, "TEXT", null, null);
	}

	@Override
	public JdbcBean getListBean() {
		return new JdbcBean(List.class, "TEXT", null, null);
	}

	@Override
	public JdbcBean getMapBean() {
		return new JdbcBean(Map.class, "TEXT", null, null);
	}

	@Override
	public JdbcBean getEnumBean() {
		return new JdbcBean(Enum.class, "VARCHAR", "32", null);
	}

	@Override
	public JdbcBean getByteArrayBean() {
		return new JdbcBean(byte[].class, "BLOB", null, null);
	}

	@Override
	public JdbcBean getSimpleBean() {
		return new JdbcBean(Simple.class, "VARCHAR", "40", null);
	}

}
