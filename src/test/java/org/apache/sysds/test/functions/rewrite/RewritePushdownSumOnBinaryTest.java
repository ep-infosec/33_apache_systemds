/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.test.functions.rewrite;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.apache.sysds.hops.OptimizerUtils;
import org.apache.sysds.runtime.matrix.data.MatrixValue.CellIndex;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.apache.sysds.test.TestUtils;

public class RewritePushdownSumOnBinaryTest extends AutomatedTestBase 
{
	private static final String TEST_NAME1 = "RewritePushdownSumOnBinary";
	private static final String TEST_DIR = "functions/rewrite/";
	private static final String TEST_CLASS_DIR = TEST_DIR + RewritePushdownSumOnBinaryTest.class.getSimpleName() + "/";
	
	private static final int rows = 1000;
	private static final int cols = 1;
	
	@Override
	public void setUp() {
		TestUtils.clearAssertionInformation();
		addTestConfiguration( TEST_NAME1, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME1, new String[] { "R1", "R2" }) );
	}

	@Test
	public void testRewritePushdownSumOnBinaryNoRewrite()  {
		testRewritePushdownSumOnBinary( TEST_NAME1, false );
	}
	
	@Test
	public void testRewritePushdownSumOnBinary()  {
		testRewritePushdownSumOnBinary( TEST_NAME1, true );
	}
	
	private void testRewritePushdownSumOnBinary( String testname, boolean rewrites )
	{	
		boolean oldFlag = OptimizerUtils.ALLOW_ALGEBRAIC_SIMPLIFICATION;
		
		try {
			TestConfiguration config = getTestConfiguration(testname);
			loadTestConfiguration(config);
			
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + testname + ".dml";
			programArgs = new String[]{ "-args", String.valueOf(rows), 
					String.valueOf(cols), output("R1"), output("R2") };
			OptimizerUtils.ALLOW_ALGEBRAIC_SIMPLIFICATION = rewrites;

			//run performance tests
			runTest(true, false, null, -1);
			
			//compare matrices 
			long expect = Math.round(0.5*rows);
			HashMap<CellIndex, Double> dmlfile1 = readDMLScalarFromOutputDir("R1");
			Assert.assertEquals(expect, dmlfile1.get(new CellIndex(1,1)), expect*0.01);
			HashMap<CellIndex, Double> dmlfile2 = readDMLScalarFromOutputDir("R2");
			Assert.assertEquals(expect, dmlfile2.get(new CellIndex(1,1)), expect*0.01);
		}
		finally {
			OptimizerUtils.ALLOW_ALGEBRAIC_SIMPLIFICATION = oldFlag;
		}
	}
}
