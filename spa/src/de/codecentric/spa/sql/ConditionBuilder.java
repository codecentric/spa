package de.codecentric.spa.sql;

import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.FieldMetaData;

/**
 * Builder class used to build SQL conditions (where clauses).
 */
public class ConditionBuilder {

	/**
	 * Enumeration describing supported operators that can be used for creating
	 * SQL conditions.
	 *
	 * @see ConditionBuilder
	 */
	public static enum Operator {

		LESS("<"), LESS_EQ("<="), EQ("=="), GREATER_EQ(">="), GREATER(">"), NOT_EQ(
				"<>");

		private String string;

		private Operator(String string) {
			this.string = string;
		}

		protected String getString() {
			return string;
		}

	}

	/**
	 * Variable defining grouping level. A group represents a statement inside
	 * brackets. Every time a group is opened, grouping level is incremented
	 * and, vice-versa - every time group is closed, grouping level is
	 * decremented. At the end of building condition, grouping level should be
	 * zero.
	 */
	private int groupingLevel;

	/**
	 * String builder containing the content of a condition.
	 */
	private StringBuilder sb;

	/**
	 * Entity meta data.
	 */
	private EntityMetaData entityMData;

	/**
	 * Default constructor.
	 *
	 * @param entityMData
	 *            entity meta data
	 */
	public ConditionBuilder(EntityMetaData entityMData) {
		groupingLevel = 0;
		this.entityMData = entityMData;
		sb = new StringBuilder(" WHERE ");
	}

	/**
	 * Method adds an expression in this condition. Null values are not
	 * supported.
	 *
	 * @param fieldName
	 * @param value
	 * @param op
	 * @return builder
	 * @throws IllegalStateException
	 *             if given field could not be found or if given value is null
	 */
	public ConditionBuilder addCondition(String fieldName, String value,
			Operator op) throws IllegalStateException {
		FieldMetaData fld = entityMData.getPersistentField(fieldName);

		if (fld == null) {
			throw new IllegalStateException("Field with name " + fieldName
					+ " not found.");
		}

		if (value == null) {
			throw new IllegalStateException("Given value must not be null.");
		}

		sb.append(fld.getColumnName()).append(op.getString()).append(value);

		return this;
	}

	/**
	 * Method opens a group (brackets) inside a condition.
	 *
	 * @return builder
	 */
	public ConditionBuilder openGroup() {
		sb.append('(');
		groupingLevel++;

		return this;
	}

	/**
	 * Method closes a group (brackets) inside a condition.
	 *
	 * @return builder
	 * @throws IllegalStateException
	 *             when more groups are closed than is opened
	 */
	public ConditionBuilder closeGroup() throws IllegalStateException {
		groupingLevel--;
		sb.append(')');

		if (groupingLevel < 0) {
			throw new IllegalStateException(
					"Grouping level is less than zero. More brackets is closed than is opened.\nCondition content:\n"
							+ sb.toString());
		}

		return this;
	}

	public ConditionBuilder and() {
		sb.append(" AND ");
		return this;
	}

	public ConditionBuilder or() {
		sb.append(" OR ");
		return this;
	}

	/**
	 * Method builds a condition string.
	 *
	 * @return condition string
	 * @throws IllegalStateException
	 */
	public String build() throws IllegalStateException {
		if (groupingLevel != 0) {
			throw new IllegalStateException(
					"Grouping level is not zero. Some brackets remained open.\nCondition content:\n"
							+ sb.toString());
		}

		return sb.toString();
	}

}
