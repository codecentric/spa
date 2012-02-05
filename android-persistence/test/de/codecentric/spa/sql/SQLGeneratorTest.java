package de.codecentric.spa.sql;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.metadata.EntityScanner;
import de.codecentric.spa.metadata.FieldMetaData;
import de.codecentric.spa.metadata.RelationshipMetaDataProvider;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;
import de.codecentric.spa.test.entities.DummyAttribute;
import de.codecentric.spa.test.entities.DummySubEntity;

public class SQLGeneratorTest {

	private EntityMetaData parentMetaData;
	private EntityMetaData childMetaData;

	private static final String CREATE_SQL_PARENT = "CREATE TABLE dummy_sub_entity (id INTEGER PRIMARY KEY, name TEXT, some_int INTEGER, sub_fld_dbl REAL, extension_name TEXT)";
	private static final String DROP_SQL_PARENT = "DROP TABLE IF EXISTS dummy_sub_entity";
	private static final String DELETE_ALL_SQL_PARENT = "DELETE FROM dummy_sub_entity";
	private static final String DELETE_SINGLE_SQL_PARENT = "DELETE FROM dummy_sub_entity WHERE id = ?";
	private static final String INSERT_SQL_PARENT = "INSERT INTO dummy_sub_entity (name, some_int, sub_fld_dbl, extension_name) VALUES (?, ?, ?, ?)";
	private static final String SELECT_ALL_SQL_PARENT = "SELECT id, name, some_int, sub_fld_dbl, extension_name FROM dummy_sub_entity";
	private static final String SELECT_SINGLE_SQL_PARENT = "SELECT id, name, some_int, sub_fld_dbl, extension_name FROM dummy_sub_entity WHERE id = ?";
	private static final String UPDATE_SQL_PARENT = "UPDATE dummy_sub_entity SET name = ?, some_int = ?, sub_fld_dbl = ?, extension_name = ? WHERE id = ?";

	private static final String CREATE_SQL_CHILD = "CREATE TABLE dummy_attribute (id INTEGER PRIMARY KEY, dummy_name TEXT, dummy_value TEXT, attributes_fk INTEGER, dummy_sub_entity_id INTEGER, parent_sub_entity_fk INTEGER, parent_id INTEGER)";
	private static final String DROP_SQL_CHILD = "DROP TABLE IF EXISTS dummy_attribute";
	private static final String DELETE_ALL_SQL_CHILD = "DELETE FROM dummy_attribute";
	private static final String DELETE_SINGLE_SQL_CHILD = "DELETE FROM dummy_attribute WHERE id = ?";
	private static final String INSERT_SQL_CHILD = "INSERT INTO dummy_attribute (dummy_name, dummy_value, attributes_fk, dummy_sub_entity_id, parent_sub_entity_fk, parent_id) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SELECT_ALL_SQL_CHILD = "SELECT id, dummy_name, dummy_value, attributes_fk, dummy_sub_entity_id, parent_sub_entity_fk, parent_id FROM dummy_attribute";
	private static final String SELECT_SINGLE_SQL_CHILD = "SELECT id, dummy_name, dummy_value, attributes_fk, dummy_sub_entity_id, parent_sub_entity_fk, parent_id FROM dummy_attribute WHERE id = ?";
	private static final String UPDATE_SQL_CHILD = "UPDATE dummy_attribute SET dummy_name = ?, dummy_value = ?, attributes_fk = ?, dummy_sub_entity_id = ?, parent_sub_entity_fk = ?, parent_id = ? WHERE id = ?";

	@Before
	public void setUp() {
		RelationshipMetaDataProvider.getInstance().clearMetaData();
		EntityMetaDataProvider entityMetaDataProvider = EntityMetaDataProvider
				.getInstance();
		entityMetaDataProvider.clearMetaData();
		EntityScanner.scanClass(DummySubEntity.class, true);
		parentMetaData = entityMetaDataProvider
				.getMetaData(DummySubEntity.class);
		childMetaData = entityMetaDataProvider
				.getMetaData(DummyAttribute.class);
	}

	@Test
	public void testGenerateSQL() {
		Assert.assertNotNull(parentMetaData);
		SQLStatements sql = SQLGenerator.generateSQL(parentMetaData);
		
		Assert.assertTrue(checkCreateSQLStringContents(CREATE_SQL_PARENT, sql.getCreateTableSQL()));
		Assert.assertEquals(DROP_SQL_PARENT, sql.getDropTableSQL());
		Assert.assertEquals(DELETE_ALL_SQL_PARENT, sql.getDeleteAllSQL());
		Assert.assertEquals(DELETE_SINGLE_SQL_PARENT, sql.getDeleteSingleSQL());
		Assert.assertTrue(checkCreateSQLStringContents(INSERT_SQL_PARENT, sql.getInsertSQL()));
		Assert.assertTrue(checkSelectSQLStringContents(SELECT_ALL_SQL_PARENT, sql.getSelectAllSQL()));
		Assert.assertTrue(checkSelectSQLStringContents(SELECT_SINGLE_SQL_PARENT, sql.getSelectSingleSQL()));
		Assert.assertTrue(checkUpdateSQLStringContents(UPDATE_SQL_PARENT, sql.getUpdateSQL()));

		Assert.assertNotNull(childMetaData);
		SQLStatements childSQL = SQLGenerator.generateSQL(childMetaData);
		
		Assert.assertTrue(checkCreateSQLStringContents(CREATE_SQL_CHILD,
				childSQL.getCreateTableSQL()));
		Assert.assertEquals(DROP_SQL_CHILD, childSQL.getDropTableSQL());
		Assert.assertEquals(DELETE_ALL_SQL_CHILD, childSQL.getDeleteAllSQL());
		Assert.assertEquals(DELETE_SINGLE_SQL_CHILD,
				childSQL.getDeleteSingleSQL());
		Assert.assertTrue(checkCreateSQLStringContents(INSERT_SQL_CHILD,
				childSQL.getInsertSQL()));
		Assert.assertTrue(checkSelectSQLStringContents(SELECT_ALL_SQL_CHILD,
				childSQL.getSelectAllSQL()));
		Assert.assertTrue(checkSelectSQLStringContents(SELECT_SINGLE_SQL_CHILD,
				childSQL.getSelectSingleSQL()));
		Assert.assertTrue(checkUpdateSQLStringContents(UPDATE_SQL_CHILD,
				childSQL.getUpdateSQL()));
	}

	private static boolean checkCreateSQLStringContents(String expected,
			String actual) {
		boolean result = expected.substring(0, expected.indexOf('(')).equals(
				actual.substring(0, actual.indexOf('(')));

		if (result) {
			expected = expected.substring(expected.indexOf('(') + 1,
					expected.indexOf(')'));
			actual = actual.substring(actual.indexOf('(') + 1,
					actual.indexOf(')'));
		}

		return compareContent(expected.split(","), actual);
	}

	private static boolean checkSelectSQLStringContents(String expected,
			String actual) {
		boolean result = expected.substring(expected.indexOf("FROM")).equals(
				actual.substring(actual.indexOf("FROM")));

		if (result) {
			expected = expected.substring("SELECT ".length(),
					expected.indexOf("FROM"));
			actual = actual.substring("SELECT ".length(),
					actual.indexOf("FROM"));
		}

		return compareContent(expected.split(","), actual);
	}

	private static boolean checkUpdateSQLStringContents(String expected,
			String actual) {
		boolean result = expected.substring(expected.indexOf("WHERE")).equals(
				actual.substring(actual.indexOf("WHERE")));

		if (result) {
			expected = expected.substring("UPDATE ".length(),
					expected.indexOf("WHERE"));
			actual = actual.substring("UPDATE ".length(),
					actual.indexOf("WHERE"));
		}

		return compareContent(expected.split(","), actual);
	}

	private static boolean compareContent(String[] expectedTokens, String actual) {
		String[] actualTokens = actual.split(",");
		boolean result = expectedTokens.length == actualTokens.length;

		if (result) {
			for (String expectedToken : expectedTokens) {
				result = actual.contains(expectedToken.trim());
				if (!result) {
					break;
				}
			}
		}

		return result;
	}

}
