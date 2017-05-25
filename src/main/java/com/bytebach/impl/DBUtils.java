package com.bytebach.impl;

import com.bytebach.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 25/05/17.
 *
 * Random helpers
 */
public class DBUtils {
	public static boolean typesMatch(Field field, Value value) {
		if (value == null)
			throw new InvalidOperation("Null");

		Field.Type type = field.type();

		if (type == Field.Type.INTEGER)
			return value instanceof IntegerValue;
		else if (type == Field.Type.BOOLEAN)
			return value instanceof BooleanValue;
		else if (type == Field.Type.TEXT)
			return value instanceof StringValue &&
					!((StringValue) value).value().contains("\n") &&
					!((StringValue) value).value().contains("\r");
		else if (type == Field.Type.TEXTAREA)
			return value instanceof StringValue;
		else if (type == Field.Type.REFERENCE)
			return value instanceof ReferenceValue;

		throw new InvalidOperation("Invalid type of value: " + value);
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
