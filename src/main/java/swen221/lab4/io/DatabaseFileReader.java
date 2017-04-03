package swen221.lab4.io;

import swen221.lab4.lang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class DatabaseFileReader {
	private Scanner input;
	
	public DatabaseFileReader(String str) {
		input = new Scanner(str);
	}
		
	public Database read() throws InvalidRowException, DuplicateKeyException {		
		// First, read and parse the schema
		String schemaLine = input.nextLine();
		ColumnType[] schema = parseSchema(schemaLine);
		int keyField = findKeyField(schemaLine);
				
		List<Object[]> rows = new ArrayList<Object[]>();
		
		// Second, read data rows
		while(input.hasNext()) {			
			String dataLine = input.nextLine();					
			Object[] row = parseRowItems(dataLine,schema);
			rows.add(row);		
		}
		
		return new DatabaseImpl(schema, keyField, rows);
	}

	/**
	 * Parse a line of text representing a row in the database.
	 * 
	 * @param dataLine
	 * @param schema
	 * @return
	 */
	private Object[] parseRowItems(String dataLine, ColumnType[] schema) {
		String[] dataItems = dataLine.split(",");
		if(dataItems.length != schema.length) {
			// Incorrect number of columns given
			throw new IllegalArgumentException("incorrect number of items: " + dataLine);
		}
		// Now construct the row items
		Object[] items = new Object[schema.length];
		for(int i=0;i!=items.length;++i) {
			Object item;
			if(schema[i].getType() instanceof RowType.Integer) {
				item = Integer.parseInt(dataItems[i]);
			} else {
				item = dataItems[i];
			}
			items[i] = item;
		}
		//
		return items;
	}
	
	/**
	 * Parse a line of text representing the database schema into an array of
	 * columns. 
	 * 
	 * @param schemaLine
	 * @return
	 */
	private ColumnType[] parseSchema(String schemaLine) {
		String[] columns = schemaLine.split(",");
		ColumnType[] schema = new ColumnType[columns.length];
		//
		for(int i=0;i!=columns.length;++i) {
			// Split out the column string, which looks e.g. like "id:int" or
			// "name:str" or "id:int*", etc.
			String[] items = columns[i].split(":");
			String name = items[0];
			RowType type;
			if(items[1].startsWith("int")) {
				type = new RowType.Integer();
			} else {
				type = new RowType.String();
			}
			schema[i] = new ColumnType(name,type);
		}
		// 
		return schema;
	}
	
	/**
	 * Identify the key field in a line of text representing the database schema
	 * 
	 * @param schemaLine
	 * @return
	 */
	private int findKeyField(String schemaLine) {
		String[] columnHeaders = schemaLine.split(",");
		return IntStream.range(0, columnHeaders.length)
				.filter(i -> columnHeaders[i].contains("*"))
				.findFirst()
				.orElseThrow(AssertionError::new);
	}
}
