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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.FinalizableWeakReference;

/**
 * Connection Partition structure
 * @author wwadge
 *
 */
public class ConnectionPartition implements Serializable{
	/** Serialization UID */
	private static final long serialVersionUID = -7864443421028454573L;
	/** Logger class. */
	static Logger logger = LoggerFactory.getLogger(ConnectionPartition.class);
	/**  Connections available to be taken  */
	private ArrayBlockingQueue<ConnectionHandle> freeConnections;
	/** When connections start running out, add these number of new connections. */
	private final int acquireIncrement;
	/** Minimum number of connections to start off with. */
	private final int minConnections;
	/** Maximum number of connections that will ever be created. */
	private final int maxConnections;
	/** Statistics lock. */
	protected ReentrantReadWriteLock statsLock = new ReentrantReadWriteLock();
	/** Number of connections that have been created. */
	private int createdConnections=0;
	/** DB details. */
	private final String url;
	/** DB details. */
	private final String username;
	/** DB details. */
	private final String password;
	/** If set to true, don't bother calling method to attempt to create
	 * more connections because we've hit our limit. 
	 */
	private volatile boolean unableToCreateMoreTransactions=false;
	/** Scratch queue of connections awaiting to be placed back in queue. */
	private ArrayBlockingQueue<ConnectionHandle> connectionsPendingRelease;
	/** Config setting. */
	private boolean disableTracking;
	/** Signal trigger to pool watch thread. Making it a queue means our signal is persistent. */
	private BlockingQueue<Object> poolWatchThreadSignalQueue = new ArrayBlockingQueue<Object>(1);
	

	/** Returns a handle to the poolWatchThreadSignalQueue
	 * @return the poolWatchThreadSignal
	 */
	protected BlockingQueue<Object> getPoolWatchThreadSignalQueue() {
		return this.poolWatchThreadSignalQueue;
	}
	
	/** Updates leased connections statistics
	 * @param increment value to add/subtract
	 */
	protected void updateCreatedConnections(int increment) {

		try{
			this.statsLock.writeLock().lock();
			this.createdConnections+=increment;
		} finally { 
			this.statsLock.writeLock().unlock();
		}
	}

	/**
	 * Adds a free connection.
	 *
	 * @param connectionHandle
	 * @throws SQLException on error
	 */
	protected void addFreeConnection(ConnectionHandle connectionHandle) throws SQLException{
		connectionHandle.setOriginatingPartition(this);
		if (this.freeConnections.offer(connectionHandle)){ // may race
			updateCreatedConnections(1);
			trackConnectionFinalizer(connectionHandle);
		} else {
			connectionHandle.internalClose();
		}
	}

	/** This method is a replacement for finalize() but avoids all the pitfalls of it (see Joshua Bloch et. all).
	 * 
	 * Keeps a handle on the connection. If the application called closed, then it means that the handle gets pushed back to the connection
	 * pool and thus we get a strong reference again. If the application forgot to call close() and subsequently lost the strong reference to it,
	 * the handle becomes eligible to garbage connection and thus the the finalizeReferent method kicks in to safely close off the database
	 * handle. Note that we do not return the connectionHandle back to the pool since that is not possible (for otherwise the GC would not 
	 * have kicked in), but we merely safely release the database internal handle and update our counters instead.
	 * @param connectionHandle handle to watch
	 */ 
	protected void trackConnectionFinalizer(ConnectionHandle connectionHandle) {
		if (!this.disableTracking){

			Connection con = connectionHandle.getInternalConnection();
			final Connection internalDBConnection = con;
			final BoneCP pool = connectionHandle.getPool();
			connectionHandle.getPool().getFinalizableRefs().put(con, new FinalizableWeakReference<ConnectionHandle>(connectionHandle, connectionHandle.getPool().getFinalizableRefQueue()) {
				public void finalizeReferent() {
					try {
						pool.getFinalizableRefs().remove(internalDBConnection);
						if (internalDBConnection != null && !internalDBConnection.isClosed()){ // safety!
							logger.warn("BoneCP detected an unclosed connection and will now attempt to close it for you. " +
							"You should be closing this connection in your application - enable connectionWatch for additional debugging assistance.");
							//	if (!(internalDBConnection instanceof Proxy)){ // this is just a safety against finding EasyMock proxies at this point.
							internalDBConnection.close();
							//}
							updateCreatedConnections(-1);
						}
					} catch (Throwable t) {
						logger.error("Error while closing off internal db connection", t);
					}
				}
			});
		}
	}

	/**
	 * @return the freeConnections
	 */
	protected ArrayBlockingQueue<ConnectionHandle> getFreeConnections() {
		return this.freeConnections;
	}

	/**
	 * @param freeConnections the freeConnections to set
	 */
	protected void setFreeConnections(
			ArrayBlockingQueue<ConnectionHandle> freeConnections) {
		this.freeConnections = freeConnections;
	}


	/**
	 * Partition constructor
	 *
	 * @param pool handle to connection pool
	 */
	public ConnectionPartition(BoneCP pool) {
		BoneCPConfig config = pool.getConfig();
		this.minConnections = config.getMinConnectionsPerPartition();
		this.maxConnections = config.getMaxConnectionsPerPartition();
		this.acquireIncrement = config.getAcquireIncrement();
		this.url = config.getJdbcUrl();
		this.username = config.getUsername();
		this.password = config.getPassword();
		this.connectionsPendingRelease = new ArrayBlockingQueue<ConnectionHandle>(this.maxConnections);
		this.disableTracking = config.isDisableConnectionTracking();

		/** Create a number of helper threads for connection release. */
		int helperThreads = config.getReleaseHelperThreads();
		if (helperThreads > 0) {

			for (int i = 0; i < helperThreads; i++) { 
				// go through pool.getReleaseHelper() rather than releaseHelper directly to aid unit testing (i.e. mocking)
				pool.getReleaseHelper().execute(new ReleaseHelperThread(this.connectionsPendingRelease, pool));
			}
		}
	}

	/**
	 * @return the acquireIncrement
	 */
	protected int getAcquireIncrement() {
		return this.acquireIncrement;
	}

	/**
	 * @return the minConnections
	 */
	protected int getMinConnections() {
		return this.minConnections;
	}


	/**
	 * @return the maxConnections
	 */
	protected int getMaxConnections() {
		return this.maxConnections;
	}

	/**
	 * @return the leasedConnections
	 */
	protected int getCreatedConnections() {
		this.statsLock.readLock().lock();
		int result = this.createdConnections;
		this.statsLock.readLock().unlock();
		return result;
	}

	/**
	 * Returns the number of free slots in this partition. 
	 *
	 * @return number of free slots.
	 */
	protected int getRemainingCapacity(){
		return this.freeConnections.remainingCapacity();
	}

	/**
	 * @return the url
	 */
	protected String getUrl() {
		return this.url;
	}


	/**
	 * @return the username
	 */
	protected String getUsername() {
		return this.username;
	}


	/**
	 * @return the password
	 */
	protected String getPassword() {
		return this.password;
	}


	/**
	 * Returns true if we have created all the connections we can
	 *
	 * @return true if we have created all the connections we can
	 */
	protected boolean isUnableToCreateMoreTransactions() {
		return this.unableToCreateMoreTransactions;
	}


	/**
	 * Sets connection creation possible status 
	 *
	 * @param unableToCreateMoreTransactions t/f
	 */
	protected void setUnableToCreateMoreTransactions(boolean unableToCreateMoreTransactions) {
		this.unableToCreateMoreTransactions = unableToCreateMoreTransactions;
	}


	/**
	 * Gets handle to a release connection handle queue.
	 *
	 * @return release connection handle queue 
	 */
	protected ArrayBlockingQueue<ConnectionHandle> getConnectionsPendingRelease() {
		return this.connectionsPendingRelease;
	}
}