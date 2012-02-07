package de.codecentric.spa.metadata;

/**
 * This is data transfer object class that holds the information about class
 * field scanning result done by {@link EntityScanner}.
 */
public class FieldMetaData {

	private String fieldName;
	private String columnName;
	private String columnType;
	private Class<?> declaringClass;

	/**
	 * Default constructor.
	 */
	public FieldMetaData() {
		this.fieldName = "";
		this.columnName = "";
		this.columnType = "";
		this.declaringClass = null;
	}

	/**
	 * Returns the field name determined by {@link EntityScanner}.
	 * 
	 * @return field name
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sets a field name determined by {@link EntityScanner}.
	 * 
	 * @param fieldName
	 *            a name to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Returns a column name determined by {@link EntityScanner}.
	 * 
	 * @return column name
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Sets column name determined by {@link EntityScanner}.
	 * 
	 * @param columnName
	 *            a name to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Returns the column type determined by {@link EntityScanner}.
	 * 
	 * @return column type
	 */
	public String getColumnType() {
		return columnType;
	}

	/**
	 * Sets a column type determined by {@link EntityScanner}.
	 * 
	 * @param columnType
	 *            a type to set
	 */
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	/**
	 * Returns a declaring class of this field.
	 * 
	 * @return declaring class
	 */
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	/**
	 * Sets declaring class of this field.
	 * 
	 * @param declaringClass
	 */
	public void setDeclaringClass(Class<?> declaringClass) {
		this.declaringClass = declaringClass;
	}

}
