package com.bytebach.impl;

import com.bytebach.model.Database;
import com.bytebach.model.Field;
import com.bytebach.model.InvalidOperation;
import com.bytebach.model.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 25/05/17.
 *
 * One row in a table. Must have unique key fields.
 */
public class MyRow extends ArrayList<Value> {
	private final MyRows parentRows;

	public MyRow(List<Value> row, MyRows parentRows) {
		super(row);
		this.parentRows = parentRows;

		if (!areTypesValid(row))
			throw new InvalidOperation("Invalid types or row size");
	}

	/**
	 * Set is the only modification that can be done to a row. And only works
	 * if the type is correct, and is not the key field
	 */
	@Override
	public Value set(int index, Value element) {
		boolean isKeyField = Arrays.stream(parentRows
				.getParentTable()
				.keyFieldIndexes())
				.anyMatch(keyFieldIndex -> keyFieldIndex == index);
		if (isKeyField)
			throw new InvalidOperation("Can't modify key field");

		Field field = parentRows.getParentTable()
				.fields()
				.get(index);
		Database database = parentRows.getParentTable().getParentDatabase();
		if (!DBUtils.typesMatch(field, element, database))
			throw new InvalidOperation("Invalid type");

		return super.set(index, element);
	}

	@Override
	public boolean add(Value value) {
		throw new InvalidOperation("Illegal operation");
	}

	@Override
	public void add(int index, Value element) {
		throw new InvalidOperation("Illegal operation");
	}

	@Override
	public boolean remove(Object o) {
		throw new InvalidOperation("Illegal operation");
	}

	@Override
	public Value remove(int index) {
		throw new InvalidOperation("Illegal operation");
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

	private boolean areTypesValid(List<Value> row) {
		List<Field> fields = parentRows.getParentTable().fields();
		MyDatabase database = parentRows.getParentTable().getParentDatabase();

		if (row.size() != fields.size())
			return false;

		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			Value value = row.get(i);

			if (!DBUtils.typesMatch(field, value, database)) return false;
		}

		return true;
	}
}
