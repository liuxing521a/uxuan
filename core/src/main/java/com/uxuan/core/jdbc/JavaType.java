package com.uxuan.core.jdbc;

import com.uxuan.util.Ioc;

/**
 * 提供基础类型
 * 
 * @author liuzhen(liuxing521a@163.com)
 * @date 2017年5月5日 上午11:44:24
 */
public enum JavaType {
	
	/**boolean 类型*/
	BOOL {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getBoolBean();
		}
	},
	/**Boolean 类型*/
	BOOLWRAP {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getBoolWrapBean();
		}
	} ,
	/**byte 类型*/
	BYTE {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getByteBean();
		}
	},
	/**Byte 类型*/
	BYTEWRAP {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getByteWrapBean();
		}
	},
	/**char 类型*/
	CHAR {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getCharBean();
		}
	},
	/**Charwrap 类型*/
	CHARWRAP {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getCharWrapBean();
		}
	},
	/**short 类型*/
	SHORT {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getShortBean();
		}
	},
	/**Short 类型*/
	SHORTWRAP {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getShortWrapBean();
		}
	},
	/**int 类型*/
	INT {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getIntBean();
		}
	},
	/**Integer 类型*/
	INTWRAP {
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getIntWrapBean();
		}
	},
	/**long 类型*/
	LONG{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getLongBean();
		}
	},
	
	/**Long 类型*/
	LONGWRAP{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getLongWrapBean();
		}
	},
	/**float 类型*/
	FLOT{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getFloatBean();
		}
	},
	/**Float 类型*/
	FLOTWRAP{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getFloatWrapBean();
		}
	},
	/**double 类型*/
	DOUBLE{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getDoubleBean();
		}
	},
	/**Double 类型*/
	DOUBLEWRAP{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getDoubleWrapBean();
		}
	},
	/**BigDecimal 类型*/
	DECIMAL{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getDecimalBean();
		}
	},
	/**String 类型*/
	STRING{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getStringBean();
		}
	},
	
	/**java.sql.Time 类型*/
	TIME{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getTimeBean();
		}
	},
	/**java.sql.Date 类型*/
	DATE{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getDateBean();
		}
	},
	/**java.sql.TimeStamp 类型*/
	TIMESTAMP{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getTimeStampBean();
		}
	},
	/**java.time.LocalDate类型*/
	LOCALDATE{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getLocalDateBean();
		}
	},
	/**java.time.Localtime 类型*/
	LOCALTIME{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getLocalTimeBean();
		}
	},
	/**java.time.Localdatetime 类型*/
	LOCALDATETIME{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getLocalDateTimeBean();
		}
	},
	/**单个 key->value 类型*/
	PAIR{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getPairBean();
		}
	},
	/**java.util.Set 类型*/
	SET{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getSetBean();
		}
	},
	/**java.util.List 类型*/
	LIST{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getListBean();
		}
	},
	/**java.util.Map 类型*/
	MAP{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getMapBean();
		}
	},
	/** 枚举类型*/
	Enum{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getEnumBean();
		}
	},
	/** 字节数组(byte[]) 类型*/
	BYTEARRAY{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getByteArrayBean();
		}
	},
	/** 简单对象(Simple) 类型*/
	SIMPLE{
		@Override
		public JdbcBean getJdbcBean() {
			return jdbcType.getSimpleBean();
		}
	}
	;
	
	JDBCType jdbcType;
	
	private JavaType() {
		jdbcType = Ioc.getInstance(JDBCType.class);
	}
	
	public abstract JdbcBean getJdbcBean();
	
}
