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

package org.apache.sysds.test.functions.builtin.part1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.sysds.common.Types;
import org.apache.sysds.common.Types.ExecType;
import org.apache.sysds.hops.OptimizerUtils;
import org.apache.sysds.runtime.matrix.data.MatrixValue;
import org.apache.sysds.runtime.meta.MatrixCharacteristics;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
@net.jcip.annotations.NotThreadSafe
public class BuiltinGLMTest extends AutomatedTestBase
{
	protected final static String TEST_NAME = "glmTest";
	protected final static String TEST_DIR = "functions/builtin/";
	protected String TEST_CLASS_DIR = TEST_DIR + BuiltinGLMTest.class.getSimpleName() + "/";
	double eps = 1e-4;
	// SUPPORTED GLM DISTRIBUTION FAMILIES AND LINKS:
	// -----------------------------------------------
	// INPUT PARAMETERS:	MEANING:			Cano-
	// dfam vpow link lpow  Distribution.link   nical?
	// -----------------------------------------------
	//  1   0.0   1  -1.0   Gaussian.inverse
	//  1   0.0   1   0.0   Gaussian.log
	//  1   0.0   1   1.0   Gaussian.id		  Yes
	//  1   1.0   1   0.0   Poisson.log		  Yes
	//  1   1.0   1   0.5   Poisson.sqrt
	//  1   1.0   1   1.0   Poisson.id
	//  1   2.0   1  -1.0   Gamma.inverse		Yes
	//  1   2.0   1   0.0   Gamma.log
	//  1   2.0   1   1.0   Gamma.id
	//  1   3.0   1  -2.0   InvGaussian.1/mu^2   Yes
	//  1   3.0   1  -1.0   InvGaussian.inverse
	//  1   3.0   1   0.0   InvGaussian.log
	//  1   3.0   1   1.0   InvGaussian.id
	//  1	*	1	*	AnyVariance.AnyLink
	// -----------------------------------------------
	//  2	*	1   0.0   Binomial.log
	//  2	*	2	*	Binomial.logit	   Yes
	//  2	*	3	*	Binomial.probit
	//  2	*	4	*	Binomial.cloglog
	//  2	*	5	*	Binomial.cauchit
	// -----------------------------------------------

	protected int numRecords, numFeatures, distFamilyType, linkType, intercept;
	protected double distParam, linkPower, logFeatureVarianceDisbalance, avgLinearForm, stdevLinearForm, dispersion;

	public BuiltinGLMTest(int numRecords_, int numFeatures_, int distFamilyType_, double distParam_,
			int linkType_, double linkPower_, double logFeatureVarianceDisbalance_,
			double avgLinearForm_, double stdevLinearForm_, double dispersion_)
	{
		this.numRecords = numRecords_;
		this.numFeatures = numFeatures_;
		this.distFamilyType = distFamilyType_;
		this.distParam = distParam_;
		this.linkType = linkType_;
		this.linkPower = linkPower_;
		this.logFeatureVarianceDisbalance = logFeatureVarianceDisbalance_;
		this.avgLinearForm = avgLinearForm_;
		this.stdevLinearForm = stdevLinearForm_;
		this.dispersion = dispersion_;
	}

	private void setIntercept(int intercept_)
	{
		intercept = intercept_/100;
	}

	@Override
	public void setUp()
	{
		TestUtils.clearAssertionInformation();
		addTestConfiguration(TEST_CLASS_DIR, TEST_NAME);
	}

	@Test
	public void glmTestIntercept_0_CP() {
		setIntercept(0);
		runtestGLM();
	}

	@Test
	public void glmTestIntercept_1_CP() {
		setIntercept(1);
		runtestGLM();
	}

	@Test
	public void glmTestIntercept_2_CP() {
		setIntercept(2);
		runtestGLM();
	}


	public void runtestGLM() {
		Types.ExecMode platformOld = setExecMode(ExecType.CP);
		try {
			int rows = numRecords;                // # of rows in the training data
			int cols = numFeatures;                // # of features in the training data
			System.out.println("------------ BEGIN " + TEST_NAME + " TEST WITH {" + rows + ", " + cols 
				+ ", " + distFamilyType + ", " + distParam + ", " + linkType + ", " + linkPower + ", " 
				+ intercept + ", " + logFeatureVarianceDisbalance + ", " + avgLinearForm + ", " + stdevLinearForm 
				+ ", " + dispersion + "} ------------");

			TestUtils.GLMDist glmdist = new TestUtils.GLMDist(distFamilyType, distParam, linkType, linkPower);
			glmdist.set_dispersion(dispersion);

			loadTestConfiguration(getTestConfiguration(TEST_NAME));

			// prepare training data set
			Random r = new Random(314159265);
			double[][] X = TestUtils.generateUnbalancedGLMInputDataX(rows, cols, logFeatureVarianceDisbalance);
			double[] beta = TestUtils.generateUnbalancedGLMInputDataB(X, cols, intercept, avgLinearForm, stdevLinearForm, r);
			double[][] y = TestUtils.generateUnbalancedGLMInputDataY(X, beta, rows, cols, glmdist, intercept, dispersion, r);
			
			int defaultBlockSize = OptimizerUtils.DEFAULT_BLOCKSIZE;

			MatrixCharacteristics mc_X = new MatrixCharacteristics(rows, cols, defaultBlockSize, -1);
			writeInputMatrixWithMTD("X", X, true, mc_X);

			MatrixCharacteristics mc_y = new MatrixCharacteristics(rows, y[0].length, defaultBlockSize, -1);
			writeInputMatrixWithMTD("Y", y, true, mc_y);

			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + TEST_NAME + ".dml";
			List<String> proArgs = new ArrayList<>();
			proArgs.add("-exec");
			proArgs.add(" singlenode");
			proArgs.add("-nvargs");
			proArgs.add("X=" + input("X"));
			proArgs.add("Y=" + input("Y"));
			proArgs.add("dfam=" + String.valueOf(distFamilyType));
			proArgs.add(((distFamilyType == 2 && distParam != 1.0) ? "yneg=" : "vpow=") + String.valueOf(distParam));
			proArgs.add((distFamilyType == 2 && distParam != 1.0) ? "vpow=0.0" : "yneg=0.0");
			proArgs.add("link=" + String.valueOf(linkType));
			proArgs.add("lpow=" + String.valueOf(linkPower));
			proArgs.add("icpt=" + String.valueOf(intercept)); // INTERCEPT - CHANGE THIS AS NEEDED
			proArgs.add("disp=0.0"); // DISPERSION (0.0: ESTIMATE)
			proArgs.add("reg=0.0"); // LAMBDA REGULARIZER
			proArgs.add("tol=0.000000000001"); // TOLERANCE (EPSILON)
			proArgs.add("moi=300");
			proArgs.add("mii=0");
			proArgs.add("B=" + output("betas_SYSTEMDS"));
			programArgs = proArgs.toArray(new String[proArgs.size()]);

			fullRScriptName = HOME + TEST_NAME + ".R";
			rCmd = getRCmd(input("X.mtx"), input("Y.mtx"),
					String.valueOf(distFamilyType),
					String.valueOf(distParam),
					String.valueOf(linkType),
					String.valueOf(linkPower),
					String.valueOf(intercept),
					"0.000000000001",
					expected("betas_R"));

			runTest(true, false, null, -1);

			double max_abs_beta = 0.0;
			HashMap<MatrixValue.CellIndex, Double> wTRUE = new HashMap<>();
			for (int j = 0; j < cols; j++) {
				wTRUE.put(new MatrixValue.CellIndex(j + 1, 1), Double.valueOf(beta[j]));
				max_abs_beta = (max_abs_beta >= Math.abs(beta[j]) ? max_abs_beta : Math.abs(beta[j]));
			}

			HashMap<MatrixValue.CellIndex, Double> wSYSTEMDS_raw = readDMLMatrixFromOutputDir("betas_SYSTEMDS");
			HashMap<MatrixValue.CellIndex, Double> wSYSTEMDS = new HashMap<>();
			for (MatrixValue.CellIndex key : wSYSTEMDS_raw.keySet())
				if (key.column == 1)
					wSYSTEMDS.put(key, wSYSTEMDS_raw.get(key));

			runRScript(true);

			HashMap<MatrixValue.CellIndex, Double> wR = readRMatrixFromExpectedDir("betas_R");

			if ((distParam == 0 && linkType == 1)) { // Gaussian.*
				//NOTE MB: Gaussian.log was the only test failing when we introduced multi-threaded
				//matrix multplications (mmchain). After discussions with Sasha, we decided to change the eps
				//because accuracy is anyway affected by various rewrites like binary to unary (-1*x->-x),
				//transpose-matrixmult, and dot product sum. Disabling these rewrites led to a successful
				//test result. Even without multi-threaded matrix mult this test was failing for different number
				//of rows if these rewrites are enabled. Users can turn off rewrites if high accuracy is required.
				//However, in the future we might also consider to use Kahan plus for aggregations in matrix mult
				//(at least for the final aggregation of partial results from individual threads).

				//NOTE MB: similar issues occurred with other tests when moving to github action tests
				eps *= (linkPower == -1) ? 4 : 2; //Gaussian.inverse vs Gaussian.*;
			}
			TestUtils.compareMatrices(wR, wSYSTEMDS, eps * max_abs_beta, "wR", "wSYSTEMDS");
		}
		finally {
			resetExecMode(platformOld);
		}
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		// SCHEMA:
		// #RECORDS, #FEATURES, DISTRIBUTION_FAMILY, VARIANCE_POWER or BERNOULLI_NO, LINK_TYPE, LINK_POWER,
		// LOG_FEATURE_VARIANCE_DISBALANCE, AVG_LINEAR_FORM, ST_DEV_LINEAR_FORM, DISPERSION
		Object[][] data = new Object[][] {
				// #RECS  #FTRS DFM VPOW  LNK LPOW   LFVD  AVGLT STDLT  DISP
				// Both DML and R work and compute close results:
				{ 10000,   50,  1,  0.0,  1,  0.0,   3.0,  10.0,  2.0,  2.5 },   // Gaussian.log
				//{  10000,  100,  1,  0.0,  1,  1.0,   3.0,   0.0,  2.0,  2.5 },   // Gaussian.id
				//{  20000,  100,  1,  0.0,  1, -1.0,   0.0,   0.2,  0.03, 2.5 },   // Gaussian.inverse
				{  1000,  100,  1,  1.0,  1,  0.0,   3.0,   0.0,  1.0,  2.5 },   // Poisson.log
				//{ 100000,   10,  1,  1.0,  1,  0.0,   3.0,   0.0, 50.0,  2.5 },   // Poisson.log              // Pr[0|x] gets near 1
				//{  20000,  100,  1,  1.0,  1,  0.5,  3.0,  10.0,  2.0,  2.5 },   // Poisson.sqrt
				//{  20000,  100,  1,  1.0,  1,  1.0,  3.0,  50.0, 10.0,  2.5 },   // Poisson.id
				{ 10000,   50,  1,  2.0,  1,  0.0,  3.0,   0.0,  2.0,  2.5 },   // Gamma.log
				//{ 100000,   50,  1,  2.0,  1, -1.0,  1.0,   2.0,  0.4,  1.5 },   // Gamma.inverse
				//{  10000,  100,  1,  3.0,  1, -2.0,  3.0,  50.0,  7.0,  1.7 },   // InvGaussian.1/mu^2
				//{  10000,  100,  1,  3.0,  1, -1.0,  3.0,  10.0,  2.0,  2.5 },   // InvGaussian.inverse
				//{ 100000,   50,  1,  3.0,  1,  0.0,  2.0,  -2.0,  1.0,  1.7 },   // InvGaussian.log
				//{ 100000,   50,  1,  3.0,  1,  1.0,  1.0,   0.2,  0.04, 1.7 },   // InvGaussian.id

				{ 10000,   50,  2, -1.0,  1,  0.0,  3.0,  -5.0,  1.0,  1.0 },   // Bernoulli {-1, 1}.log     // Note: Y is sparse
				//{ 100000,   50,  2, -1.0,  1,  1.0,  1.0,   0.6,  0.1,  1.0 },   // Bernoulli {-1, 1}.id
				//{ 100000,   50,  2, -1.0,  1,  0.5,  0.0,   0.4,  0.05, 1.0 },   // Bernoulli {-1, 1}.sqrt
				{  1000,  100,  2, -1.0,  2,  0.0,  3.0,   0.0,  2.0,  1.0 },   // Bernoulli {-1, 1}.logit
				//{  10000,  100,  2, -1.0,  2,  0.0,  3.0,   0.0, 50.0,  1.0 },   // Bernoulli {-1, 1}.logit   // Pr[y|x] near 0, 1
				{  2000,  100,  2, -1.0,  3,  0.0,  3.0,   0.0,  2.0,  1.0 },   // Bernoulli {-1, 1}.probit
				//{ 100000,   10,  2, -1.0,  3,  0.0,  3.0,   0.0, 50.0,  1.0 },   // Bernoulli {-1, 1}.probit  // Pr[y|x] near 0, 1
				//{  10000,  100,  2, -1.0,  4,  0.0,  3.0,  -2.0,  1.0,  1.0 },   // Bernoulli {-1, 1}.cloglog
				//{  50000,   20,  2, -1.0,  4,  0.0,  3.0,  -2.0, 50.0,  1.0 },   // Bernoulli {-1, 1}.cloglog // Pr[y|x] near 0, 1
				//{  50000,  100,  2, -1.0,  5,  0.0,  3.0,   0.0,  2.0,  1.0 },   // Bernoulli {-1, 1}.cauchit

				{ 10000,   50,  2,  1.0,  1,  0.0,  3.0,  -5.0,  1.0,  2.5 },   // Binomial two-column.log   // Note: Y is sparse
				//{  10000,  100,  2,  1.0,  1,  1.0,  0.0,   0.4,  0.05, 2.5 },   // Binomial two-column.id
				//{ 100000,   50,  2,  1.0,  1,  0.5,  0.0,   0.4,  0.05, 2.5 },   // Binomial two-column.sqrt
				{  1000,  100,  2,  1.0,  2,  0.0,  3.0,   0.0,  2.0,  2.5 },   // Binomial two-column.logit
				{  2000,  100,  2,  1.0,  3,  0.0,  3.0,   0.0,  2.0,  2.5 },   // Binomial two-column.probit
				//{  10000,  100,  2,  1.0,  4,  0.0,  3.0,  -2.0,  1.0,  2.5 },   // Binomial two-column.cloglog
				//{  20000,  100,  2,  1.0,  5,  0.0,  3.0,   0.0,  2.0,  2.5 },   // Binomial two-column.cauchit
		};
		return Arrays.asList(data);
	}
}
