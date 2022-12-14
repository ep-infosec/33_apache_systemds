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

package org.apache.sysds.test.functions.builtin.part2;

import org.junit.Test;
import org.apache.sysds.common.Types.ExecMode;
import org.apache.sysds.common.Types.ExecType;
import org.apache.sysds.runtime.matrix.data.MatrixValue.CellIndex;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.apache.sysds.test.TestUtils;

import java.util.HashMap;

public class BuiltinSTEPLmTest extends AutomatedTestBase 
{
	private final static String TEST_NAME = "steplm";
	private final static String TEST_DIR = "functions/builtin/";
	private static final String TEST_CLASS_DIR = TEST_DIR + BuiltinSTEPLmTest.class.getSimpleName() + "/";

	private final static double eps = 1e-10;
	private final static double spSparse = 0.3;
	private final static double spDense = 0.7;

	@Override
	public void setUp() {
		addTestConfiguration(TEST_NAME, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME, new String[]{"B"}));
	}

	@Test
	public void testLmMatrixDenseCPlm() {
		runSTEPLmTest(false, 10, 3, ExecType.CP);
	}

	@Test
	public void testLmMatrixSparseCPlm() {
		runSTEPLmTest(true, 10, 3, ExecType.CP);
	}

	@Test
	public void testLmMatrixDenseSPlm() {
		runSTEPLmTest(false, 10, 3, ExecType.SPARK);
	}

	@Test
	public void testLmMatrixSparseSPlm() {
		runSTEPLmTest(true, 10, 3, ExecType.SPARK);
	}
	
	@Test
	public void testLmMatrixDenseCPlm2() {
		runSTEPLmTest(false, 100, 3, ExecType.CP);
	}

	@Test
	public void testLmMatrixSparseCPlm2() {
		runSTEPLmTest(true, 100, 3, ExecType.CP);
	}

	private void runSTEPLmTest(boolean sparse, int rows, int cols, ExecType instType) {
		ExecMode platformOld = setExecMode(instType);
		String dml_test_name = TEST_NAME;

		try {
			loadTestConfiguration(getTestConfiguration(TEST_NAME));
			double sparsity = sparse ? spSparse : spDense;

			String HOME = SCRIPT_DIR + TEST_DIR;

			fullDMLScriptName = HOME + dml_test_name + ".dml";
			programArgs = new String[]{"-args", input("A"), input("B"), output("C"), output("S")};
			fullRScriptName = HOME + TEST_NAME + ".R";
			rCmd = "Rscript" + " " + fullRScriptName + " " + inputDir() + " " + expectedDir();

			//generate actual dataset
			double[][] A = getRandomMatrix(rows, cols, 0, 1, sparsity, 7);
			writeInputMatrixWithMTD("A", A, true);
			double[][] B = getRandomMatrix(rows, 1, 0, 10, 1.0, 3);
			writeInputMatrixWithMTD("B", B, true);

			runTest(true, false, null, -1);
			runRScript(true);

			//compare matrices
			//FIXME: currently only scenario w/o any features produce same results
			HashMap<CellIndex, Double> dmlfile = readDMLMatrixFromOutputDir("C");
			HashMap<CellIndex, Double> dmfile1 = readDMLMatrixFromOutputDir("S");
			HashMap<CellIndex, Double> rfile = readRMatrixFromExpectedDir("C");
			HashMap<CellIndex, Double> rfile1 = readRMatrixFromExpectedDir("S");
			TestUtils.compareMatrices(dmlfile, rfile, eps, "Stat-DML", "Stat-R");
			TestUtils.compareMatrices(dmfile1, rfile1, eps, "Stat-DML", "Stat-R");
		}
		finally {
			rtplatform = platformOld;
		}
	}
}
