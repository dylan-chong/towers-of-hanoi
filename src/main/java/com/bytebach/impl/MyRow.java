package com.bytebach.impl;

import com.bytebach.model.Value;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 25/05/17.
 */
public class MyRow extends AbstractList<Value> implements List<Value> {
	private final List<Value> decoratedRow;
	private final MyRows parentRows;

	public MyRow(List<Value> row, MyRows parentRows) {
		this.decoratedRow = new ArrayList<>(row);
		this.parentRows = parentRows;
	}

	@Override
	public Value get(int index) {
		return decoratedRow.get(index);
	}

	@Override
	public int size() {
		return decoratedRow.size();
	}

	@Override
	public void add(int index, Value element) {
		decoratedRow.add(index, element);
	}

	@Override
	public boolean add(Value value) {
		return decoratedRow.add(value);
	}

	@Override
	public Value remove(int index) {
		return decoratedRow.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return decoratedRow.remove(o);
	}

	public boolean matchesKeyFields(Value[] searchKeys) {
		int[] keyFieldIndexes = parentRows.getParentTable().keyFieldIndexes();
		if (keyFieldIndexes.length != searchKeys.length) return false;

		for (int i = 0; i < keyFieldIndexes.length; i++) {
			Value field = get(keyFieldIndexes[i]);
			Value searchKey = searchKeys[i];
			if (!field.equals(searchKey)) return false;
		}

		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MyRow)) return false;
		MyRow other = (MyRow) o;
		return decoratedRow.equals(other.decoratedRow);
	}

	public static List<Value> getKeyFields(List<Value> row,
										   int[] keyFieldIndexes) {
		List<Value> keyFields = new ArrayList<>();
		for (int keyFieldIndex : keyFieldIndexes) {
			keyFields.add(row.get(keyFieldIndex));
		}
		return keyFields;
	}
}
