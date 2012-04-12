package de.codecentric.spa.sql;

import java.util.List;

import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.metadata.EntityScanner;
import de.codecentric.spa.metadata.FieldMetaData;
import de.codecentric.spa.metadata.RelationshipMetaData;
import de.codecentric.spa.metadata.RelationshipMetaDataProvider;
import de.codecentric.spa.metadata.RelationshipMetaData.RelationshipType;

/**
 * SQLGenerator is class that generates basic SQL statements (such as 'select',
 * 'update', 'insert' or 'delete' queries) based on {@link EntityMetaData}
 * derived from {@link EntityScanner} scanning.
 */
public class SQLGenerator {

	/**
	 * Method generates the basic SQL statements (in a form of
	 * {@link SQLStatements}) for a table described with given
	 * {@link EntityMetaData} parameter.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return {@link SQLStatements}
	 */
	public static SQLStatements generateSQL(EntityMetaData metaData) {
		SQLStatements sql = new SQLStatements();

		sql.setCreateTableSQL(generateCreateTableSQL(metaData));
		sql.setDropTableSQL(generateDropTableSQL(metaData));
		sql.setInsertSQL(generateInsertSQL(metaData));
		sql.setUpdateSQL(generateUpdateSQL(metaData));
		sql.setSelectSingleSQL(generateSelectSingleSQL(metaData));
		sql.setSelectAllSQL(generateSelectAllSQL(metaData));
		sql.setDeleteSingleSQL(generateDeleteSingleSQL(metaData));
		sql.setDeleteAllSQL(generateDeleteAllSQL(metaData));

		return sql;
	}

	/**
	 * Method generated SQL statement used to insert values into the database
	 * table.
	 *
	 * Statement is generated based on {@link EntityMetaData} parameter. If meta
	 * data does not contain table name, method will return empty string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'insert' SQL statement
	 */
	private static String generateInsertSQL(EntityMetaData metaData) {
		StringBuilder sb = new StringBuilder();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("INSERT INTO ").append(tableName).append(" (");
			List<FieldMetaData> fldMetaDataList = metaData
					.getPersistentFields();

			StringBuilder paramSb = new StringBuilder();
			if (fldMetaDataList != null && !fldMetaDataList.isEmpty()) {

				for (int i = 0; i < fldMetaDataList.size(); i++) {
					FieldMetaData fldMData = fldMetaDataList.get(i);
					sb.append(fldMData.getColumnName());
					paramSb.append('?');

					if (i < fldMetaDataList.size() - 1) {
						sb.append(", ");
						paramSb.append(", ");
					}
				}
			}

			// Iterate through the relationship meta data in order to append
			// those columns too.
			List<RelationshipMetaData> rMetaDataList = RelationshipMetaDataProvider
					.getInstance().getMetaDataByChild(
							metaData.getDescribingClass());
			if (rMetaDataList != null && !rMetaDataList.isEmpty()) {

				for (RelationshipMetaData rmd : rMetaDataList) {
					// Do not skip ONE_TO_ONE relationship because its value has
					// to be filled also.
					EntityMetaData parentMetaData = EntityMetaDataProvider
							.getInstance().getMetaData(rmd.getParentClass());
					if (parentMetaData != null) {
						sb.append(", ").append(rmd.getForeignKeyColumnName());
						paramSb.append(", ?");
					}
				}

			}

			sb.append(") VALUES (").append(paramSb).append(')');
		}

		return sb.toString();
	}

	/**
	 * Method generated SQL statement used to update values of database table.
	 * Method assumes that {@link EntityMetaData#getIdentifier()} will return
	 * not null value. If it does, null SQL statement is returned.
	 *
	 * Statement is generated based on {@link EntityMetaData} parameter. If meta
	 * data does not contain table name, method will return empty string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'update' SQL statement
	 */
	private static String generateUpdateSQL(EntityMetaData metaData) {
		if (metaData.getIdentifier() == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("UPDATE ").append(tableName).append(" SET ");

			List<FieldMetaData> fldMetaDataList = metaData
					.getPersistentFields();
			if (fldMetaDataList != null && !fldMetaDataList.isEmpty()) {

				for (int i = 0; i < fldMetaDataList.size(); i++) {
					FieldMetaData fldMData = fldMetaDataList.get(i);
					sb.append(fldMData.getColumnName()).append(" = ?");

					if (i < fldMetaDataList.size() - 1) {
						sb.append(", ");
					}
				}
			}
			appendRelationshipColumnsForUpdate(sb,
					metaData.getDescribingClass());
			sb.append(" WHERE ")
              .append(metaData.getIdentifier().getColumnName())
              .append(" = ?");
		}

		return sb.toString();
	}

	/**
	 * Method generated SQL statement used to select single record from database
	 * table. Method assumes that {@link EntityMetaData#getIdentifier()} will
	 * return not null value. If it does null SQL statement is returned.
	 *
	 * Statement is generated based on {@link EntityMetaData} parameter. Record
	 * is identified using table identifier field, returned by
	 * {@link EntityMetaData#getIdentifier()}. If meta data does not contain
	 * table name, method will return empty string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'select single' SQL statement
	 */
	private static String generateSelectSingleSQL(EntityMetaData metaData) {
		if (metaData.getIdentifier() == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("SELECT ");

			FieldMetaData idFld = metaData.getIdentifier();
			if (idFld != null) {
				sb.append(idFld.getColumnName());
			}

			List<FieldMetaData> fldMetaDataList = metaData
					.getPersistentFields();
			if (fldMetaDataList != null && !fldMetaDataList.isEmpty()) {
				if (idFld != null) {
					sb.append(", ");
				}

				for (int i = 0; i < fldMetaDataList.size(); i++) {
					FieldMetaData fldMData = fldMetaDataList.get(i);
					sb.append(fldMData.getColumnName());

					if (i < fldMetaDataList.size() - 1) {
						sb.append(", ");
					}
				}

			}
			appendRelationshipColumns(sb, metaData.getDescribingClass(), false);
			sb.append(" FROM ").append(tableName);
			sb.append(" WHERE ")
              .append(metaData.getIdentifier().getColumnName())
              .append(" = ?");
		}

		return sb.toString();
	}

	/**
	 * Method generated SQL statement used to select all records from database
	 * table.
	 *
	 * Statement is generated based on {@link EntityMetaData} parameter. If meta
	 * data does not contain table name, method will return empty string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'select all' SQL statement
	 */
	private static String generateSelectAllSQL(EntityMetaData metaData) {
		StringBuilder sb = new StringBuilder();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("SELECT ");

			FieldMetaData idFld = metaData.getIdentifier();
			if (idFld != null) {
				sb.append(idFld.getColumnName());
			}

			List<FieldMetaData> fldMetaDataList = metaData
					.getPersistentFields();
			if (fldMetaDataList != null && !fldMetaDataList.isEmpty()) {
				if (idFld != null) {
					sb.append(", ");
				}

				for (int i = 0; i < fldMetaDataList.size(); i++) {
					FieldMetaData fldMData = fldMetaDataList.get(i);
					sb.append(fldMData.getColumnName());

					if (i < fldMetaDataList.size() - 1) {
						sb.append(", ");
					}
				}

			}
			appendRelationshipColumns(sb, metaData.getDescribingClass(), false);
			sb.append(" FROM ").append(tableName);
		}

		return sb.toString();
	}

	/**
	 * Method generated SQL statement used to delete record from database table.
	 * Method assumes that {@link EntityMetaData#getIdentifier()} will return
	 * not null value. If it does null SQL statement is returned.
	 *
	 * Statement is generated based on {@link EntityMetaData} parameter. Record
	 * is identified using table identifier field, returned by
	 * {@link EntityMetaData#getIdentifier()}. If meta data does not contain
	 * table name, method will return empty string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'delete single record' SQL statement
	 */
	private static String generateDeleteSingleSQL(EntityMetaData metaData) {
		if (metaData.getIdentifier() == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("DELETE FROM ")
              .append(tableName)
              .append(" WHERE ")
              .append(metaData.getIdentifier().getColumnName())
              .append(" = ?");
		}

		return sb.toString();
	}

	/**
	 * Method generated SQL statement used to delete all records from database
	 * table.
	 *
	 * Statement is generated based on {@link EntityMetaData} parameter. If meta
	 * data does not contain table name, method will return empty string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'delete all records' SQL statement
	 */
	private static String generateDeleteAllSQL(EntityMetaData metaData) {
		StringBuilder sb = new StringBuilder();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("DELETE FROM ").append(tableName);
		}

		return sb.toString();
	}

	/**
	 * Method generates SQL statement used to create database table described
	 * with given {@link EntityMetaData} parameter.
	 *
	 * If meta data does not contain table name, method will return empty
	 * string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'create table' SQL statement
	 */
	private static String generateCreateTableSQL(EntityMetaData metaData) {
		StringBuilder sb = new StringBuilder();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("CREATE TABLE ").append(tableName).append(" (");

			FieldMetaData idFld = metaData.getIdentifier();
			if (idFld != null) {
				sb.append(idFld.getColumnName())
                  .append(' ')
                  .append(idFld.getColumnType())
                  .append(" PRIMARY KEY");
			}

			List<FieldMetaData> fldMetaDataList = metaData
					.getPersistentFields();
			if (fldMetaDataList != null && !fldMetaDataList.isEmpty()) {
				if (idFld != null) {
					sb.append(", ");
				}
				for (int i = 0; i < fldMetaDataList.size(); i++) {
					FieldMetaData fldMData = fldMetaDataList.get(i);
					sb.append(fldMData.getColumnName())
                      .append(' ')
                      .append(fldMData.getColumnType());
					if (i < fldMetaDataList.size() - 1) {
						sb.append(", ");
					}
				}
			}

			appendRelationshipColumns(sb, metaData.getDescribingClass(), true);

			sb.append(')');
		}

		return sb.toString();
	}

	/**
	 * Method appends to the buffer SQL describing relationship (columns and
	 * their types) that specified class has.
	 *
	 * @param sb
	 *            string buffer
	 * @param cls
	 *            describing class, class which SQL is being generated
	 * @param forStructure
	 *            parameter should be true if types are needed (for create
	 *            statement, for example), otherwise false
	 */
	private static void appendRelationshipColumns(final StringBuilder sb,
			Class<?> cls, boolean forStructure) {
		List<RelationshipMetaData> rMetaDataList = RelationshipMetaDataProvider
				.getInstance().getMetaDataByChild(cls);
		if (rMetaDataList != null && !rMetaDataList.isEmpty()) {

			for (RelationshipMetaData rmd : rMetaDataList) {
				// Skip ONE_TO_ONE relationship since it's persistent fields
				// will be processed as persistent fields of
				// parent of the relationship.
				if (!RelationshipType.ONE_TO_ONE.equals(rmd
						.getRelationshipType())) {
					EntityMetaData parentMetaData = EntityMetaDataProvider
							.getInstance().getMetaData(rmd.getParentClass());
					if (parentMetaData != null) {
						sb.append(", ").append(rmd.getForeignKeyColumnName());
						if (forStructure) {
							sb.append(' ')
                              .append(parentMetaData.getIdentifier()
                                                    .getColumnType());
						}
					}
				}
			}

		}
	}

	/**
	 * Method appends to the buffer SQL describing relationship that specified
	 * class has.
	 *
	 * @param sb
	 *            string buffer
	 * @param cls
	 *            describing class, class which SQL is being generated
	 */
	private static void appendRelationshipColumnsForUpdate(
			final StringBuilder sb, Class<?> cls) {
		List<RelationshipMetaData> rMetaDataList = RelationshipMetaDataProvider
				.getInstance().getMetaDataByChild(cls);
		if (rMetaDataList != null && !rMetaDataList.isEmpty()) {

			for (RelationshipMetaData rmd : rMetaDataList) {
				// Skip ONE_TO_ONE relationship since it's persistent fields
				// will be processed as persistent fields of
				// parent of the relationship.
				if (!RelationshipType.ONE_TO_ONE.equals(rmd
						.getRelationshipType())) {
					EntityMetaData parentMetaData = EntityMetaDataProvider
							.getInstance().getMetaData(rmd.getParentClass());
					if (parentMetaData != null) {
						sb.append(", ")
                          .append(rmd.getForeignKeyColumnName())
                          .append(" = ?");
					}
				}
			}

		}
	}

	/**
	 * Method generates SQL statement used to drop database table described with
	 * given {@link EntityMetaData} parameter.
	 *
	 * If meta data does not contain table name, method will return empty
	 * string.
	 *
	 * @param metaData
	 *            table descriptor
	 * @return 'drop table' SQL statement
	 */
	private static String generateDropTableSQL(EntityMetaData metaData) {
		StringBuilder sb = new StringBuilder();

		String tableName = metaData.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			sb.append("DROP TABLE IF EXISTS ").append(tableName);
		}

		return sb.toString();
	}

	/**
	 * This is data transfer object class that holds SQL statements generated by
	 * {@link SQLGenerator#generateSQL(EntityMetaData)} method.
	 */
	public static class SQLStatements {

		private String createTableSQL;
		private String dropTableSQL;
		private String insertSQL;
		private String updateSQL;
		private String selectSingleSQL;
		private String selectAllSQL;
		private String deleteSingleSQL;
		private String deleteAllSQL;

		/**
		 * Default constructor.
		 */
		private SQLStatements() {
			super();
			createTableSQL = "";
			dropTableSQL = "";
			insertSQL = "";
			updateSQL = "";
			selectSingleSQL = "";
			selectAllSQL = "";
			deleteSingleSQL = "";
			deleteAllSQL = "";
		}

		/**
		 * Returns 'create table' SQL statement
		 *
		 * @return 'create table' SQL statement
		 */
		public String getCreateTableSQL() {
			return createTableSQL;
		}

		/**
		 * Sets the 'create table' SQL statement
		 *
		 * @param createTableSQL
		 *            statement to set
		 */
		public void setCreateTableSQL(String createTableSQL) {
			this.createTableSQL = createTableSQL;
		}

		/**
		 * Returns 'drop table' SQL statement
		 *
		 * @return 'drop table' SQL statement
		 */
		public String getDropTableSQL() {
			return dropTableSQL;
		}

		/**
		 * Sets the 'drop table' SQL statement
		 *
		 * @param dropTableSQL
		 *            statement to set
		 */
		public void setDropTableSQL(String dropTableSQL) {
			this.dropTableSQL = dropTableSQL;
		}

		/**
		 * Returns 'insert' SQL statement
		 *
		 * @return 'insert' SQL statement
		 */
		public String getInsertSQL() {
			return insertSQL;
		}

		/**
		 * Sets the 'insert' SQL statement
		 *
		 * @param insertSQL
		 *            statement to set
		 */
		public void setInsertSQL(String insertSQL) {
			this.insertSQL = insertSQL;
		}

		/**
		 * Returns 'update' SQL statement
		 *
		 * @return 'update' SQL statement
		 */
		public String getUpdateSQL() {
			return updateSQL;
		}

		/**
		 * Sets the 'update' SQL statement
		 *
		 * @param updateSQL
		 *            statement to set
		 */
		public void setUpdateSQL(String updateSQL) {
			this.updateSQL = updateSQL;
		}

		/**
		 * Returns 'select single' SQL statement
		 *
		 * @return 'select single' SQL statement
		 */
		public String getSelectSingleSQL() {
			return selectSingleSQL;
		}

		/**
		 * Sets the 'select single' SQL statement
		 *
		 * @param selectSingleSQL
		 *            statement to set
		 */
		public void setSelectSingleSQL(String selectSingleSQL) {
			this.selectSingleSQL = selectSingleSQL;
		}

		/**
		 * Returns 'select all' SQL statement
		 *
		 * @return 'select all' SQL statement
		 */
		public String getSelectAllSQL() {
			return selectAllSQL;
		}

		/**
		 * Sets the 'select all' SQL statement
		 *
		 * @param selectAllSQL
		 *            statement to set
		 */
		public void setSelectAllSQL(String selectAllSQL) {
			this.selectAllSQL = selectAllSQL;
		}

		/**
		 * Returns 'delete single' SQL statement
		 *
		 * @return 'delete single' SQL statement
		 */
		public String getDeleteSingleSQL() {
			return deleteSingleSQL;
		}

		/**
		 * Sets the 'delete single' SQL statement
		 *
		 * @param deleteSingleSQL
		 *            statement to set
		 */
		public void setDeleteSingleSQL(String deleteSingleSQL) {
			this.deleteSingleSQL = deleteSingleSQL;
		}

		/**
		 * Returns 'delete all' SQL statement
		 *
		 * @return 'delete all' SQL statement
		 */
		public String getDeleteAllSQL() {
			return deleteAllSQL;
		}

		/**
		 * Sets the 'delete all' SQL statement
		 *
		 * @param deleteAllSQL
		 *            statement to set
		 */
		public void setDeleteAllSQL(String deleteAllSQL) {
			this.deleteAllSQL = deleteAllSQL;
		}

	}

}
