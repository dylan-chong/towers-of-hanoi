package com.bytebach.impl;

import com.bytebach.model.InvalidOperation;
import com.bytebach.model.Value;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 25/05/17.
 * <p>
 * This class is required for fixing generics problems.
 */
public class MyRows extends AbstractList<List<Value>> {
	private final List<MyRow> decoratedRows = new ArrayList<>();
	private final MyTable parentTable;

	public MyRows(MyTable parentTable) {
		this.parentTable = parentTable;
	}

	public MyTable getParentTable() {
		return parentTable;
	}

	@Override
	public List<Value> get(int index) {
		return decoratedRows.get(index);
	}

	@Override
	public List<Value> set(int index, List<Value> element) {
		if (containsMatchingRow(element, index))
			throw new InvalidOperation("Row already exists");
		ensureCorrectSize(element);

		return decoratedRows.set(index, new MyRow(element, this));
	}

	@Override
	public int size() {
		return decoratedRows.size();
	}

	@Override
	public void add(int index, List<Value> element) {
		if (containsMatchingRow(element))
			throw new InvalidOperation("Row already exists");
		ensureCorrectSize(element);

		decoratedRows.add(index, new MyRow(element, this));
	}

	@Override
	public boolean add(List<Value> value) {
		if (containsMatchingRow(value))
			throw new InvalidOperation("Row already exists");
		ensureCorrectSize(value);

		return decoratedRows.add(new MyRow(value, this));
	}

	private void ensureCorrectSize(List<Value> row) {
		if (row.size() != parentTable.fields().size())
			throw new InvalidOperation("Row already exists");
	}

	@Override
	public List<Value> remove(int index) {
		return decoratedRows.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return decoratedRows.remove(o);
	}

	public boolean containsMatchingRow(List<Value> row) {
		return containsMatchingRow(row, null);
	}

	public boolean containsMatchingRow(List<Value> row, Integer indexToIgnore) {
		int[] keyFieldIndexes = parentTable.keyFieldIndexes();
		List<Value> keyFields = DBUtils.getKeyFields(row, keyFieldIndexes);
		Value[] keyFieldsArray = new Value[keyFields.size()];
		return indexOfMatchingRow(indexToIgnore, keyFields.toArray(keyFieldsArray)) != -1;
	}

	/**
	 * Finds the index of a row that has the same key field values as
	 * searchKeys
	 * @param indexToIgnore null or an int to ignore that index
	 */
	public int indexOfMatchingRow(Integer indexToIgnore, Value... searchKeys) {
		for (int i = 0; i < decoratedRows.size(); i++) {
			if (indexToIgnore != null && i == indexToIgnore) continue;
			if (decoratedRows.get(i).matchesKeyFields(searchKeys)) {
				return i;
			}
		}
		return -1;
	}

	public int indexOfMatchingRow(Value... searchKeys) {
		return indexOfMatchingRow(null, searchKeys);
	}

	/**
	 * Delegated from {@link MyTable}
	 */
	public List<Value> row(Value... keys) {
		int index = indexOfMatchingRow(keys);
		if (index == -1) throw new InvalidOperation("No row found");

		return decoratedRows.get(index);
	}

	/**
	 * Delegated from {@link MyTable}
	 */
	public void delete(Value... keys) {
		int index = indexOfMatchingRow(keys);
		if (index == -1) throw new InvalidOperation("No matching row to delete");

		List<Value> row = decoratedRows.remove(index);
		assert row != null;
	}
}

