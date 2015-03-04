package de.codecentric.spa.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.codecentric.spa.metadata.EntityScanner.StringUtils;

/**
 * This is data transfer object class that holds the information about class
 * scanning result done by {@link EntityScanner}.
 */
public class EntityMetaData {

	private Class<?> describingClass;

	private String tableName;
	private boolean hasStructure;
	private FieldMetaData identifier;
	private List<FieldMetaData> persistentFields;
	private HashMap<String, String> fieldColumnLinks;

	/**
	 * Constructor.
	 * 
	 * @param cls
	 *            class which is described by this instance
	 */
	public EntityMetaData(Class<?> cls) {
		describingClass = cls;
		tableName = StringUtils.uncamelize(cls.getSimpleName());
		hasStructure = true;
		identifier = null;
		persistentFields = new ArrayList<FieldMetaData>(0);
		fieldColumnLinks = new HashMap<String, String>(0);
	}

	/**
	 * Method adds entity field to the list of persistent fields that are
	 * already discovered by {@link EntityScanner}.
	 * 
	 * @param f
	 *            field to add
	 */
	public void addPersistentField(FieldMetaData f) {
		if (persistentFields == null) {
			persistentFields = new ArrayList<FieldMetaData>(0);
		}
		persistentFields.add(f);

		fieldColumnLinks.put(f.getColumnName(), f.getFieldName());
	}

	/**
	 * Method returns a name of the field (of the class described by this
	 * object) that is represented with given column in corresponding database
	 * table. Null value will be returned if there is no corresponding field.
	 * 
	 * Link between field name and column is established by
	 * {@link EntityMetaData#addPersistentField(FieldMetaData)} method.
	 * 
	 * @param columnName
	 *            a name of the column in database table representing class
	 *            described by this object
	 * @return field represented with given column
	 */
	public String getFieldNameForColumn(String columnName) {
		return fieldColumnLinks.get(columnName);
	}

	/**
	 * Method returns a name of the database table column corresponding to the
	 * field with a given name (of the class described by this object). Null
	 * value will be returned if there is no corresponding column.
	 * 
	 * Link between field name and column is established by
	 * {@link EntityMetaData#addPersistentField(FieldMetaData)} method.
	 * 
	 * @param fieldName
	 *            a name of the field in the class described by this object
	 * @return database table column corresponding to the given field
	 */
	public String getColumnNameForField(String fieldName) {
		String columnName = null;

		if (persistentFields != null && persistentFields.size() > 0) {
			for (FieldMetaData fmd : persistentFields) {
				if (fieldName.equals(fmd.getFieldName())) {
					columnName = fmd.getColumnName();
					break;
				}
			}
		}

		return columnName;
	}

	/**
	 * Method returns a class that is described by this object.
	 * 
	 * @return
	 */
	public Class<?> getDescribingClass() {
		return describingClass;
	}

	/**
	 * Method returns the list of persistent fields discovered by
	 * {@link EntityScanner}.
	 * 
	 * @return
	 */
	public List<FieldMetaData> getPersistentFields() {
		return persistentFields;
	}

	/**
	 * Method returns a field meta data for field with a given name. If it
	 * doesn't exists, method returns null.
	 * 
	 * @param fieldName
	 *            field name
	 * @return field meta data or null
	 */
	public FieldMetaData getPersistentField(String fieldName) {
		if (fieldName == null) {
			return null;
		}

		FieldMetaData fld = null;

		if (persistentFields != null && !persistentFields.isEmpty()) {
			for (FieldMetaData f : persistentFields) {
				if (fieldName.equals(f.getFieldName())) {
					fld = f;
					break;
				}
			}
		}

		return fld;
	}

	/**
	 * Method returns the database table name resolved by {@link EntityScanner}.
	 * 
	 * @return table name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Method sets the database table name resolved by {@link EntityScanner}.
	 * 
	 * @param tableName
	 *            a name to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Returns true if class described by this meta data should have separate
	 * structure - separate database table - otherwise returns false.
	 * 
	 * @return
	 */
	public boolean hasStructure() {
		return hasStructure;
	}

	/**
	 * Sets whether class described by this meta data should have separate
	 * structure - separate database table.
	 * 
	 * @param hasStructure
	 */
	public void setHasStructure(boolean hasStructure) {
		this.hasStructure = hasStructure;
	}

	/**
	 * Method returns the field that is discovered by {@link EntityScanner} as
	 * database table primary key.
	 * 
	 * @return identifier field
	 */
	public FieldMetaData getIdentifier() {
		return identifier;
	}

	/**
	 * Method sets the field that is discovered by {@link EntityScanner} as
	 * database table primary key.
	 * 
	 * @param identifier
	 *            a field to set as identifier
	 */
	public void setIdentifier(FieldMetaData identifier) {
		this.identifier = identifier;
	}

}
