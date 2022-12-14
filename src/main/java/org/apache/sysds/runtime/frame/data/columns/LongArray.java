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

public class LongArray extends Array<Long> {
	private long[] _data;

	public LongArray(long[] data) {
		_data = data;
		_size = _data.length;
	}

	public long[] get() {
		return _data;
	}

	@Override
	public Long get(int index) {
		return _data[index];
	}

	@Override
	public void set(int index, Long value) {
		_data[index] = (value != null) ? value : 0L;
	}

	@Override
	public void set(int index, double value) {
		_data[index] = (long)value;
	}

	@Override
	public void set(int rl, int ru, Array<Long> value) {
		set(rl, ru, value, 0);
	}

	@Override
	public void setFromOtherType(int rl, int ru, Array<?> value) {
		throw new NotImplementedException();
	}

	@Override
	public void set(int rl, int ru, Array<Long> value, int rlSrc) {
		System.arraycopy(((LongArray) value)._data, rlSrc, _data, rl, ru - rl + 1);
	}

	@Override
	public void setNz(int rl, int ru, Array<Long> value) {
		long[] data2 = ((LongArray) value)._data;
		for(int i = rl; i < ru + 1; i++)
			if(data2[i] != 0)
				_data[i] = data2[i];
	}

	@Override
	public void append(String value) {
		append((value != null) ? Long.parseLong(value.trim()) : null);
	}

	@Override
	public void append(Long value) {
		if(_data.length <= _size)
			_data = Arrays.copyOf(_data, newSize());
		_data[_size++] = (value != null) ? value : 0L;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(FrameArrayType.INT64.ordinal());
		for(int i = 0; i < _size; i++)
			out.writeLong(_data[i]);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		_size = _data.length;
		for(int i = 0; i < _size; i++)
			_data[i] = in.readLong();
	}

	@Override
	public Array<Long> clone() {
		return new LongArray(Arrays.copyOf(_data, _size));
	}

	@Override
	public Array<Long> slice(int rl, int ru) {
		return new LongArray(Arrays.copyOfRange(_data, rl, ru));
	}

	@Override
	public Array<Long> sliceTransform(int rl, int ru, ValueType vt) {
		return slice(rl, ru);
	}

	@Override
	public void reset(int size) {
		if(_data.length < size)
			_data = new long[size];
		_size = size;
	}

	@Override
	public byte[] getAsByteArray(int nRow) {
		ByteBuffer longBuffer = ByteBuffer.allocate(8 * nRow);
		longBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0; i < nRow; i++)
			longBuffer.putLong(_data[i]);
		return longBuffer.array();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.INT64;
	}

	@Override
	public ValueType analyzeValueType() {
		return ValueType.INT64;
	}

	@Override
	public FrameArrayType getFrameArrayType() {
		return FrameArrayType.INT64;
	}

	@Override
	public long getInMemorySize() {
		long size = 16; // object header + object reference
		size += MemoryEstimates.longArrayCost(_data.length);
		return size;
	}

	@Override
	public long getExactSerializedSize() {
		return 1 + 8 * _data.length;
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
		int[] ret = new int[size()];
		for(int i = 0; i < size(); i++) {
			if(_data[i] != (long) (int) _data[i])
				throw new DMLRuntimeException("Unable to change to integer from long array because of value:" + _data[i]);
			ret[i] = (int) _data[i];
		}
		return new IntegerArray(ret);
	}

	@Override
	protected Array<?> changeTypeLong() {
		return clone();
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
