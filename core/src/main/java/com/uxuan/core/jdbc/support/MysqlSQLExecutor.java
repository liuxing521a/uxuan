package com.uxuan.core.jdbc.support;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uxuan.core.BaseObject;
import com.uxuan.core.jdbc.DataSource;
import com.uxuan.core.jdbc.Page;
import com.uxuan.core.jdbc.Parameter;
import com.uxuan.core.jdbc.RowMapper;
import com.uxuan.core.jdbc.SQLExecutor;
import com.uxuan.util.Queue;

public class MysqlSQLExecutor implements SQLExecutor {

	private DataSource dataSource;

	public MysqlSQLExecutor(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	

	@Override
	public <T extends BaseObject> int insert(T item) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends BaseObject> int[] insertBatch(Queue<T> items) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends BaseObject> int update(T item) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends BaseObject> int[] updateBatch(Queue<T> items) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends BaseObject> int delete(T item) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public <T extends BaseObject> int deleteBatch(Queue<T> items) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends BaseObject> int deleteBatch(Class<T> clazz, Queue<String> idList) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends BaseObject> T select(T data, String id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends BaseObject> T select(T data, Parameter param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <T extends BaseObject> List<T> selectArray(T data, Parameter param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <T extends BaseObject> Page<T> selectPage(T data, Parameter param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int execute(String sql) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);

			return ps.executeUpdate();
		} finally {
			close(ps, conn);
		}
	}

	@Override
	public int execute(String sql, Object[] args) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = buildStatement(conn, sql, args);

			return ps.executeUpdate();
		} finally {
			close(ps, conn);
		}
	}


	@Override
	public int[] executeBatch(String[] sqlArray) throws SQLException {
		Connection conn = null;
		Statement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.createStatement();

			for (String sql : sqlArray) {
				ps.addBatch(sql);
			}

			return ps.executeBatch();
		}  finally {
			close(ps, conn);
		}
	}

	@Override
	public int[] executeBatch(String sql, Object[][] args) throws SQLException {
		Connection conn = null;
		Statement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = buildStatement(conn, sql, args);

			return ps.executeBatch();
		} finally {
			close(ps, conn);
		}
	}


	@Override
	public <T> T queryForObject(RowMapper<T> mapper, String sql, Object[] args) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			ps = buildStatement(conn, sql, args);
			rs = ps.executeQuery();

			return rs.next() ? mapper.mapRow(rs) : null;
		} finally {
			close(rs, ps, conn);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] queryForArray(RowMapper<T> mapper, String sql, Object[] args) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = buildStatement(conn, sql, args);
			rs = ps.executeQuery();
			
			final int maxRow = getRowCount(rs);

			Object[] datas = new Object[maxRow];
			int index = 0;
			while (rs.next()) {
				datas[index++] = mapper.mapRow(rs);
			}

			return (T[]) datas;
		} finally {
			close(conn, ps, rs);
		}
	}

	public Set<String> tableColumns(String tableName) 
	throws SQLException {
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			DatabaseMetaData data = conn.getMetaData();
			ResultSet result = data.getTables(null, "%", "%", new String[] {"TABLE", "VIEW" });
			if (!result.next()) {
				throw new SQLException("unkown database meta info...");
			}

			String dbName = result.getString("TABLE_CAT");

			String sql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?;";
			ppst = conn.prepareStatement(sql);
			ppst.setString(1, tableName);
			ppst.setString(2, dbName);
			rs = ppst.executeQuery();

			Set<String> columns = new HashSet<String>();
			while (rs.next()) {
				columns.add(rs.getString("column_name"));
			}

			return columns;
		} finally {
			close(conn, ppst, rs);
		}
	}

	
	private PreparedStatement buildStatement(Connection conn, String sql, Object[] args)  throws SQLException {
		sql = expandArrayParams(sql, args);
		PreparedStatement ps = conn.prepareStatement(sql);
		if (args.length > 0) {
			setBindVariable(ps, 1, args);
		}
		
		return ps;
	}

	private PreparedStatement buildStatement(Connection conn, String sql, Object[][] args) 
	throws SQLException {
		sql = expandArrayParams(sql, args);
		PreparedStatement ps = conn.prepareStatement(sql);
		for (Object[] arg : args) {
			setBindVariable(ps, 1, arg);
		}
		
		return ps;
	}

	private int setBindVariable(PreparedStatement statement, int startIndex, Object param) 
	throws SQLException {
		int index = startIndex;
		if (param instanceof Iterable) {
			for (Object o : (Iterable<?>) param) {
				index = setBindVariable(statement, index, o);
			}
			index -= 1;
		} else if (param instanceof Object[]) {
			for (Object o : (Object[]) param) {
				index = setBindVariable(statement, index, o);
			}
			index -= 1;
		} else {
			statement.setObject(index, param);
		}

		return index + 1;
	}

	private String expandArrayParams(String query, Object[] params) {
		if (params.length < 1) {
			return query;
		}

		Pattern p = Pattern.compile("\\?");
		Matcher m = p.matcher(query);
		StringBuffer result = new StringBuffer();
		int i = 0;

		while (m.find()) {
			m.appendReplacement(result, marks(params[i]));
			i += 1;
		}

		m.appendTail(result);
		return result.toString();
	}

	private String marks(Object param) {
		if (param instanceof Iterable) {
			Iterable<?> iter = ((Iterable<?>) param);
			
//			
//			Iterable<String> markIter = Iterables.transform(iter,
//					new Function<Object, String>() {
//						@Override
//						public String apply(Object input) {
//							return "?";
//						}
//					});
//
//			return Joiner.on(',').join(markIter);
			
			StringBuffer buf = new StringBuffer();
			for (Iterator<?> iterator = iter.iterator(); iterator.hasNext(); ) {
				iterator.next();
				
				if (buf.length() > 0) {
					buf.append(",");
				}
				
				buf.append("?");
			}
			
			return buf.toString();
		}

		return "?";
	}

	private int getRowCount(ResultSet rs) throws SQLException {
		try {
			rs.last();
			return rs.getRow();
		} finally {
			rs.first();
		}
	}

	
	private void close(AutoCloseable...autos) {
	  	for (AutoCloseable auto : autos) {
	  		if (auto != null) {
			  try {
			  		auto.close();
			  } catch (Exception e) {
			    // do nothing
			  }
	  		}
	  	}
	  }

}