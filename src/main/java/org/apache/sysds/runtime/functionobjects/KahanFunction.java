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

package org.apache.sysds.runtime.functionobjects;

import org.apache.sysds.runtime.instructions.cp.KahanObject;


/**
 * Runtime function type to perform the summation of values using
 * the Kahan summation algorithm.
 */
public abstract class KahanFunction extends ValueFunction {

	private static final long serialVersionUID = -8880016655817461398L;

	/**
	 * Add the given term to the existing sum with a function applied
	 * using the Kahan summation algorithm.
	 *
	 * @param kObj A KahanObject containing the current sum and
	 *             correction factor for the Kahan summation
	 *             algorithm.
	 * @param in The current term to be added.
	 */
	public abstract void execute2(KahanObject kObj, double in);

	public abstract void execute3(KahanObject kObj, double in, int count);

	@Override
	public final boolean requiresCorrection() {
		return true;
	}
}
