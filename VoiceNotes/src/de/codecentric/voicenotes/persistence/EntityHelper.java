package de.codecentric.voicenotes.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.codecentric.voicenotes.persistence.entity.DataEntity;

/**
 * Abstract database helper class.
 */
public abstract class EntityHelper {

	protected String tableName;

	/**
	 * SQL used to create appropriate database table.
	 */
	protected String createTableSQL;

	/**
	 * SQL used to drop appropriate database table.
	 */
	protected String dropTableSQL;

	protected SQLiteStatement insertStmt;
	protected String insertStmtSQL;

	protected SQLiteStatement updateStmt;
	protected String updateStmtSQL;

	protected String selectStmtSQL;

	protected String selectSingleStmtSQL;

	/**
	 * Supplies the SQL string for "create table" statement.
	 * 
	 * @return SQL statement
	 */
	public String supplyCreateTableSQL() {
		return createTableSQL;
	}

	/**
	 * Supplies the SQL string for "drop table" statement.
	 * 
	 * @return SQL statement
	 */
	public String supplyDropTableSQL() {
		return dropTableSQL;
	}

	/**
	 * Method fills the update statement used to save appropriate entity.
	 * 
	 * @param data
	 *            data to save
	 */
	protected abstract void fillUpdateStatement(DataEntity data);

	/**
	 * Method fills the insert statement used to save appropriate entity.
	 * 
	 * @param data
	 *            data to save
	 */
	protected abstract void fillInsertStatement(DataEntity data);

	/**
	 * Method closes used cursors.
	 */
	public abstract void close();

	/**
	 * Method compiles the SQL statements used in this class.
	 * 
	 * @param db
	 *            database in use
	 */
	public abstract void compileSQLStatements(SQLiteDatabase db);

}
