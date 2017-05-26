package com.bytebach.impl;

import com.bytebach.model.Database;
import com.bytebach.model.Field;
import com.bytebach.model.InvalidOperation;
import com.bytebach.model.Table;

import java.util.*;

/**
 * Simple hashmap implementation of a {@link Database}
 */
public class MyDatabase implements Database {
	private final Map<String, Table> tables = new HashMap<>();

	@Override
	public Collection<? extends Table> tables() {
		return Collections.unmodifiableCollection(tables.values());
	}

	@Override
	public Table table(String name) {
		return tables.get(name);
	}

	@Override
	public void createTable(String name, List<Field> fields) {
		if (tables.containsKey(name))
			throw new InvalidOperation("Table already exists");

		tables.put(name, new MyTable(name, fields, this));
	}

	@Override
	public void deleteTable(String name) {
		if (!tables.containsKey(name))
			throw new InvalidOperation("Table doesn't exist");

		tables.remove(name);
	}
}
