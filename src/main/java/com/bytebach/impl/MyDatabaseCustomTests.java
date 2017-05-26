package com.bytebach.impl;

import com.bytebach.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Dylan on 25/05/17.
 * <p>
 * Follow a non crap test naming convention by Roy Osherove
 * http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html
 * i.e., public void methodOrFunctionalityBeingTested_inputOrState_expectation()
 */
public class MyDatabaseCustomTests {

	@Test
	public void myTableGetRow_oneMatchingRow_findsRow() {
		DataSet dataSet = DataSet.PEOPLE;
		Database myDatabase = dataSet.createDB();
		List<Value> expectedRow = dataSet.mainTableRows().get(0);

		List<Value> foundRow = myDatabase.table(dataSet.mainTableName())
				.row(new StringValue(DataSet.PERSON_NAME));
		assertEquals(expectedRow, foundRow);
	}

	@Test
	public void myTableGetRow_noMatchingRows_findsRow() {
		DataSet dataSet = DataSet.PEOPLE;
		Table table = dataSet.createDB().table(dataSet.mainTableName());

		try {
			List<Value> row = table.row(new StringValue("fake name"));
			fail("Found a row: " + row);
		} catch (InvalidOperation ignored) {
		}
	}

	@Test
	public void myRowAdd_matchingTypes_rowAdds() {
		Value key = new StringValue("new name");
		testRowAdd(
				DataSet.PEOPLE,
				Arrays.asList(
						new IntegerValue(2),
						key,
						new BooleanValue(false)
				),
				new Value[]{key}
		);
	}

	@Test
	public void myRowAdd_wrongNumberOfValues_error() {
		testAddRowFails(
				DataSet.PEOPLE,
				Arrays.asList(
						new IntegerValue(2),
						new StringValue("new name"),
						new BooleanValue(false),
						new BooleanValue(false) // extra
				)
		);
	}

	@Test
	public void myRowAdd_wrongKeyFieldType_error() {
		testAddRowFails(
				DataSet.PEOPLE,
				Arrays.asList(
						new IntegerValue(2),
						new IntegerValue(3), // wrong type
						new BooleanValue(false)
				)
		);
	}

	@Test
	public void myRowAdd_wrongNonKeyFieldType_error() {
		testAddRowFails(
				DataSet.PEOPLE,
				Arrays.asList(
						new IntegerValue(2),
						new StringValue("new name"),
						new IntegerValue(4) // wrong type
				)
		);
	}

	@Test
	public void myRowAdd_lineSpaceInString_error() {
		testAddRowFails(
				DataSet.PEOPLE,
				Arrays.asList(
						new IntegerValue(2),
						new StringValue("new\nname"),
						new IntegerValue(4) // wrong type
				)
		);
	}

	@Test
	public void myRowAdd_lineSpaceInTextArea_ok() {
		StringValue key = new StringValue("new\nname");
		testRowAdd(
				DataSet.PEOPLE_TEXTAREA,
				Arrays.asList(
						new IntegerValue(2),
						key,
						new BooleanValue(true)
				),
				new Value[]{key}
		);
	}

	private void testRowAdd(DataSet dataSet,
							List<Value> rowToAdd,
							Value[] rowToAddKeys) {
		Database myDatabase = dataSet.createDB();
		myDatabase.table(dataSet.mainTableName())
				.rows()
				.add(rowToAdd);
		List<Value> foundRow = myDatabase.table(dataSet.mainTableName())
				.row(rowToAddKeys);
		assertEquals(rowToAdd, foundRow);
	}

	private void testAddRowFails(DataSet dataSet, List<Value> row) {
		Table table = dataSet.createDB()
				.table(dataSet.mainTableName());
		try {
			table.rows().add(row);
			fail();
		} catch (InvalidOperation ignored) {
		}
	}

	private enum DataSet {
		PEOPLE {
			@Override
			public List<Field> mainTableFields() {
				return Arrays.asList(
						new Field("age", Field.Type.INTEGER, false),
						new Field("name", Field.Type.TEXT, true),
						new Field("isAlive", Field.Type.BOOLEAN, false)
				);
			}

			@Override
			public List<List<Value>> mainTableRows() {
				return Collections.singletonList(Arrays.asList(
						new IntegerValue(1),
						new StringValue(PERSON_NAME),
						new BooleanValue(true)
				));
			}

			@Override
			public String mainTableName() {
				return "main-table";
			}
		},
		PEOPLE_TEXTAREA {
			@Override
			public List<Field> mainTableFields() {
				return Arrays.asList(
						new Field("age", Field.Type.INTEGER, false),
						new Field("name", Field.Type.TEXTAREA, true),
						new Field("isAlive", Field.Type.BOOLEAN, false)
				);
			}

			@Override
			public String mainTableName() {
				return PEOPLE.mainTableName();
			}

			@Override
			public List<List<Value>> mainTableRows() {
				return PEOPLE.mainTableRows();
			}
		};

		public static final String PERSON_NAME = "mr bob";

		public Database createDB() {
			MyDatabase myDatabase = new MyDatabase();
			myDatabase.createTable(mainTableName(), mainTableFields());
			myDatabase.table(mainTableName())
					.rows()
					.add(mainTableRows().get(0));
			return myDatabase;
		}

		public abstract List<Field> mainTableFields();

		public abstract String mainTableName();

		/**
		 * Some rows you could add
		 *
		 * @return
		 */
		public abstract List<List<Value>> mainTableRows();
	}
}
