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
import org.apache.sysds.runtime.frame.data.columns.ArrayFactory.FrameArrayType;
import org.apache.sysds.utils.MemoryEstimates;

public class BooleanArray extends Array<Boolean> {
	private boolean[] _data;

	public BooleanArray(boolean[] data) {
		_data = data;
		_size = _data.length;
	}

	public boolean[] get() {
		return _data;
	}

	@Override
	public Boolean get(int index) {
		return _data[index];
	}

	@Override
	public void set(int index, Boolean value) {
		_data[index] = (value != null) ? value : false;
	}
	
	@Override
	public void set(int index, double value) {
		_data[index] = value == 0 ? false : true;
	}

	@Override
	public void set(int rl, int ru, Array<Boolean> value) {
		set(rl, ru, value, 0);
	}

	@Override
	public void setFromOtherType(int rl, int ru, Array<?> value){
		throw new NotImplementedException();
	}

	@Override
	public void set(int rl, int ru, Array<Boolean> value, int rlSrc) {
		System.arraycopy(((BooleanArray) value)._data, rlSrc, _data, rl, ru - rl + 1);
	}

	@Override
	public void setNz(int rl, int ru, Array<Boolean> value) {
		boolean[] data2 = ((BooleanArray) value)._data;
		for(int i = rl; i < ru + 1; i++)
			if(data2[i])
				_data[i] = data2[i];
	}

	@Override
	public void append(String value) {
		append(Boolean.parseBoolean(value));
	}

	@Override
	public void append(Boolean value) {
		if(_data.length <= _size)
			_data = Arrays.copyOf(_data, newSize());
		_data[_size++] = (value != null) ? value : false;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(FrameArrayType.BOOLEAN.ordinal());
		for(int i = 0; i < _size; i++)
			out.writeBoolean(_data[i]);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		_size = _data.length;
		for(int i = 0; i < _size; i++)
			_data[i] = in.readBoolean();
	}

	@Override
	public Array<Boolean> clone() {
		return new BooleanArray(Arrays.copyOf(_data, _size));
	}

	@Override
	public Array<Boolean> slice(int rl, int ru) {
		return new BooleanArray(Arrays.copyOfRange(_data, rl, ru));
	}

	@Override
	public Array<Boolean> sliceTransform(int rl, int ru, ValueType vt) {
		return slice(rl, ru);
	}

	@Override
	public void reset(int size) {
		if(_data.length < size)
			_data = new boolean[size];
		_size = size;
	}

	@Override
	public byte[] getAsByteArray(int nRow) {
		// over allocating here.. we could maybe bit pack?
		ByteBuffer booleanBuffer = ByteBuffer.allocate(nRow);
		booleanBuffer.order(ByteOrder.nativeOrder());
		for(int i = 0; i < nRow; i++)
			booleanBuffer.put((byte) (_data[i] ? 1 : 0));
		return booleanBuffer.array();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.BOOLEAN;
	}

	@Override
	public ValueType analyzeValueType() {
		return ValueType.BOOLEAN;
	}

	@Override
	public FrameArrayType getFrameArrayType() {
		return FrameArrayType.BOOLEAN;
	}

	@Override
	public long getInMemorySize() {
		long size = 16; // object header + object reference
		size += MemoryEstimates.booleanArrayCost(_data.length);
		return size;
	}

	@Override
	public long getExactSerializedSize() {
		return 1 + _data.length;
	}

	@Override
	protected Array<?> changeTypeBoolean() {
		return clone();
	}

	@Override
	protected Array<?> changeTypeDouble() {
		double[] ret = new double[size()];
		for(int i = 0; i < size(); i++)
			ret[i] = _data[i] ? 1.0 : 0.0;
		return new DoubleArray(ret);
	}

	@Override
	protected Array<?> changeTypeFloat() {
		float[] ret = new float[size()];
		for(int i = 0; i < size(); i++)
			ret[i] = _data[i] ? 1.0f : 0.0f;
		return new FloatArray(ret);
	}

	@Override
	protected Array<?> changeTypeInteger() {
		int[] ret = new int[size()];
		for(int i = 0; i < size(); i++)
			ret[i] = _data[i] ? 1 : 0;
		return new IntegerArray(ret);
	}

	@Override
	protected Array<?> changeTypeLong() {
		long[] ret = new long[size()];
		for(int i = 0; i < size(); i++)
			ret[i] = _data[i] ? 1L : 0L;
		return new LongArray(ret);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(_data.length * 5 + 2);
		sb.append(super.toString() + ":[");
		for(int i = 0; i < _size - 1; i++)
			sb.append((_data[i] ? 1 : 0) + ",");
		sb.append(_data[_size - 1] ? 1 : 0);
		sb.append("]");
		return sb.toString();
	}
}
