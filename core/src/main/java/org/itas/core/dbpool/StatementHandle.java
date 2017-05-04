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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.itas.core.dbpool.hooks.ConnectionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wrapper around JDBC Statement.
 *
 * @author wallacew
 * @version $Revision$
 */
public class StatementHandle implements Statement{
	/** For logging purposes - stores parameters to be used for execution. */
	protected Map<Object, Object> logParams = new TreeMap<Object, Object>();
	/** Set to true if the connection has been "closed". */
	protected AtomicBoolean logicallyClosed = new AtomicBoolean(false);
	/** A handle to the actual statement. */
	protected Statement internalStatement;
	/** SQL Statement used for this statement. */
	protected String sql;
	/** Cache pertaining to this statement. */
	protected IStatementCache cache;
	/** Handle to the connection holding this statement. */
	protected ConnectionHandle connectionHandle;
	/** The key to use in the cache. */
	private String cacheKey ;
	/** If enabled, log all statements being executed. */
	protected boolean logStatementsEnabled;
	/** for logging of addBatch. */
	protected StringBuffer batchSQL = new StringBuffer();
	/** If true, this statement is in the cache. */
	public volatile boolean inCache = false;
	/** Stack trace capture of where this statement was opened. */ 
	public String openStackTrace;
	/** Class logger. */
	private static final Logger logger = LoggerFactory.getLogger(StatementHandle.class);
	/** Config setting converted to nanoseconds. */
	protected long queryExecuteTimeLimit;
	/** Config setting. */
	private ConnectionHook connectionHook;
	
	/**
	 * Constructor to statement handle wrapper. 
	 *
	 * @param internalStatement handle to actual statement instance.
	 * @param sql statement used for this handle.
	 * @param cache Cache handle 
	 * @param connectionHandle Handle to the connection
	 * @param cacheKey  
	 * @param logStatementsEnabled set to true to log statements. 
	 */
	public StatementHandle(Statement internalStatement, String sql, IStatementCache cache, 
			ConnectionHandle connectionHandle, String cacheKey, boolean logStatementsEnabled) {
		this.sql = sql;
		this.internalStatement = internalStatement;
		this.cache = cache;
		this.cacheKey = cacheKey; 
		this.connectionHandle = connectionHandle;
		this.logStatementsEnabled = logStatementsEnabled;

		try{
			BoneCPConfig config = connectionHandle.getPool().getConfig();
			this.connectionHook = config.getConnectionHook();
			this.queryExecuteTimeLimit = TimeUnit.NANOSECONDS.convert(config.getQueryExecuteTimeLimit(), TimeUnit.MILLISECONDS);
		} catch (Exception e){ // safety!
			this.connectionHook = null;
			this.queryExecuteTimeLimit = 0; 
		}
		// store it in the cache if caching is enabled(unless it's already there). FIXME: make this a direct call to putIfAbsent.
		if (this.cache != null){
			this.cache.put(this.cacheKey, this);
		}
	}


	/**
	 * Constructor for empty statement (created via connection.createStatement) 
	 *
	 * @param internalStatement wrapper to statement
	 * @param connectionHandle Handle to the connection that this statement is tied to.
	 * @param logStatementsEnabled set to true to enable sql logging.
	 */
	public StatementHandle(Statement internalStatement, ConnectionHandle connectionHandle, boolean logStatementsEnabled) {
		this(internalStatement, null, null, connectionHandle, null, logStatementsEnabled);
	}


	// @Override
	public void close() throws SQLException {
		this.logicallyClosed.set(true);
		if (this.logStatementsEnabled){
			this.logParams.clear();
		}
		if (this.cache == null || !this.inCache){ // no cache = throw it away right now
			internalClose();
		}
	}



	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	// @Override
	public void addBatch(String sql)
	throws SQLException {
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				this.batchSQL.append(sql);
			}

			this.internalStatement.addBatch(sql);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * Checks if the connection is marked as being logically open and throws an exception if not.
	 * @throws SQLException if connection is marked as logically closed.
	 * 
	 *
	 */
	protected void checkClosed() throws SQLException {
		if (this.logicallyClosed.get()) {
			throw new SQLException("Statement is closed");
		}
	}



	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#cancel()
	 */
	// @Override
	public void cancel()
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.cancel();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#clearBatch()
	 */
	// @Override
	public void clearBatch()
	throws SQLException {
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				this.batchSQL = new StringBuffer();
			}
			this.internalStatement.clearBatch();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#clearWarnings()
	 */
	// @Override
	public void clearWarnings()
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.clearWarnings();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	// @Override
	public boolean execute(String sql)
	throws SQLException {
		boolean result = false;
		checkClosed();
		try {
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
			}
			long timer = queryTimerStart();
			result = this.internalStatement.execute(sql);
			queryTimerEnd(sql, timer);

		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result;
	}




	/** Call the onQueryExecuteTimeLimitExceeded hook if necessary
	 * @param sql sql statement that took too long
	 * @param queryStartTime time when query was started.
	 */
	protected void queryTimerEnd(String sql, long queryStartTime) {
		if ((System.nanoTime() - queryStartTime) > this.queryExecuteTimeLimit 
				&&  this.connectionHook != null){
			this.connectionHook.onQueryExecuteTimeLimitExceeded(sql, this.logParams);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	// @Override
	public boolean execute(String sql, int autoGeneratedKeys)
	throws SQLException {
		boolean result = false;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
			}

			long queryStartTime = queryTimerStart();
			result = this.internalStatement.execute(sql, autoGeneratedKeys);
			queryTimerEnd(sql, queryStartTime);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result;

	}


	/** Start off a timer if necessary
	 * @return Start time
	 */
	protected long queryTimerStart() {
		return (this.queryExecuteTimeLimit != 0) ? System.nanoTime() : Long.MAX_VALUE;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	// @Override
	public boolean execute(String sql, int[] columnIndexes)
	throws SQLException {
		boolean result = false;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
			}

			long queryStartTime = queryTimerStart();
			result = this.internalStatement.execute(sql, columnIndexes);
			queryTimerEnd(sql, queryStartTime);

		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	// @Override
	public boolean execute(String sql, String[] columnNames)
	throws SQLException {
		boolean result = false;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
			}
			long queryStartTime = queryTimerStart();
			result = this.internalStatement.execute(sql, columnNames);
			queryTimerEnd(sql, queryStartTime);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result;

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#executeBatch()
	 */
	// @Override
	public int[] executeBatch()
	throws SQLException {
		int[] result = null;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(this.batchSQL.toString(), this.logParams));
			}
			long queryStartTime = queryTimerStart();
			result = this.internalStatement.executeBatch();
			queryTimerEnd(this.batchSQL.toString(), queryStartTime);

		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; // never reached

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	// @Override
	public ResultSet executeQuery(String sql)
	throws SQLException {
		ResultSet result = null;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
			}
			long queryStartTime = queryTimerStart();
			result = this.internalStatement.executeQuery(sql);
			queryTimerEnd(sql, queryStartTime);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result;

	}


	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	// @Override
	public int executeUpdate(String sql)
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
			}
			long queryStartTime = queryTimerStart();
			result = this.internalStatement.executeUpdate(sql);
			queryTimerEnd(sql, queryStartTime);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}


	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	// @Override
	public int executeUpdate(String sql, int autoGeneratedKeys)
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
			}
			long queryStartTime = queryTimerStart();
			result = this.internalStatement.executeUpdate(sql, autoGeneratedKeys);
			queryTimerEnd(sql, queryStartTime);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}


	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	// @Override
	public int executeUpdate(String sql, int[] columnIndexes)
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams), columnIndexes);
			}
			long queryStartTime = queryTimerStart();
			result = this.internalStatement.executeUpdate(sql, columnIndexes);
			queryTimerEnd(sql, queryStartTime);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}


	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	// @Override
	public int executeUpdate(String sql, String[] columnNames)
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			if (this.logStatementsEnabled){
				logger.debug(PoolUtil.fillLogParams(sql, this.logParams), columnNames);
			}
			long queryStartTime = queryTimerStart();
			result = this.internalStatement.executeUpdate(sql, columnNames);
			queryTimerEnd(sql, queryStartTime);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);
		}

		return result; 
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getConnection()
	 */
	// @Override
	public Connection getConnection()
	throws SQLException {
		checkClosed();
		return this.connectionHandle;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getFetchDirection()
	 */
	// @Override
	public int getFetchDirection()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getFetchDirection();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getFetchSize()
	 */
	// @Override
	public int getFetchSize()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getFetchSize();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	// @Override
	public ResultSet getGeneratedKeys()
	throws SQLException {
		ResultSet result = null;
		checkClosed();
		try{
			result = this.internalStatement.getGeneratedKeys();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	// @Override
	public int getMaxFieldSize()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getMaxFieldSize();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getMaxRows()
	 */
	// @Override
	public int getMaxRows()
	throws SQLException {
		int result=0;
		checkClosed();
		try{
			result = this.internalStatement.getMaxRows();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}


	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getMoreResults()
	 */
	// @Override
	public boolean getMoreResults()
	throws SQLException {
		boolean result = false;
		checkClosed();
		try{
			result = this.internalStatement.getMoreResults();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	// @Override
	public boolean getMoreResults(int current)
	throws SQLException {
		boolean result = false;
		checkClosed();

		try{ 
			result = this.internalStatement.getMoreResults(current);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	// @Override
	public int getQueryTimeout()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getQueryTimeout();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getResultSet()
	 */
	// @Override
	public ResultSet getResultSet()
	throws SQLException {
		ResultSet result = null;
		checkClosed();
		try{
			result = this.internalStatement.getResultSet();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	// @Override
	public int getResultSetConcurrency()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getResultSetConcurrency();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	// @Override
	public int getResultSetHoldability()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getResultSetHoldability();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getResultSetType()
	 */
	// @Override
	public int getResultSetType()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getResultSetType();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getUpdateCount()
	 */
	// @Override
	public int getUpdateCount()
	throws SQLException {
		int result = 0;
		checkClosed();
		try{
			result = this.internalStatement.getUpdateCount();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#getWarnings()
	 */
	// @Override
	public SQLWarning getWarnings()
	throws SQLException {
		SQLWarning result = null;
		checkClosed();
		try{
			result = this.internalStatement.getWarnings();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}

	//@Override
	/** Returns true if statement is logically closed
	 * @return True if handle is closed
	 */
	public boolean isClosed() {
		return this.logicallyClosed.get();
	}

	// #ifdef JDK6
	@Override
	public void setPoolable(boolean poolable)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setPoolable(poolable);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface)
	throws SQLException {
		boolean result = false;
		try{
			result = this.internalStatement.isWrapperFor(iface);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result;
	}

	@Override
	public <T> T unwrap(Class<T> iface)
	throws SQLException {
		T result = null;
		try{
			
			result = this.internalStatement.unwrap(iface);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result;

	}

	@Override
	public boolean isPoolable()
	throws SQLException {
		boolean result = false;
		checkClosed();
		try{
			result = this.internalStatement.isPoolable();
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}
		return result; 

	}
	// #endif JDK6
	
	
	public void setCursorName(String name)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setCursorName(name);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	// @Override
	public void setEscapeProcessing(boolean enable)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setEscapeProcessing(enable);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	// @Override
	public void setFetchDirection(int direction)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setFetchDirection(direction);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	// @Override
	public void setFetchSize(int rows)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setFetchSize(rows);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	// @Override
	public void setMaxFieldSize(int max)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setMaxFieldSize(max);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	// @Override
	public void setMaxRows(int max)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setMaxRows(max);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}

	
	/**
	 * {@inheritDoc}
	 *
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	// @Override
	public void setQueryTimeout(int seconds)
	throws SQLException {
		checkClosed();
		try{
			this.internalStatement.setQueryTimeout(seconds);
		} catch (Throwable t) {
			throw this.connectionHandle.markPossiblyBroken(t);

		}

	}



	/**
	 * @throws SQLException 
	 * 
	 *
	 */
	protected void internalClose() throws SQLException {
		this.batchSQL = new StringBuffer();
		if (this.logStatementsEnabled){
			this.logParams.clear();
		}
		this.internalStatement.close();
	}

	/**
	 * Clears out the cache of statements.
	 */
	protected void clearCache(){
		if (this.cache != null){
			this.cache.clear();
		}
	}



	/**
	 * Marks this statement as being "open"
	 *
	 */
	protected void setLogicallyOpen() {
		this.logicallyClosed.set(false);
	}


	@Override
	public String toString(){
		return this.sql;
	}


	/** Returns the stack trace where this statement was first opened.
	 * @return the openStackTrace
	 */
	public String getOpenStackTrace() {
		return this.openStackTrace;
	}


	/** Sets the stack trace where this statement was first opened.
	 * @param openStackTrace the openStackTrace to set
	 */
	public void setOpenStackTrace(String openStackTrace) {
		this.openStackTrace = openStackTrace;
	}


	/** Returns the statement being wrapped around by this wrapper.
	 * @return the internalStatement being used.
	 */
	public Statement getInternalStatement() {
		return this.internalStatement;
	}


	/** Sets the internal statement used by this wrapper. 
	 * @param internalStatement the internalStatement to set
	 */
	public void setInternalStatement(Statement internalStatement) {
		this.internalStatement = internalStatement;
	}


	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
