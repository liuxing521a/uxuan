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

package org.itas.core.dbpool.hooks;

import java.util.Map;

import org.itas.core.dbpool.ConnectionHandle;

/**
 * Interface to the hooking mechanism of a connection lifecycle. Applications
 * will generally want to extend {@link com.jolbox.bonecp.hooks.AbstractConnectionHook} instead to provide a 
 * default implementation. Applications might also want to make use of 
 * connection.setDebugHandle(...) to keep track of additional information on 
 * each connection.
 * 
 *  Since the class is eventually loaded via reflection, the implementation must 
 *  provide a public, no argument constructor.
 *  
 *  Warning: Be careful to make sure that the hook methods are re-entrant and
 *  thread-safe; do not rely on external state without appropriate locking, use appropriate 
 *  synchronization!
 *   
 * @author wallacew
 * 
 */
public interface ConnectionHook {

	/** Called upon getting a new connection from the JDBC driver (and prior to
	 * inserting into the pool). You may call connection.getInternalConnection() to obtain
	 * a handle to the actual (unwrapped) connection obtained from the driver. 
	 * @param connection Handle to the new connection
	 */
	void onAcquire(ConnectionHandle connection); 
	
	/**  Called when the connection is about to be returned to the pool.
	 * @param connection being returned to pool.
	 */
	void onCheckIn(ConnectionHandle connection);

	/**  Called when the connection is extracted from the pool and about to be
	 * given to the application.
	 * @param connection about to given to the app.
	 */
	void onCheckOut(ConnectionHandle connection);
	
	/** Called when the connection is about to be completely removed from the
	 * pool. Careful with this hook; the connection might be marked as being
	 * broken. Use connection.isPossiblyBroken() to determine if the connection
	 * has triggered an exception at some point. 
	 * @param connection
	 */
	void onDestroy(ConnectionHandle connection); 
	
	/** Called on attempting (and failing) to acquire a connection. Note that implementing this means that acquireRetry/delay logic
	 * will be overridden by this code.
	 * @param t Exception that occurred.
	 * @param acquireConfig handle containing retry delay, retry attempts etc.
	 * @return Return true to attempt the connection again.
	 */
	boolean onAcquireFail(Throwable t, AcquireFailConfig acquireConfig);
	
	/** Called when a query execute time limit has been set and an executing query took longer
	 * to than the limit to return control to the application.
	 * @param sql SQL statement that was used.
	 * @param logParams Parameters used in this statement.
	 */
	void onQueryExecuteTimeLimitExceeded(String sql, Map<Object, Object> logParams);
	 
	/** Called whenever an exception on a connection occurs. This exception may be a connection failure, a DB failure or a 
	 * non-fatal logical failure (eg Duplicate key exception).
	 * 
	 * 	<p>SQLSTATE Value	  
	 *	<p>Value	Meaning
	 *	<p>08001	The application requester is unable to establish the connection.
	 *	<p>08002	The connection already exists.
	 *	<p>08003	The connection does not exist.
	 *	<p>08004	The application server rejected establishment of the connection.
	 *	<p>08007	Transaction resolution unknown.
	 *	<p>08502	The CONNECT statement issued by an application process running with a SYNCPOINT of TWOPHASE has failed, because no transaction manager is available.
	 *	<p>08504	An error was encountered while processing the specified path rename configuration file.
	 * <p>SQL Failure codes 08001 & 08007 indicate that the database is broken/died (and thus all remaining connections are killed off). 
	 * <p>Anything else will be taken as the connection (not the db) being broken. 
	 * <p>
	 * 
	 * Note: You may use pool.isConnectionHandleAlive(connection) to verify if the connection is in a usable state again.
	 * Note 2: As in all interceptor hooks, this method may be called concurrently so any implementation must be thread-safe.
	 * 
	 * @param connection The handle that triggered this error
	 * @param state the SQLState error code. 
	 * @param t Exception that caused this failure.
	 * @return Returning true means: when you eventually close off this connection, test to see if the connection is still
	 * alive and discard it if not (this is the normal behaviour). Returning false pretends that the connection is still ok 
	 * when the connection is closed (your application will still receive the original exception that was thrown).
	 */
	boolean onConnectionException(ConnectionHandle connection, String state, Throwable t);
}
