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

import org.apache.sysds.runtime.util.UtilFunctions;

public class BitwXor extends ValueFunction
{
	private static final long serialVersionUID = -7231003441114081755L;
	
	private static BitwXor singleObj = null;

	private BitwXor() {
		// nothing to do here
	}

	public static BitwXor getBitwXorFnObject() {
		if ( singleObj == null )
			singleObj = new BitwXor();
		return singleObj;
	}

	@Override
	public  double execute(long in1, long in2) {
		return (int)in1 ^ (int)in2;
	}
	
	@Override
	public  double execute(double in1, double in2) {
		return UtilFunctions.toInt(in1) ^ UtilFunctions.toInt(in2);
	}
}
