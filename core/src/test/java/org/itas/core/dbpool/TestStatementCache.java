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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.reset;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import com.jolbox.bonecp.CommonTestUtils;


/** Test class for statement cache
 * @author wwadge
 *
 */
public class TestStatementCache {
	/** Mock handle. */
	private static IStatementCache mockCache;
	/** Mock handle. */
	private static Logger mockLogger;
	/** Config clone. */
	private static BoneCPConfig config;

	/** Mock setup.
	 * @throws ClassNotFoundException
	 */
	@BeforeClass
	public static void setup() throws ClassNotFoundException{
		Class.forName("org.hsqldb.jdbcDriver");
		mockCache = createNiceMock(IStatementCache.class);
		mockLogger = createNiceMock(Logger.class);
		config = CommonTestUtils.getConfigClone();

	

	}
	/**
	 * Init.
	 */
	@Before
	public void beforeTest(){
		config.setJdbcUrl(CommonTestUtils.url);
		config.setUsername(CommonTestUtils.username);
		config.setPassword(CommonTestUtils.password);
		config.setIdleConnectionTestPeriod(0);
		config.setIdleMaxAge(0);
		config.setStatementsCacheSize(0);
		config.setReleaseHelperThreads(0);
		config.setDisableConnectionTracking(true);
	}


	
	/** Prepared statement tests.
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testPreparedStatement() throws SQLException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		BoneCP dsb = null ;
		CommonTestUtils.logTestInfo("Tests that prepared statements are obtained from cache when set.");
		config.setMinConnectionsPerPartition(10);
		config.setMaxConnectionsPerPartition(20);
		config.setAcquireIncrement(5);
		config.setPartitionCount(1);
		config.setStatementsCacheSize(1);
		config.setLogStatementsEnabled(true);
		dsb = new BoneCP(config);

		ConnectionHandle con = (ConnectionHandle) dsb.getConnection();
		Field preparedStatement = con.getClass().getDeclaredField("preparedStatementCache");
		preparedStatement.setAccessible(true);
		// switch to our mock
		preparedStatement.set(con, mockCache);
		expect(mockCache.get(isA(String.class))).andReturn(null);
//		mockCache.put(isA(String.class), isA(PreparedStatement.class));

		replay(mockCache);
		Statement statement = con.prepareStatement(CommonTestUtils.TEST_QUERY);
		statement.close();
		verify(mockCache);

		reset(mockCache);
		expect(mockCache.get(isA(String.class))).andReturn(null);
		replay(mockCache);

		con.prepareStatement(CommonTestUtils.TEST_QUERY);
		statement.close();
		verify(mockCache);
		dsb.shutdown();
		statement.close();
		con.close();
		CommonTestUtils.logPass();
	}

	/** Test case for when item is not in cache.
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testStatementCacheNotInCache() throws SQLException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		CommonTestUtils.logTestInfo("Tests statement not in cache.");

		StatementCache cache = new StatementCache(5);
		assertNull(cache.get("nonExistent"));

		CommonTestUtils.logPass();
	}

	/** Test case method for calling different get signatures.
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testStatementCacheDifferentGetSignatures() throws SQLException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		CommonTestUtils.logTestInfo("Tests statement get() signatures.");

		CommonTestUtils.logTestInfo("Tests statement close (put in cache).");
		String sql = CommonTestUtils.TEST_QUERY;
		BoneCP dsb = null ;
		config.setMinConnectionsPerPartition(1);
		config.setMaxConnectionsPerPartition(5);
		config.setAcquireIncrement(1);
		config.setPartitionCount(1);
		config.setStatementsCacheSize(5);
		dsb = new BoneCP(config);
		Connection conn = dsb.getConnection();
		Statement statement = conn.prepareStatement(sql);
		statement.close();

		
		StatementCache cache = new StatementCache(5);
		cache.put("test1", (StatementHandle)statement);
		assertNotNull(cache.get("test1"));
		
		assertNull(cache.get("test1", 1));
		assertNull(cache.get("test1", new int[]{1}));
		assertNull(cache.get("test1", new String[]{"1"}));
		assertNull(cache.get("test1", 1, 1));
		assertNull(cache.get("test1", 1, 1, 1));

		CommonTestUtils.logPass();
	}
	
	/** Test case for cache put.
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testStatementCachePut() throws SQLException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		CommonTestUtils.logTestInfo("Tests statement close (put in cache).");
		String sql = CommonTestUtils.TEST_QUERY;
		BoneCP dsb = null ;
		config.setMinConnectionsPerPartition(1);
		config.setMaxConnectionsPerPartition(5);
		config.setAcquireIncrement(1);
		config.setPartitionCount(1);
		config.setStatementsCacheSize(5);
		dsb = new BoneCP(config);
		Connection conn = dsb.getConnection();
		Statement statement = conn.prepareStatement(sql);
		statement.close();
		Field statementCache = conn.getClass().getDeclaredField("preparedStatementCache");
		statementCache.setAccessible(true);
		IStatementCache cache = (IStatementCache) statementCache.get(conn);
		statement = cache.get(sql);
		assertNotNull(statement);
		// Calling again should not provide the same object
		assertNull(cache.get(sql));

		// now pretend we have 1 connection being asked for the same statement
		// twice
		statement = conn.prepareStatement(sql);
		Statement statement2 = conn.prepareStatement(sql);

		statement.close(); // release it again
		statement2.close(); // release the other one

		statement2.close();
		statement.close();
		conn.close();
		dsb.shutdown();
		
		
		CommonTestUtils.logPass();
	}

	/** Test limits.
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testStatementCacheLimits() throws SQLException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		CommonTestUtils.logTestInfo("Tests statement caching module.");
		String sql = CommonTestUtils.TEST_QUERY;
		BoneCP dsb = null ;
		config.setMinConnectionsPerPartition(2);
		config.setMaxConnectionsPerPartition(5);
		config.setAcquireIncrement(1);
		config.setPartitionCount(1);
		config.setStatementsCacheSize(5);
		dsb = new BoneCP(config);
		Connection conn = dsb.getConnection();
		StatementHandle statement = (StatementHandle)conn.prepareStatement(sql);

		StatementCache cache = new StatementCache(5);
		cache.put("test1", statement);
		cache.put("test2", statement);
		cache.put("test3", statement);
		cache.put("test4", statement);
		cache.put("test5", statement);


		conn.close();

		for (int i=0; i < 5000000; i++){
			cache.put("test"+i, statement);
			if ((i % 10000) == 0){
				System.gc();
			}
			if (cache.size() != i) {
				break;
			}
		}
		// some elements should have been dropped in the cache
		assertFalse(cache.size()==5000000);
	

		dsb.shutdown();
		CommonTestUtils.logPass();
	}

	
	/** Tests statement cache clear.
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStatementCacheClear() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
		ConcurrentMap mockCache = createNiceMock(ConcurrentMap.class);
		List<StatementHandle> mockStatementCollections = createNiceMock(List.class);
		StatementCache testClass = new StatementCache(1);
		Field field = testClass.getClass().getDeclaredField("cache");
		field.setAccessible(true);
		field.set(testClass, mockCache);
		
		Iterator<StatementHandle> mockIterator = createNiceMock(Iterator.class);
		StatementHandle mockStatement = createNiceMock(StatementHandle.class);
		
		expect(mockCache.values()).andReturn(mockStatementCollections).anyTimes();
		expect(mockStatementCollections.iterator()).andReturn(mockIterator).anyTimes();
		expect(mockIterator.hasNext()).andReturn(true).times(2).andReturn(false).once();
		expect(mockIterator.next()).andReturn(mockStatement).anyTimes();
		mockStatement.internalClose();
		expectLastCall().once().andThrow(new SQLException()).once();
		
		mockCache.clear();
		expectLastCall().once();
		replay(mockCache, mockStatementCollections, mockIterator,mockStatement);
		
		testClass.clear();
		verify(mockCache, mockStatement);
		
	}

	/** Proper closure test
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStatementCacheCheckForProperClosure() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
		
		ConcurrentMap mockCache = createNiceMock(ConcurrentMap.class);
		List<StatementHandle> mockStatementCollections = createNiceMock(List.class);
		StatementCache testClass = new StatementCache(1);
		Field field = testClass.getClass().getDeclaredField("cache");
		field.setAccessible(true);
		field.set(testClass, mockCache);
		
		field = testClass.getClass().getDeclaredField("logger");
		field.setAccessible(true);
		field.set(null, mockLogger);
		
		Iterator<StatementHandle> mockIterator = createNiceMock(Iterator.class);
		StatementHandle mockStatement = createNiceMock(StatementHandle.class);
		
		expect(mockCache.values()).andReturn(mockStatementCollections).anyTimes();
		expect(mockStatementCollections.iterator()).andReturn(mockIterator).anyTimes();
		expect(mockIterator.hasNext()).andReturn(true).once().andReturn(false).once();
		expect(mockIterator.next()).andReturn(mockStatement).anyTimes();
		expect(mockStatement.isClosed()).andReturn(false).once();
		mockLogger.error((String)anyObject());
		expectLastCall().once();
		replay(mockCache, mockStatementCollections, mockIterator,mockStatement, mockLogger);
		
		testClass.checkForProperClosure();
		verify(mockCache, mockStatement, mockLogger);
		
	}

}
