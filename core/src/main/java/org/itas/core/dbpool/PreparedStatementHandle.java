/*

Copyright 2009 Wallace Wadge

This file is part of BoneCP.

BoneCP is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BoneCP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BoneCP.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.itas.core.dbpool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
// #ifdef JDK6
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLXML;
// #endif JDK6
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around JDBC PreparedStatement.
 * 
 * @author wallacew
 */
public class PreparedStatementHandle extends StatementHandle implements
PreparedStatement {

	/** Handle to the real prepared statement. */
	private PreparedStatement internalPreparedStatement;
	/** Class logger. */
	private static final Logger logger = LoggerFactory.getLogger(PreparedStatementHandle.class);


	/**
	 * PreparedStatement Wrapper constructor.
	 * 
	 * @param internalPreparedStatement
	 * @param sql
	 *            sql statement
	 * @param cache
	 *            cache handle.
	 * @param connectionHandle
	 *            Handle to the connection this is tied to.
	 * @param cacheKey 
	 */
	public PreparedStatementHandle(PreparedStatement internalPreparedStatement,
			String sql, ConnectionHandle connectionHandle, String cacheKey, IStatementCache cache) {
		super(internalPreparedStatement, sql, cache, connectionHandle, cacheKey, connectionHandle.isLogStatementsEnabled());
		this.internalPreparedStatement = internalPreparedStatement;
		this.connectionHandle = connectionHandle;
		this.sql = sql;
		this.cache = cache;
	}



	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	// @Override
	public void addBatch() throws SQLException {
		checkClosed();
		try {
			if (this.logStatementsEnabled){
				this.batchSQL.append(this.sql);
			}
			this.internalPreparedStatement.addBatch();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	// @Override
	public void clearParameters() throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.clearParameters();
			if (this.logStatementsEnabled){
				this.logParams.clear();
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#execute()
	 */
	// @Override
	public boolean execute() throws SQLException {
		checkClosed();
		try {
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(this.sql, this.logParams));
			}
			long queryStartTime = queryTimerStart();
			boolean result = this.internalPreparedStatement.execute();
			queryTimerEnd(this.sql, queryStartTime);

			return result;
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	// @Override
	public ResultSet executeQuery() throws SQLException {
		checkClosed();
		try {
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(this.sql, this.logParams));
			}
			long queryStartTime = queryTimerStart();
			ResultSet result = this.internalPreparedStatement.executeQuery();
			queryTimerEnd(this.sql, queryStartTime);

			return result;
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	// @Override
	public int executeUpdate() throws SQLException {
		checkClosed();
		try {
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(this.sql, this.logParams));
			}
			long queryStartTime = queryTimerStart();
			int result = this.internalPreparedStatement.executeUpdate();
			queryTimerEnd(this.sql, queryStartTime);

			return result;
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	// @Override
	public ResultSetMetaData getMetaData() throws SQLException {
		checkClosed();
		try {
			return this.internalPreparedStatement.getMetaData();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	// @Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		checkClosed();
		try {
			return this.internalPreparedStatement.getParameterMetaData();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	// @Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setArray(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	// #ifdef JDK6
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBinaryStream(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBinaryStream(parameterIndex, x, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBlob(parameterIndex, inputStream);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, inputStream);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setAsciiStream(parameterIndex, x, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setClob(parameterIndex, reader);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, reader);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setRowId(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setSQLXML(parameterIndex, xmlObject);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, xmlObject);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setClob(parameterIndex, reader, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, reader);
			}

		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNCharacterStream(parameterIndex, value);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, value);
			}

		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNCharacterStream(parameterIndex, value, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, value);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNClob(parameterIndex, value);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, value);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNClob(parameterIndex, reader);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, reader);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNClob(parameterIndex, reader, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, reader);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setNString(int parameterIndex, String value)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNString(parameterIndex, value);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, value);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setAsciiStream(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setCharacterStream(parameterIndex, reader, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, reader);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBlob(parameterIndex, inputStream, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, inputStream);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

 	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setCharacterStream(parameterIndex, reader);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, reader);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	 // #endif JDK6

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream,
	 *      int)
	 */
	// @Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setAsciiStream(parameterIndex, x, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	// @Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBigDecimal(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream,
	 *      int)
	 */
	// @Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBinaryStream(parameterIndex, x, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	// @Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBlob(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	// @Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBoolean(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	// @Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setByte(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	// @Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setBytes(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader,
	 *      int)
	 */
	// @Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setCharacterStream(parameterIndex,
					reader, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, reader);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	// @Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setClob(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	// @Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setDate(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date,
	 *      java.util.Calendar)
	 */
	// @Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setDate(parameterIndex, x, cal);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, PoolUtil.safePrint(x, ", cal=", cal));
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	// @Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setDouble(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	// @Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setFloat(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	// @Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setInt(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	// @Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setLong(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	// @Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNull(parameterIndex, sqlType);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, "[SQL NULL of type "+sqlType+"]");
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	// @Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setNull(parameterIndex, sqlType, typeName);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, PoolUtil.safePrint("[SQL NULL of type ", sqlType, ", type = ", typeName, "]"));
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	// @Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setObject(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	// @Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setObject(parameterIndex, x, targetSqlType);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int,
	 *      int)
	 */
	// @Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	// @Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setRef(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	// @Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setShort(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	// @Override
	public void setString(int parameterIndex, String x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setString(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	// @Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setTime(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time,
	 *      java.util.Calendar)
	 */
	// @Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setTime(parameterIndex, x, cal);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, PoolUtil.safePrint(x, ", cal=", cal));
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	// @Override
	public void setTimestamp(int parameterIndex, Timestamp x)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setTimestamp(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp,
	 *      java.util.Calendar)
	 */
	// @Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setTimestamp(parameterIndex, x, cal);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, PoolUtil.safePrint(x, ", cal=", cal));
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	// @Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setURL(parameterIndex, x);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.sql.PreparedStatement#setUnicodeStream(int,
	 *      java.io.InputStream, int)
	 */
	// @Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
	throws SQLException {
		checkClosed();
		try {
			this.internalPreparedStatement.setUnicodeStream(parameterIndex, x, length);
			if (this.logStatementsEnabled){
				this.logParams.put(parameterIndex, x);
			}
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);
		}

	}



	/** Returns the wrapped internal statement.
	 * @return the internalPreparedStatement that this wrapper is using.
	 */
	public PreparedStatement getInternalPreparedStatement() {
		return this.internalPreparedStatement;
	}


	/** Sets the internal statement that this wrapper wraps. 
	 * @param internalPreparedStatement the internalPreparedStatement to set
	 */
	public void setInternalPreparedStatement(PreparedStatement internalPreparedStatement) {
		this.internalPreparedStatement = internalPreparedStatement;
	}

	/** {@inheritDoc}
	 * @see java.lang.Object#finalize()
	 *
	// @Override
	protected void finalize() throws Throwable {
		super.finalize();
		  if (!isClosed()){
			  close();
			  logger.warn("BoneCP detected an unclosed statement and has closed it for you. You should be closing this statement in your application - enable connectionWatch config setting for additional debugging assistance.");
		   }
	}
	 */
}
