package de.codecentric.spa;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.FieldMetaData;
import de.codecentric.spa.metadata.RelationshipMetaData;
import de.codecentric.spa.metadata.RelationshipMetaDataProvider;

/**
 * Content values helper class - it provides methods for supplying given {@link ContentValues} with value of proper type from given object and its field.
 */
public class ContentValuesPreparer {

	private PersistenceApplicationContext context;

	public ContentValuesPreparer(PersistenceApplicationContext context) {
		this.context = context;
	}

	/**
	 * Method returns {@link ContentValues} object filled with column names and values.
	 * 
	 * NOTE: method does not work properly with byte[] parameters.
	 * 
	 * @param object
	 *            object which mapping is done
	 * @param emd
	 *            entity meta data
	 * @param fieldName
	 *            field defining the relationship containing given object; if this parameter is null, object will be treated as root of the relationship
	 * @return {@link ContentValues} object filled with column names and values
	 */
	public ContentValues prepareValues(final Object object, EntityMetaData emd, String fieldName) {
		ContentValues values = new ContentValues();

		try {

			List<FieldMetaData> mFieldList = emd.getPersistentFields();
			if (mFieldList != null && !mFieldList.isEmpty()) {

				for (FieldMetaData mFld : mFieldList) {
					Field dataFld = null;
					try {
						dataFld = object.getClass().getField(mFld.getFieldName());
						prepareSingleValue(values, object, mFld, dataFld);
					} catch (NoSuchFieldException e) {
						// If the field is not found, we should try a search
						// through complex structure.
						// Try to find a field of it's type and than field of
						// given name in substructure.
						Field[] flds = object.getClass().getFields();
						Object particle = null;
						for (Field f : flds) {
							Class<?> particleClass = f.getType();
							if (particleClass.equals(mFld.getDeclaringClass())) {
								dataFld = particleClass.getField(mFld.getFieldName());
								particle = f.get(object);
								break;
							}
						}
						if (dataFld != null) {
							prepareSingleValue(values, particle, mFld, dataFld);
						}
					}
				}

			}

			prepareRelationshipFieldValues(values, object, fieldName);

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		return values;
	}

	/**
	 * Method puts single value (determined by given meta field and data field from given object) into the values map.
	 * 
	 * @param values
	 * @param object
	 * @param mFld
	 * @param dataFld
	 * @throws Exception
	 */
	private void prepareSingleValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		String typeName = dataFld.getType().getName();

		if (Boolean.class.getName().equals(typeName)) {

			prepareBooleanValue(values, object, mFld, dataFld);

		} else if (Date.class.getName().equals(typeName)) {

			prepareDateValue(values, object, mFld, dataFld);

		} else if (byte[].class.getName().equals(typeName)) {

			// TODO

		} else if (Double.class.getName().equals(typeName)) {

			prepareDoubleValue(values, object, mFld, dataFld);

		} else if (Float.class.getName().equals(typeName)) {

			prepareFloatValue(values, object, mFld, dataFld);

		} else if (Integer.class.getName().equals(typeName)) {

			prepareIntegerValue(values, object, mFld, dataFld);

		} else if (Long.class.getName().equals(typeName)) {

			prepareLongValue(values, object, mFld, dataFld);

		} else if (Short.class.getName().equals(typeName)) {

			prepareShortValue(values, object, mFld, dataFld);

		} else if (Byte.class.getName().equals(typeName)) {

			prepareByteValue(values, object, mFld, dataFld);

		} else {

			prepareStringValue(values, object, mFld, dataFld);

		}

	}

	/**
	 * Method puts relationship values into the values map.
	 * 
	 * Parameter fieldName defines a field that defines the relationship containing given object. If given fieldName is null, method will assume that object is
	 * root of the relationship and will not search for relationship field values.
	 * 
	 * @param values
	 *            values
	 * @param object
	 *            object being persisted
	 * @param fieldName
	 *            a name of the field that defines the relationship containing this object
	 */
	private void prepareRelationshipFieldValues(final ContentValues values, final Object object, String fieldName) {
		// if (fieldName == null) {
		// return;
		// }
		// Go through relationship meta data of given entity's class...
		List<RelationshipMetaData> rMFieldList = RelationshipMetaDataProvider.getInstance().getMetaDataByChild(object.getClass());
		if (rMFieldList != null && !rMFieldList.isEmpty()) {
			EntityTransactionCache eCache = EntityTransactionCache.getInstance();
			for (RelationshipMetaData rmd : rMFieldList) {
				Class<?> parentClass = rmd.getParentClass();
				// Try to find parent entity in entity transaction cache and
				// use it's values.
				Object parentEntity = eCache.read(parentClass);
				if (parentEntity != null && fieldName.equals(rmd.getFieldName())) {
					EntityHelper entityHelper = context.getEntityHelper(parentClass);
					values.put(rmd.getForeignKeyColumnName(), entityHelper.getIdentifierValue(parentEntity));
				}
			}
		}
	}

	/**
	 * Method checks if given value is null and, if it is, puts the null value in the given values map. If given value is null, method will return true which
	 * should signal that value is already placed in the map and should be skipped by any of methods like
	 * {@link #prepareBooleanValue(ContentValues, Object, FieldMetaData, Field)} , {@link #prepareDateValue(ContentValues, Object, FieldMetaData, Field)}, ...
	 * 
	 * @param values
	 *            value map
	 * @param mFld
	 *            meta field describing the field which value is given
	 * @param value
	 *            object value
	 * @return true if given value is null, otherwise is null
	 */
	private boolean processNullValue(final ContentValues values, FieldMetaData mFld, Object value) {
		if (value == null) {
			values.putNull(mFld.getColumnName());
		}
		return value == null;
	}

	private void prepareBooleanValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Boolean value = (Boolean) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value ? 1 : 0);
		}
	}

	private void prepareDateValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Date value = (Date) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value.getTime());
		}
	}

	private void prepareDoubleValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Double value = (Double) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value);
		}
	}

	private void prepareFloatValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Float value = (Float) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value);
		}
	}

	private void prepareIntegerValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Integer value = (Integer) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value);
		}
	}

	private void prepareLongValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Long value = (Long) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value);
		}
	}

	private void prepareShortValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Short value = (Short) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value);
		}
	}

	private void prepareByteValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		Byte value = (Byte) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value);
		}
	}

	private void prepareStringValue(final ContentValues values, final Object object, FieldMetaData mFld, Field dataFld) throws Exception {
		String value = (String) dataFld.get(object);
		if (!processNullValue(values, mFld, value)) {
			values.put(mFld.getColumnName(), value);
		}
	}

}
