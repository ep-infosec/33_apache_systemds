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
import java.io.Serializable;

import org.apache.hadoop.io.Writable;
import org.apache.sysds.runtime.io.IOUtilFunctions;
import org.apache.sysds.utils.MemoryEstimates;

public class ColumnMetadata implements Serializable, Writable {
	private static final long serialVersionUID = -90094082422100311L;

	private static final long DEFAULT_DISTINCT = -1;

	private long _ndistinct = DEFAULT_DISTINCT;
	private String _mvValue = null;

	/**
	 * Default constructor
	 */
	public ColumnMetadata() {

	}

	public ColumnMetadata(long ndistinct) {
		_ndistinct = ndistinct;
	}

	public ColumnMetadata(long ndistinct, String mvVal) {
		_ndistinct = ndistinct <= 0 ? DEFAULT_DISTINCT : ndistinct;
		setMvValue(mvVal);
	}

	public ColumnMetadata(ColumnMetadata that) {
		_ndistinct = that._ndistinct;
		_mvValue = that._mvValue;
	}

	public long getNumDistinct() {
		return _ndistinct;
	}

	public void setNumDistinct(long ndistinct) {
		_ndistinct = ndistinct <= 0 ? DEFAULT_DISTINCT : ndistinct;
	}

	public String getMvValue() {
		return _mvValue;
	}

	public void setMvValue(String mvVal) {
		_mvValue = mvVal == null ? null : mvVal.equals("") ? null : mvVal;
	}

	public boolean isDefault() {
		return _mvValue == null && _ndistinct == DEFAULT_DISTINCT;
	}

	public long getInMemorySize() {
		long size = 16; // object header
		size += 8; // long
		size += MemoryEstimates.stringCost(_mvValue); // string
		return size;
	}

	public long getExactSerializedSize(){
		return 8 + IOUtilFunctions.getUTFSize(getMvValue());
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(_ndistinct);
		out.writeUTF(_mvValue != null ? _mvValue : "");
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		_ndistinct = in.readLong();
		_mvValue = in.readUTF();
		if(_mvValue.equals(""))
			_mvValue = null;
	}

	public static ColumnMetadata read(DataInput in) throws IOException {
		ColumnMetadata ret = new ColumnMetadata();
		ret.readFields(in);
		return ret;
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof ColumnMetadata ? equals((ColumnMetadata) that) : false;
	}

	public boolean equals(ColumnMetadata that) {
		if(that._ndistinct != this._ndistinct )
			return false;
		else if(that._mvValue == null)
			return this._mvValue == null;
		else if(this._mvValue == null)
			return false;
		else
			return that._mvValue.equals(this._mvValue);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		if(_ndistinct != DEFAULT_DISTINCT) {
			sb.append(":");
			sb.append(_ndistinct);
		}
		if(_mvValue != null)
			sb.append("--" + _mvValue);

		return sb.toString();
	}
}
