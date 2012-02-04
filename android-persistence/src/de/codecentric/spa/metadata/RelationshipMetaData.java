package de.codecentric.spa.metadata;

/**
 * This is data transfer object class that holds the information about the relationship between two entity classes.
 */
public class RelationshipMetaData {

	private Class<?> parentClass;
	private Class<?> childClass;

	private RelationshipType relationshipType;
	
	private RelationshipInfo relationshipInfo;

	
	public RelationshipInfo getRelationshipInfo() {
		return relationshipInfo;
	}

	public void setRelationshipInfo(RelationshipInfo relationshipInfo) {
		this.relationshipInfo = relationshipInfo;
	}

	private String fieldName;

	private String foreignKeyColumnName;

	/**
	 * Default constructor - sets {{@link #relationshipType} to {@link RelationshipType#OTHER}.
	 */
	public RelationshipMetaData() {
		relationshipType = RelationshipType.OTHER;
	}

	/**
	 * Method returns class which database table will be the parent of the relationship.
	 * 
	 * @return relationship parent class
	 */
	public Class<?> getParentClass() {
		return parentClass;
	}

	/**
	 * Method sets parent class, i.e. class which database table will be the parent of the relationship.
	 * 
	 * @param parentClass
	 */
	public void setParentClass(Class<?> parentClass) {
		this.parentClass = parentClass;
	}

	/**
	 * Method returns class which database table will be the child of the relationship.
	 * 
	 * @return relationship child class
	 */
	public Class<?> getChildClass() {
		return childClass;
	}

	/**
	 * Method sets child class, i.e. class which database table will be the child of the relationship.
	 * 
	 * @param parentClass
	 */
	public void setChildClass(Class<?> childClass) {
		this.childClass = childClass;
	}

	/**
	 * Method returns relationship type.
	 * 
	 * @return
	 */
	public RelationshipType getRelationshipType() {
		return relationshipType;
	}

	/**
	 * Method sets relationship type.
	 * 
	 * @param relationshipType
	 */
	public void setRelationshipType(RelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	/**
	 * Method returns the name of the field which describes the relationship.
	 * 
	 * @return relationship field name
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Method sets the name of the field which describes the relationship.
	 * 
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Method returns the name of the column that represents foreign key of described relationship.
	 * 
	 * @return foreign key column name
	 */
	public String getForeignKeyColumnName() {
		return foreignKeyColumnName;
	}

	/**
	 * Method sets the name of the foreign key column.
	 * 
	 * @param foreignKeyColumnName
	 */
	public void setForeignKeyColumnName(String foreignKeyColumnName) {
		this.foreignKeyColumnName = foreignKeyColumnName;
	}

	/**
	 * Enumeration describing possible relationship types.
	 */
	public enum RelationshipType {

		ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY, OTHER;

	}

}
