package com.uxuan.core.jdbc;

import com.uxuan.core.BaseObject;

/**
 * 生成对应sql接口
 * 
 * @author liuzhen(liuxing521a@163.com)
 * @date 2017年5月5日 上午11:43:57
 */
public interface BaseMapper {
	
	/**
	 * 绑定提供类型
	 * 
	 * @param jdbcType
	 * @return
	 */
	boolean bindType(JDBCType jdbcType);
	
	/**
	 * 显示表结构
	 * 
	 * @param item
	 * @return
	 */
	String getTableStruct(BaseObject item);
	
	/**
	 * 创建语句
	 * 
	 * @param item
	 * @return
	 */
	String getCreateTableSQL(BaseObject item);
	
	/**
	 * 修改语句
	 * 
	 * @param item
	 * @return
	 */
	String getAlterTableSQL(BaseObject item);
	
	/**
	 * 查询语句
	 * 
	 * @param item
	 * @return
	 */
	String getSelectSQL(BaseObject item);
	
	/**
	 * 插入语句
	 * 
	 * @param item
	 * @return
	 */
	String getInsertSQL(BaseObject item);
	
	/**
	 * 修改语句
	 * 
	 * @param item
	 * @return
	 */
	String getUpdateSQL(BaseObject item);

	/**
	 * 删除语句
	 * 
	 * @param item
	 * @return
	 */
	String getDeleteSQL(BaseObject item);
	
	public interface BindType {
		
		/**
		 * 绑定提供类型
		 * 
		 * @return
		 */
		boolean bindType(JDBCType jdbcType);
		
	}
	
	
	interface TableStructMapper {
		
		/**
		 * 显示表结构
		 * 
		 * @param item
		 * @return
		 */
		String getTableStruct(BaseObject item);
		
	}
	
	interface CreateTableSQLMapper {
		
		/**
		 * 创建语句
		 * 
		 * @param item
		 * @return
		 */
		String getCreateTableSQL(BaseObject item);
		
	}
	
	interface AlterTableSQLMapper {
	
		/**
		 * 修改语句
		 * 
		 * @param item
		 * @return
		 */
		String getAlterTableSQL(BaseObject item);
		
	}
	
	interface SelectSQLMapper {
		
		/**
		 * 查询语句
		 * 
		 * @param item
		 * @return
		 */
		String getSelectSQL(BaseObject item);
		
	}
	
	interface InsertSQLMapper {
		
		/**
		 * 插入语句
		 * 
		 * @param item
		 * @return
		 */
		String getInsertSQL(BaseObject item);
		
	}
	
	interface UpdateSQL {
		
		/**
		 * 修改语句
		 * 
		 * @param item
		 * @return
		 */
		String getUpdateSQL(BaseObject item);

	}

	interface DeleteSQL {
		
		/**
		 * 删除语句
		 * 
		 * @param item
		 * @return
		 */
		String getDeleteSQL(BaseObject item);
		
	}
	
}
