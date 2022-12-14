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

package org.apache.sysds.runtime.frame.data.columns;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;
import org.apache.sysds.common.Types.ValueType;
import org.apache.sysds.runtime.DMLRuntimeException;
import org.apache.sysds.runtime.frame.data.columns.ArrayFactory.FrameArrayType;
import org.apache.sysds.utils.MemoryEstimates;

public class IntegerArray extends Array<Integer> {
	private int[] _data;

	public IntegerArray(int[] data) {
		_data = data;
		_size = _data.length;
	}

	public int[] get() {
		return _data;
	}

	@Override
	public Integer get(int index) {
		return _data[index];
	}

	@Override
	public void set(int index, Integer value) {
		_data[index] = (value != null) ? value : 0;
	}

	@Override
	public void set(int index, double value) {
		_data[index] = (int)value;
	}

	@Override
	public void set(int rl, int ru, Array<Integer> value) {
		set(rl, ru, value, 0);
	}

	@Override
	public void setFromOtherType(int rl, int ru, Array<?> value) {
		throw new NotImplementedException();
	}

	@Override
	public void set(int rl, int ru, Array<Integer> value, int rlSrc) {
		System.arraycopy(((IntegerArray) value)._data, rlSrc, _data, rl, ru - rl + 1);
	}

	@Override
	public void setNz(int rl, int ru, Array<Integer> value) {
		int[] data2 = ((IntegerArray) value)._data;
		for(int i = rl; i < ru + 1; i++)
			if(data2[i] != 0)
				_data[i] = data2[i];
	}

	@Override
	public void append(String value) {
		append((value != null) ? Integer.parseInt(value.trim()) : null);
	}

	@Override
	public void append(Integer value) {
		if(_data.length <= _size)
			_data = Arrays.copyOf(_data, newSize());
		_data[_size++] = (value != null) ? value : 0;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(FrameArrayType.INT32.ordinal());
		for(int i = 0; i < _size; i++)
			out.writeInt(_data[i]);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		_size = _data.length;
		for(int i = 0; i < _size; i++)
			_data[i] = in.readInt();
	}

	@Override
	public Array<Integer> clone() {
		return new IntegerArray(Arrays.copyOf(_data, _size));
	}

	@Override
	public Array<Integer> slice(int rl, int ru) {
		return new IntegerArray(Arrays.copyOfRange(_data, rl, ru));
	}

	@Override
	public Array<Integer> sliceTransform(int rl, int ru, ValueType vt) {
		return slice(rl, ru);
	}

	public void reset(int size) {
		if(_data.length < size)
			_data = new int[size];
		_size = size;
	}

	@Override
	public byte[] getAsByteArray(int nRow) {
		ByteBuffer intBuffer = ByteBuffer.allocate(4 * nRow);
		intBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0; i < nRow; i++)
			intBuffer.putInt(_data[i]);
		return intBuffer.array();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.INT32;
	}

	@Override
	public ValueType analyzeValueType() {
		return ValueType.INT32;
	}

	@Override
	public FrameArrayType getFrameArrayType() {
		return FrameArrayType.INT32;
	}

	@Override
	public long getInMemorySize() {
		long size = 16; // object header + object reference
		size += MemoryEstimates.intArrayCost(_data.length);
		return size;
	}

	@Override
	public long getExactSerializedSize() {
		return 1 + 4 * _data.length;
	}

	@Override
	protected Array<?> changeTypeBoolean() {
		boolean[] ret = new boolean[size()];
		for(int i = 0; i < size(); i++) {
			if(_data[i] < 0 || _data[i] > 1)
				throw new DMLRuntimeException(
					"Unable to change to Boolean from Integer array because of value:" + _data[i]);
			ret[i] = _data[i] == 0 ? false : true;
		}
		return new BooleanArray(ret);
	}

	@Override
	protected Array<?> changeTypeDouble() {
		double[] ret = new double[size()];
		for(int i = 0; i < size(); i++)
			ret[i] = (double) _data[i];
		return new DoubleArray(ret);
	}

	@Override
	protected Array<?> changeTypeFloat() {
		float[] ret = new float[size()];
		for(int i = 0; i < size(); i++)
			ret[i] = (float) _data[i];
		return new FloatArray(ret);
	}

	@Override
	protected Array<?> changeTypeInteger() {
		return clone();
	}

	@Override
	protected Array<?> changeTypeLong() {
		long[] ret = new long[size()];
		for(int i = 0; i < size(); i++)
			ret[i] = _data[i];
		return new LongArray(ret);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(_data.length * 5 + 2);
		sb.append(super.toString() + ":[");
		for(int i = 0; i < _size - 1; i++)
			sb.append(_data[i] + ",");
		sb.append(_data[_size - 1]);
		sb.append("]");
		return sb.toString();
	}
}
