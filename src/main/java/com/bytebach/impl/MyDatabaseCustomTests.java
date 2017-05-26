package com.bytebach.impl;

import com.bytebach.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Dylan on 25/05/17.
 * <p>
 * Follow a non crap test naming convention by Roy Osherove
 * http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html
 * i.e., public void methodOrFunctionalityBeingTested_inputOrState_expectation()
 *
 * Still, these tests make lots of lines of code in exchange for reducing
 * duplication. Hmm...
 */
public class MyDatabaseCustomTests {

	@Test
	public void myTableGetRow_oneMatchingRow_findsRow() {
		TableDataSet tableDataSet = TableDataSet.PEOPLE;
		Database myDatabase = tableDataSet.createDBWithTable();
		List<Value> expectedRow = tableDataSet.defaultRows().get(0);

		List<Value> foundRow = myDatabase.table(tableDataSet.tableName())
				.row(new StringValue(TableDataSet.PERSON_NAME));
		assertEquals(expectedRow, foundRow);
	}

	@Test
	public void myTableGetRow_noMatchingRows_findsRow() {
		TableDataSet tableDataSet = TableDataSet.PEOPLE;
		Table table = tableDataSet.createDBWithTable().table(tableDataSet.tableName());

		try {
			List<Value> row = table.row(new StringValue("fake name"));
			fail("Found a row: " + row);
		} catch (InvalidOperation ignored) {
		}
	}

	@Test
	public void myTableFields_modify_invalidOperationException() {
		Database database = TableDataSet.PEOPLE.createDBWithTable();
		try {
			database.table(TableDataSet.PEOPLE.tableName())
					.fields()
					.add(null);
			fail();
		} catch (InvalidOperation ignore) {
		}
	}

	@Test
	public void myRowAdd_matchingTypes_rowAdds() {
		Value key = new StringValue("new name");
		testRowAdd(
				TableDataSet.PEOPLE,
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
				TableDataSet.PEOPLE,
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
				TableDataSet.PEOPLE,
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
				TableDataSet.PEOPLE,
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
				TableDataSet.PEOPLE,
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
				TableDataSet.PEOPLE_TEXTAREA,
				Arrays.asList(
						new IntegerValue(2),
						key,
						new BooleanValue(true)
				),
				new Value[]{key}
		);
	}

	@Test
	public void myRowAdd_referenceToExistingEntry_ok() {
		Database database = databaseWithPeopleNonEmptyAndReferenceEmptyTables();

		StringValue referenceTableKey = new StringValue("ref1");
		testRowAdd(
				database,
				TableDataSet.PEOPLE_REFERENCES_EMPTY.tableName(),
				Arrays.asList(
						referenceTableKey,
						new ReferenceValue(
								TableDataSet.PEOPLE.tableName(),
								new StringValue(TableDataSet.PERSON_NAME)
						)
				),
				new Value[]{referenceTableKey}
		);
	}

	@Test
	public void myRowAdd_referenceToNonExistentEntry_error() {
		Database database = databaseWithPeopleNonEmptyAndReferenceEmptyTables();

		StringValue referenceTableKey = new StringValue("ref1");
		testAddRowFails(
				database,
				TableDataSet.PEOPLE_REFERENCES_EMPTY.tableName(),
				Arrays.asList(
						referenceTableKey,
						new ReferenceValue(
								TableDataSet.PEOPLE.tableName(),
								new StringValue("nonexistent name")
						)
				)
		);
	}

	@Test
	public void myRowDelete_rowThatIsReferredTo_referenceIsDeletedToo() {
		testDeleteTable(database ->
				database.table(TableDataSet.PEOPLE.tableName())
						.rows()
						.remove(0)
		);
	}

	@Test
	public void myDatabaseDeleteTable_withRowThatIsReferredTo_referenceIsDeletedToo() {
		testDeleteTable(database ->
				database.deleteTable(TableDataSet.PEOPLE.tableName())
		);
	}

	private void testDeleteTable(Consumer<Database> tableDeleter) {
		Database database = databaseWithNonEmptyPeopleAndReferenceTables();
		tableDeleter.accept(database);
		boolean isEmpty = database
				.table(TableDataSet.PEOPLE_REFERENCES_WITH_ONE_ITEM.tableName())
				.rows()
				.isEmpty();
		assertTrue(isEmpty);
	}

	private void testRowAdd(TableDataSet tableDataSet,
							List<Value> rowToAdd,
							Value[] rowToAddKeys) {
		Database myDatabase = tableDataSet.createDBWithTable();
		testRowAdd(myDatabase, tableDataSet.tableName(), rowToAdd, rowToAddKeys);
	}

	private void testRowAdd(Database myDatabase,
							String tableName,
							List<Value> rowToAdd,
							Value[] rowToAddKeys) {
		myDatabase.table(tableName)
				.rows()
				.add(rowToAdd);
		List<Value> foundRow = myDatabase.table(tableName)
				.row(rowToAddKeys);
		assertEquals(rowToAdd, foundRow);
	}

	private void testAddRowFails(Database database,
								 String tableName,
								 List<Value> row) {
		Table table = database.table(tableName);
		try {
			table.rows().add(row);
			fail();
		} catch (InvalidOperation ignored) {
		}
	}

	private void testAddRowFails(TableDataSet tableDataSet, List<Value> row) {
		testAddRowFails(
				tableDataSet.createDBWithTable(),
				tableDataSet.tableName(),
				row
		);
	}

	private Database databaseWithPeopleNonEmptyAndReferenceEmptyTables() {
		Database database = TableDataSet.PEOPLE_REFERENCES_EMPTY.createDBWithTable();
		TableDataSet.PEOPLE.addThisTableToDatabase(database);
		return database;
	}

	private Database databaseWithNonEmptyPeopleAndReferenceTables() {
		Database database = TableDataSet.PEOPLE.createDBWithTable();
		TableDataSet.PEOPLE_REFERENCES_WITH_ONE_ITEM.addThisTableToDatabase(database);
		return database;
	}

	private enum TableDataSet {
		PEOPLE {
			@Override
			public List<Field> fields() {
				return Arrays.asList(
						new Field("age", Field.Type.INTEGER, false),
						new Field("name", Field.Type.TEXT, true),
						new Field("isAlive", Field.Type.BOOLEAN, false)
				);
			}

			@Override
			public List<List<Value>> defaultRows() {
				return Collections.singletonList(Arrays.asList(
						new IntegerValue(1),
						new StringValue(PERSON_NAME),
						new BooleanValue(true)
				));
			}
		},
		PEOPLE_TEXTAREA {
			@Override
			public List<Field> fields() {
				return Arrays.asList(
						new Field("age", Field.Type.INTEGER, false),
						new Field("name", Field.Type.TEXTAREA, true),
						new Field("isAlive", Field.Type.BOOLEAN, false)
				);
			}

			@Override
			public List<List<Value>> defaultRows() {
				return PEOPLE.defaultRows();
			}
		},
		PEOPLE_REFERENCES_EMPTY {
			@Override
			public List<Field> fields() {
				return Arrays.asList(
						new Field("id", Field.Type.TEXT, true),
						new Field("ref", PEOPLE.tableName(), false)
				);
			}

			@Override
			public List<List<Value>> defaultRows() {
				return Collections.emptyList();
			}
		},
		PEOPLE_REFERENCES_WITH_ONE_ITEM {
			@Override
			public List<Field> fields() {
				return PEOPLE_REFERENCES_EMPTY.fields();
			}

			@Override
			public List<List<Value>> defaultRows() {
				return Collections.singletonList(Arrays.asList(
						new StringValue(PEOPLE_REF_1_NAME),
						new ReferenceValue(
								TableDataSet.PEOPLE.tableName(),
								new StringValue(PERSON_NAME)
						)
				));
			}
		};

		public static final String PERSON_NAME = "mr bob";
		public static final String PEOPLE_REF_1_NAME = "ref1";

		public Database createDBWithTable() {
			MyDatabase myDatabase = new MyDatabase();
			addThisTableToDatabase(myDatabase);
			return myDatabase;
		}

		public void addThisTableToDatabase(Database database) {
			database.createTable(tableName(), fields());
			for (List<Value> row : defaultRows()) {
				database.table(tableName())
						.rows()
						.add(row);
			}
		}

		public String tableName() {
			return name().toLowerCase();
		}

		public abstract List<Field> fields();

		/**
		 * Some rows you could add
		 *
		 * @return
		 */
		public abstract List<List<Value>> defaultRows();
	}
}
