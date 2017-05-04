/**
 * Copyright 2009 Wallace Wadge
 *
 * This file is part of BoneCP.
 *
 * BoneCP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoneCP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BoneCP.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.itas.core.dbpool.hooks;

import java.util.Map;

import org.itas.core.dbpool.ConnectionHandle;
import org.itas.core.dbpool.PoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A no-op implementation of the ConnectionHook interface.
 * @author wallacew
 *
 */
public abstract class AbstractConnectionHook implements ConnectionHook {
	/** Class logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractConnectionHook.class);

	@Override
	public void onAcquire(ConnectionHandle connection) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.jolbox.bonecp.hooks.ConnectionHook#onCheckIn(com.jolbox.bonecp.ConnectionHandle)
	 */
	// @Override
	public void onCheckIn(ConnectionHandle connection) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.jolbox.bonecp.hooks.ConnectionHook#onCheckOut(com.jolbox.bonecp.ConnectionHandle)
	 */
	// @Override
	public void onCheckOut(ConnectionHandle connection) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.jolbox.bonecp.hooks.ConnectionHook#onDestroy(com.jolbox.bonecp.ConnectionHandle)
	 */
	// @Override
	public void onDestroy(ConnectionHandle connection) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.jolbox.bonecp.hooks.ConnectionHook#onAcquireFail(Exception)
	 */ 
	// @Override
	public boolean onAcquireFail(Throwable t, AcquireFailConfig acquireConfig) {
		boolean tryAgain = false;
		String log = acquireConfig.getLogMessage();
		logger.error(log+" Sleeping for "+acquireConfig.getAcquireRetryDelay()+"ms and trying again. Attempts left: "+acquireConfig.getAcquireRetryAttempts()+". Exception: "+t.getCause());

		try {
			Thread.sleep(acquireConfig.getAcquireRetryDelay());
			if (acquireConfig.getAcquireRetryAttempts().get() > 0){
				tryAgain = (acquireConfig.getAcquireRetryAttempts().decrementAndGet()) > 0;
			}
		} catch (Exception e) {
			tryAgain=false;
		}
 
		return tryAgain;  
	}


	public void onQueryExecuteTimeLimitExceeded(String sql, Map<Object, Object> logParams){
		StringBuilder sb = new StringBuilder("Query execute time limit exceeded. Query: ");
		sb.append(PoolUtil.fillLogParams(sql, logParams));
		logger.warn(sb.toString());
	}

	public boolean onConnectionException(ConnectionHandle connection, String state, Throwable t) {
		return true; // keep the default behaviour
	}

}
