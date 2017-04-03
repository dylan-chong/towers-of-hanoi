package swen221.lab4.lang;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Dylan on 4/04/17.
 */
public class DatabaseImpl implements Database {
	private final ColumnType[] columnTypes;
	private final int keyField;
	private final List<Object[]> rows;

	private Map<Object, Object[]> keysToRows;

	public DatabaseImpl(ColumnType[] columnTypes, int keyField, List<Object[]> rows) {
		this.columnTypes = columnTypes;
		this.keyField = keyField;
		this.rows = rows;

		this.keysToRows = rows.stream()
				.collect(Collectors.toMap(
						row -> row[keyField],
						row -> row
				));

	}

	@Override
	public ColumnType[] getSchema() {
		return columnTypes;
	}

	@Override
	public int getKeyField() {
		return keyField;
	}

	@Override
	public int size() {
		return rows.size();
	}

	@Override
	public void addRow(Object... data) throws InvalidRowException, DuplicateKeyException {
		Object key = data[keyField];

		if (keysToRows.containsKey(key)) throw new DuplicateKeyException();
		if (data.length != columnTypes.length) throw new InvalidRowException();

		boolean allTypesMatch = IntStream.range(0, data.length)
				.allMatch(i -> columnTypes[i].getType().isInstance(data[i]));
		if (!allTypesMatch) throw new InvalidRowException();

		keysToRows.put(data[keyField], data);
		rows.add(data);
	}

	@Override
	public Object[] getRow(Object key) throws InvalidKeyException {
		if (!keysToRows.containsKey(key)) throw new InvalidKeyException();
		return keysToRows.get(key);
	}

	@Override
	public Object[] getRow(int index) throws IndexOutOfBoundsException {
		return rows.get(index);
	}
}
