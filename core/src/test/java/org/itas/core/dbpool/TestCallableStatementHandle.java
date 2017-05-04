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


import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.reset;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jolbox.bonecp.CommonTestUtils;
/**
 * @author Wallace
 *
 */
public class TestCallableStatementHandle {
	/** Class under test. */
	private static CallableStatementHandle testClass;
	/** Mock class under test. */
	private static CallableStatementHandle mockClass;
	/** Mock handles. */
	private static IStatementCache mockCallableStatementCache;
	/** Mock handles. */
	private static ConnectionHandle mockConnection;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mockClass = createNiceMock(CallableStatementHandle.class);
		mockCallableStatementCache = createNiceMock(IStatementCache.class);
		mockConnection = createNiceMock(ConnectionHandle.class);
		
		expect(mockConnection.isLogStatementsEnabled()).andReturn(true).anyTimes();
		testClass = new CallableStatementHandle(mockClass, "",  mockConnection, "somesql", mockCallableStatementCache);
		testClass.logStatementsEnabled=true;

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		reset(mockClass, mockCallableStatementCache, mockConnection);
	}

	/** Test that each method will result in an equivalent bounce on the inner statement (+ test exceptions)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStandardBounceMethods() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Set<String> skipTests = new HashSet<String>();
		skipTests.add("$VRi"); // this only comes into play when code coverage is started. Eclemma bug?
		CommonTestUtils.testStatementBounceMethod(mockConnection, testClass, skipTests, mockClass);
	}
	
	/**
	 * Coverage.
	 */
	@Test
	public void testGetterSetter(){
		CallableStatement mockCallableStatement = createNiceMock(CallableStatement.class); 
		testClass.setInternalCallableStatement(mockCallableStatement);
		Assert.assertEquals(mockCallableStatement, testClass.getInternalCallableStatement());
	}
}
