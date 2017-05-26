package com.bytebach.impl;

import com.bytebach.model.Field;
import com.bytebach.model.Table;
import com.bytebach.model.Value;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Basic list implementation of {@link Table}
 */
public class MyTable implements Table {
	private final String name;
	private final List<Field> fields;
	private final MyDatabase database;
	private final MyRows rows;

	/**
	 * @param database The db that contains this table
	 */
	public MyTable(String name, List<Field> fields, MyDatabase database) {
		this.name = name;
		this.fields = new ImmutableList<>(fields);
		this.database = database;
		this.rows = new MyRows(this);
	}

	public MyDatabase getParentDatabase() {
		return database;
	}

	/**
	 * Get the table name
	 *
	 * @return
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Return the list of all fields (i.e. including key fields) in the table.
	 * This list is <b>not modifiable</b>. Any attempts to modify this list
	 * should result in an InvalidOperation exception.
	 */
	@Override
	public List<Field> fields() {
		return fields;
	}

	/**
	 * @return Indexes of all key fields in fields
	 */
	public int[] keyFieldIndexes() {
		return IntStream.range(0, fields.size())
				.filter(i -> fields.get(i).isKey())
				.toArray();
	}

	/**
	 * <p>
	 * Return the list of rows in the table. Each row is simply a list of
	 * values, which must correspond to the types determined in fields
	 * </p>
	 * <p>
	 * <p>
	 * The list returned is <b>modifiable</b>. This means one can add and remove
	 * rows from the table via the list interface. Special care must be taken to
	 * ensure that such operations are checked to ensure they do not violate the
	 * database constraints. However, one is not permitted to add and remove
	 * fields within a row; rather, one may update them.
	 * </p>
	 *
	 * @return
	 */
	@Override
	public List<List<Value>> rows() {
		return rows;
	}

	/**
	 * <p>
	 * Return row matching a given key. The row is simply a list of values,
	 * which must correspond to the types determined in fields. If no matching
	 * row is found, then an <code>InvalidOperation</code> should be thrown.
	 * </p>
	 * <p>
	 * <p>
	 * The list returned is <b>partially modifiable</b>. One cannot add or
	 * remove items from the list; however, one can set items in the list
	 * (provided they are not key fields). Special care must be taken to ensure
	 * that such operations are checked to ensure they do not violate the
	 * database constraints.
	 * </p>
	 *
	 * @param keys
	 * @return
	 */
	@Override
	public List<Value> row(Value... keys) {
		return rows.row(keys);
	}

	/**
	 * Delete a row with the matching keys. If no matching row is found, then an
	 * <code>InvalidOperation</code> should be thrown
	 *
	 * @param keys
	 */
	@Override
	public void delete(Value... keys) {
		rows.delete(keys);
	}

}
