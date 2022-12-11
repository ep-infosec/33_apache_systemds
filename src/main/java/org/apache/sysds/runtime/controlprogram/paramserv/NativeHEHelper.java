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

package org.apache.sysds.runtime.controlprogram.paramserv;

import org.apache.commons.lang.SystemUtils;
import org.apache.sysds.utils.NativeHelper;

public class NativeHEHelper {
	public static boolean initialize() {
		String platform_suffix = (SystemUtils.IS_OS_WINDOWS ? "-Windows-AMD64.dll" : "-Linux-x86_64.so");
		String library_name = "libhe" + platform_suffix;
		return NativeHelper.loadLibraryHelperFromResource(library_name);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// SEAL integration
	// ----------------------------------------------------------------------------------------------------------------

	// these are called by SEALClient

	/**
	 * generates a Client object
	 * @param a a constant generated by generateA
	 * @return a pointer to the native client object
	 */
	public static native long initClient(byte[] a);

	/**
	 * generates a partial public key
	 * stores a partial private key corresponding to the partial public key in client
	 * @param client A pointer to a Client, obtained from initClient
	 * @return a serialized partial public key
	 */
	public static native byte[] generatePartialPublicKey(long client);

	/**
	 * sets the public key and stores it in client
	 * @param client A pointer to a Client, obtained from initClient
	 * @param public_key serialized public key obtained from generatePartialPublicKey
	 */
	public static native void setPublicKey(long client, byte[] public_key);

	/**
	 * encrypts data with public key stored in client
	 * setPublicKey() must have been called before calling this
	 * @param client A pointer to a Client, obtained from initClient
	 * @param plaintexts array of double values to be encrypted
	 * @return serialized ciphertext
	 */
	public static native byte[] encrypt(long client, double[] plaintexts);

	/**
	 * partially decrypts ciphertexts with the partial private key. generatePartialPublicKey() must
	 * have been called before calling this function
	 * @param client A pointer to a Client, obtained from initClient
	 * @param ciphertext serialized ciphertext
	 * @return serialized partial decryption
	 */
	public static native byte[] partiallyDecrypt(long client, byte[] ciphertext);

	// ----------------------------------------------------------------------------------------------------------------

	// these are called by SEALServer

	/**
	 * generates the Server object and returns a pointer to it
	 * @return pointer to a native Server object
	 */
	public static native long initServer();

	/**
	 * this generates the a constant. in a future version we want to generate this together with the clients to prevent misuse
	 * @param server A pointer to a Server, obtained from initServer
	 * @return serialized a constant
	 */
	public static native byte[] generateA(long server);

	/**
	 * accumulates the given partial public keys into a public key, stores it in server and returns it
	 * @param server A pointer to a Server, obtained from initServer
	 * @param partial_public_keys array of serialized partial public keys
	 * @return serialized partial public key
	 */
	public static native byte[] aggregatePartialPublicKeys(long server, byte[][] partial_public_keys);

	/**
	 * accumulates the given ciphertexts into a sum ciphertext and returns it
	 * @param server A pointer to a Server, obtained from initServer
	 * @param ciphertexts array of serialized ciphertexts
	 * @return serialized accumulated ciphertext
	 */
	public static native byte[] accumulateCiphertexts(long server, byte[][] ciphertexts);

	/**
	 * averages the partial decryptions and returns the result
	 * @param server A pointer to a Server, obtained from initServer
	 * @param encrypted_sum the result of accumulateCiphertexts()
	 * @param partial_plaintexts the result of partiallyDecrypt of each ciphertext fed into accumulateCiphertexts
	 * @return average of original data
	 */
	public static native double[] average(long server, byte[] encrypted_sum, byte[][] partial_plaintexts);
}
