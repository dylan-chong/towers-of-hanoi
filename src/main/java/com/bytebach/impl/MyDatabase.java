package com.bytebach.impl;

import com.bytebach.model.*;

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
		pruneNullReferences();
	}

	public void pruneNullReferences() {
		boolean didPrune;
		do {
			// Make sure we prune references of references of a deleted row
			didPrune = doPrune();
		} while (didPrune);
	}

	/**
	 * @return true iff any pruning was done
	 */
	private boolean doPrune() {
		boolean didPrune = false;

		for (Table table : tables.values()) {

			Iterator<List<Value>> rowsIterator = table.rows().iterator();
			while (rowsIterator.hasNext()) {
				List<Value> row = rowsIterator.next();
				if (!hasNullReference(row)) continue;

				didPrune = true;
				rowsIterator.remove();
			}
		}

		return didPrune;
	}

	private boolean hasNullReference(List<Value> row) {
		return row.stream()
				.filter(val -> val instanceof ReferenceValue)
				.map(val -> (ReferenceValue) val)
				.anyMatch(ref -> !DBUtils.isReferenceLinked(ref, this));
	}
}
