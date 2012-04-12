package de.codecentric.spa;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.FetchType;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.ManyToOne;
import de.codecentric.spa.annotations.OneToMany;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.metadata.EntityScanner.StringUtils;
import de.codecentric.spa.metadata.FieldMetaData;
import de.codecentric.spa.sql.ConditionBuilder;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;

/**
 * Entity helper class - it provides methods for basic operations with database
 * table.
 * 
 * @param <T>
 */
public class EntityHelper<T> {

	private PersistenceApplicationContext context;
	private EntityMetaData entityMData;
	private ContentValuesPreparer contentValuesPreparer;

	private String selectAllStmtSQL;
	private String selectSingleStmtSQL;

	/**
	 * Constructor - during the construction of instance {@link EntityMetaData}
	 * is retrieved from {@link EntityMetaDataProvider} used in given
	 * {@link PersistenceApplicationContext}. All further operations are
	 * performed on class and database table described with that
	 * {@link EntityMetaData}.
	 * 
	 * @param ctx
	 *            Application context in use
	 * @param cls
	 *            Class to be bound for this helper. Should be the same as
	 *            parameterized class.
	 */
	public EntityHelper(PersistenceApplicationContext ctx, Class<?> cls) {
		context = ctx;
		entityMData = ctx.getEntityMetaDataProvider().getMetaData(cls);
		contentValuesPreparer = new ContentValuesPreparer(ctx);

		SQLStatements sql = ctx.getSQLProvider().getSQL(cls);
		selectSingleStmtSQL = sql.getSelectSingleSQL();
		selectAllStmtSQL = sql.getSelectAllSQL();
	}

	/**
	 * Method returns single entity representing single row in database table or
	 * null if not found.
	 * 
	 * @param id
	 *            identifier value
	 * @return entity representing single row in database table or null if not
	 *         found
	 * @throws RuntimeException
	 */
	public T findById(Long id) throws RuntimeException {
		try {
			Class<?> cls = entityMData.getDescribingClass();

			@SuppressWarnings("unchecked")
			T entity = (T) cls.newInstance();

			Cursor c = context.getDatabaseHelper().getDatabase()
					.rawQuery(selectSingleStmtSQL, new String[] { String.valueOf(id) });
			if (c.moveToFirst()) {
				int columnCount = c.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					String colName = c.getColumnName(i);
					FieldMetaData mFld = resolveMetaField(entityMData, colName);
					readColumn(c, entity, i, mFld);
				}
			} else {
				entity = null;
			}
			c.close();
			readRelationColumnsForEntity(entity, cls);
			return entity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Read fields annotated using OneToMany or ManyToOne annotation. For each
	 * fetch children eagerly if field is annotated using FetchType.EAGER . In
	 * other case just skip field.
	 * 
	 * @param entity
	 *            parent entity which holds child collection
	 * @param cls
	 *            parent class
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void readRelationColumnsForEntity(T entity, Class<?> cls) throws IllegalArgumentException,
			IllegalAccessException {
		// in case there are one-many or many-to-one relations read values from
		// table and attach to current entity.
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(OneToMany.class) != null
					&& field.getAnnotation(OneToMany.class).fetch().equals(FetchType.EAGER)) {

				// Load "many" part of association. In this class it will be
				// referred as child.
				Type genericParameterTypes = field.getGenericType();
				Class<T> childClass = (Class<T>) ((ParameterizedType) genericParameterTypes).getActualTypeArguments()[0];
				EntityHelper eh = context.getEntityHelper(childClass);
				String columnName = getColumnName(field);
				Field primaryKeyField = getPrimaryKeyField(cls.getDeclaredFields());

				// TODO Check is it possible to make this more efficient.
				// This approach is necessarily because EntityHelper is
				// different for child entity.
				List<Class<?>> children = eh.findBy(" where " + columnName + "=" + primaryKeyField.get(entity));
				field.set(entity, children);

			} else if (field.getAnnotation(ManyToOne.class) != null
					&& field.getAnnotation(ManyToOne.class).fetch().equals(FetchType.EAGER)) {

				// Load "one" part of association. In this class it will be
				// referred as parent.
				Class<?> parentClass = field.getType();
				String columnName = getColumnName(field);
				String foreignKeyColumnValue = getForeignKeyValue(entity, columnName);
				EntityHelper ehParent = context.getEntityHelper(parentClass);
				Object parent = ehParent.findById(Long.parseLong(foreignKeyColumnValue));
				if (parent != null) {
					field.set(entity, parent);
				}

			}
		}
	}

	/**
	 * Return column name for given field.
	 * 
	 * @param field
	 * @return name of the column
	 */
	private String getColumnName(Field field) {
		String columnName = "";
		if (field.getAnnotation(Column.class) != null) {
			columnName = field.getAnnotation(Column.class).name();
		} else {
			columnName = StringUtils.uncamelize(field.getName());
		}
		return columnName;
	}

	/**
	 * Get foreign key value for entity. This method is used for many-to-one
	 * relations, to be able to obtain "one" part of relation.
	 * 
	 * @param entity
	 * @param columnNameParent
	 * @return
	 * @throws IllegalAccessException
	 */
	private String getForeignKeyValue(Object entity, String columnNameParent) throws IllegalAccessException {
		String foreignKeyColumnValue = "";

		Class<?> childClass = entity.getClass();
		Field[] allChildFields = childClass.getDeclaredFields();
		Field primaryFieldChild = getPrimaryKeyField(allChildFields);
		String typeName = primaryFieldChild.getType().getName();

		Cursor c = context.getDatabaseHelper().getDatabase()
				.rawQuery(selectSingleStmtSQL, new String[] { String.valueOf(primaryFieldChild.get(entity)) });
		if (c.moveToFirst()) {
			int columnCount = c.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				String colName = c.getColumnName(i);
				if (columnNameParent.equals(colName)) {
					if (int.class.getName().equals(typeName)) {
						Integer intVal = c.getInt(i);
						if (intVal != null) {
							foreignKeyColumnValue = intVal.toString();
						}
					} else if (Long.class.getName().equals(typeName)) {
						Long longVal = c.getLong(i);
						if (longVal != null) {
							foreignKeyColumnValue = longVal.toString();
						}
					}
				}
			}
		}
		return foreignKeyColumnValue;
	}

	/**
	 * Get primary key field.
	 * 
	 * @param declaredFields
	 * @return
	 */
	private Field getPrimaryKeyField(Field[] declaredFields) {
		for (Field field : declaredFields) {
			if (field.getAnnotation(Id.class) != null) {
				return field;
			}
		}
		return null;
	}

	/**
	 * Method returns a list of objects that fulfill given condition.
	 * 
	 * @param condition
	 *            a 'where' clause built before calling this method with
	 *            {@link ConditionBuilder}.
	 * @return list of objects or empty list if nothing is found
	 */
	public List<T> findBy(String condition) throws RuntimeException {
		try {
			Class<?> cls = entityMData.getDescribingClass();
			List<T> list = new ArrayList<T>();

			Cursor c = context.getDatabaseHelper().getDatabase()
					.rawQuery(selectAllStmtSQL + condition, new String[] {});
			while (c.moveToNext()) {
				@SuppressWarnings("unchecked")
				T entity = (T) cls.newInstance();

				int columnCount = c.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					String colName = c.getColumnName(i);
					FieldMetaData mFld = resolveMetaField(entityMData, colName);
					readColumn(c, entity, i, mFld);
				}
				readRelationColumnsForEntity(entity, cls);
				list.add(entity);
			}
			c.close();
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method returns list of all entities persisted in database table or empty
	 * list if nothing is found.
	 * 
	 * @return list of all entities persisted in database table or empty list if
	 *         nothing is found
	 * @throws RuntimeException
	 */
	public List<T> listAll() throws RuntimeException {
		try {
			Class<?> cls = entityMData.getDescribingClass();
			List<T> list = new ArrayList<T>();

			Cursor c = context.getDatabaseHelper().getDatabase().rawQuery(selectAllStmtSQL, new String[] {});
			while (c.moveToNext()) {
				@SuppressWarnings("unchecked")
				T entity = (T) cls.newInstance();

				int columnCount = c.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					String colName = c.getColumnName(i);
					FieldMetaData mFld = resolveMetaField(entityMData, colName);
					readColumn(c, entity, i, mFld);
				}
				readRelationColumnsForEntity(entity, cls);
				list.add(entity);

			}
			c.close();

			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method saves or updates the record in database table representing given
	 * entity.
	 * 
	 * @param object
	 *            entity to save or update
	 * @throws RuntimeException
	 */
	public void saveOrUpdate(final T object) throws RuntimeException {
		SQLiteDatabase db = context.getDatabaseHelper().getDatabase();
		db.beginTransaction();

		try {
			Long idVal = getIdentifierValue(object);

			if (idVal != 0) { // update entity
				String idColumn = entityMData.getIdentifier().getColumnName();
				String where = idColumn + " = ?";
				db.update(entityMData.getTableName(), contentValuesPreparer.prepareValues(object, entityMData), where,
						new String[] { String.valueOf(idVal) });
				updateCascadingRelationColumns(object, db);
			} else { // new one, insert it
				Long rowId = db.insert(entityMData.getTableName(), null,
						contentValuesPreparer.prepareValues(object, entityMData));
				if (rowId != -1) {
					setIdentifierValue(object, rowId);
					insertCascadingRelationColumns(object, db);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Cascade insert children in case entity is annotated using
	 * CascadeType.PERSIST or CascadeType.ALL .
	 * 
	 * @param entity
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("rawtypes")
	private void insertCascadingRelationColumns(Object entity, SQLiteDatabase db) throws IllegalArgumentException,
			IllegalAccessException {
		CascadeType[] acceptedCascadeTypes = { CascadeType.PERSIST, CascadeType.ALL };
		Class<?> cls = entityMData.getDescribingClass();
		Field[] fields = cls.getDeclaredFields();
		EntityTransactionCache eCache = EntityTransactionCache.getInstance();
		eCache.cache(entity);
		for (Field field : fields) {
			if (field.getAnnotation(OneToMany.class) != null
					&& (isProperCascadeType(field.getAnnotation(OneToMany.class).cascade(), acceptedCascadeTypes))) {
				// take "many" part of relation and persist
				Type genericParameterTypes = field.getGenericType();
				Class<?> childClass = (Class<?>) ((ParameterizedType) genericParameterTypes).getActualTypeArguments()[0];
				@SuppressWarnings("unchecked")
				List<Class<?>> children = (List<Class<?>>) field.get(entity);
				EntityHelper ehChild = context.getEntityHelper(childClass);
				for (Iterator<Class<?>> iterator = children.iterator(); iterator.hasNext();) {
					Object child = iterator.next();
					Long rowId = db.insert(ehChild.entityMData.getTableName(), null,
							contentValuesPreparer.prepareValues(child, ehChild.entityMData));
					if (rowId != -1) {
						setIdentifierValue(child, rowId);
					}
				}
			} else if (field.getAnnotation(ManyToOne.class) != null) {
				// In case there is no CascadeType.PERSIST defined we will force
				// this as default behavior .
				Class<?> parentClass = (Class<?>) field.getGenericType();
				Object parent = field.get(entity);
				EntityHelper ehParent = context.getEntityHelper(parentClass);
				Field primaryKeyFieldChild = getPrimaryKeyField(parentClass.getDeclaredFields());
				Object result = ehParent.findById(primaryKeyFieldChild.getLong(parent));
				if (result == null) {
					Long rowId = db.insert(ehParent.entityMData.getTableName(), null,
							contentValuesPreparer.prepareValues(parent, entityMData));
					if (rowId != -1) {
						setIdentifierValue(parent, rowId);
						field.set(entity, parent);
						eCache.cache(parent);
						updateEntity(entity);
					}
				} else {
					field.set(entity, parent);
					eCache.cache(parent);
					updateEntity(entity);
				}
			}
		}
		eCache.clear();
	}

	/**
	 * Helper method to update given entity.
	 * 
	 * @param entity
	 * @param field
	 * @param parent
	 * @throws IllegalAccessException
	 */
	private void updateEntity(Object entity) throws IllegalAccessException {
		Long idVal = getIdentifierValue(entity);
		String idColumn = entityMData.getIdentifier().getColumnName();
		String where = idColumn + " = ?";
		int rowsAffected = context
				.getDatabaseHelper()
				.getDatabase()
				.update(entityMData.getTableName(), contentValuesPreparer.prepareValues(entity, entityMData), where,
						new String[] { String.valueOf(idVal) });
		if (rowsAffected == 0) {
			throw new RuntimeException("No row to update!Database not consistent");
		}
	}

	/**
	 * Cascade update in case entity is annotated using CascadeType.REFRESH or
	 * CascadeType.ALL.
	 * 
	 * @param object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void updateCascadingRelationColumns(Object object, SQLiteDatabase db) throws IllegalArgumentException,
			IllegalAccessException {
		CascadeType[] acceptedCascadeTypes = { CascadeType.REFRESH, CascadeType.ALL };
		Class<?> cls = entityMData.getDescribingClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(OneToMany.class) != null
					&& (isProperCascadeType(field.getAnnotation(OneToMany.class).cascade(), acceptedCascadeTypes))) {
				// Take all children and do the update for each of them
				@SuppressWarnings("unchecked")
				List<Class<?>> children = (List<Class<?>>) field.get(object);
				for (Iterator<Class<?>> iterator = children.iterator(); iterator.hasNext();) {
					Object child = iterator.next();
					updateEntity(child);
				}
			} else if (field.getAnnotation(ManyToOne.class) != null
					&& (isProperCascadeType(field.getAnnotation(ManyToOne.class).cascade(), acceptedCascadeTypes))) {
				// Take the parent from child and do the update
				Object parent = field.get(object);
				updateEntity(parent);
			}

		}
	}

	/**
	 * Cascade delete in case entity is annotated using CascadeType.REMOVE or
	 * CascadeType.ALL .
	 * 
	 * @param id
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void deleteCascadingRelationColumns(Long id) throws IllegalArgumentException, IllegalAccessException {
		// in case id=-1, this will be indicator that deletion should be
		// performed for all entries.
		CascadeType[] acceptedCascadeTypes = { CascadeType.REMOVE, CascadeType.ALL };
		Class<?> cls = entityMData.getDescribingClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(OneToMany.class) != null
					&& (isProperCascadeType(field.getAnnotation(OneToMany.class).cascade(), acceptedCascadeTypes))) {
				// delete only children for parent with given id
				if (id != -1) {
					Object object = findById(id);
					@SuppressWarnings("unchecked")
					List<Class<?>> children = (List<Class<?>>) field.get(object);
					for (Iterator<Class<?>> iterator = children.iterator(); iterator.hasNext();) {
						Object child = iterator.next();
						deleteEntity(child);
					}
				} else {
					// parent id=-1, which indicates that all entries from
					// parent table will be deleted, so we have to
					// delete all from child table also.
					Type genericParameterTypes = field.getGenericType();
					Class<?> childClass = (Class<?>) ((ParameterizedType) genericParameterTypes)
							.getActualTypeArguments()[0];
					EntityHelper ehChild = context.getEntityHelper(childClass);
					ehChild.deleteAll();
				}

			}
		}
	}

	/**
	 * Delete given entity from database table.
	 * 
	 * @param entity
	 */
	private void deleteEntity(Object entity) {
		Long idVal = getIdentifierValue(entity);
		String idColumn = entityMData.getIdentifier().getColumnName();
		String tableName = entityMData.getTableName();
		String where = idColumn + " = ?";
		int count = context.getDatabaseHelper().getDatabase()
				.delete(tableName, where, new String[] { String.valueOf(idVal) });
		if (count == 0) {
			throw new RuntimeException("No entry was deleted. Database not consistent!");
		}

	}

	/**
	 * Check if current filed has expected cascading type
	 * 
	 * @param cascade
	 * @param cascadeTypesToCheck
	 * @return
	 */
	private boolean isProperCascadeType(CascadeType[] cascadeTypes, CascadeType[] acceptedCascadeTypes) {
		for (CascadeType currentCascadeType : cascadeTypes) {
			for (CascadeType acceptedCascadeType : acceptedCascadeTypes) {
				if (acceptedCascadeType.equals(currentCascadeType)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method deletes the entity with given id.
	 * 
	 * @param id
	 *            identifier value
	 * @return true if only one entity is deleted, otherwise false
	 */
	public boolean delete(Long id) {
		SQLiteDatabase db = context.getDatabaseHelper().getDatabase();
		db.beginTransaction();

		try {
			String tableName = entityMData.getTableName();
			String idColumn = entityMData.getIdentifier().getColumnName();
			String where = idColumn + " = ?";
			// first delete children, then parent
			deleteCascadingRelationColumns(id);
			int count = db.delete(tableName, where, new String[] { String.valueOf(id) });

			db.setTransactionSuccessful();

			return count == 1;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Method deletes all persisted entities in database table.
	 */
	public void deleteAll() {
		SQLiteDatabase db = context.getDatabaseHelper().getDatabase();
		db.beginTransaction();

		try {
			String tableName = entityMData.getTableName();
			db.delete(tableName, null, new String[] {});
			deleteCascadingRelationColumns(-1L);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Method should close used cursors and statements.
	 * 
	 * It does nothing in this implementation, but is present in order to
	 * provide extending capabilities.
	 */
	public void close() {
		// nothing to do
	}

	/**
	 * Method should compile used SQL statements.
	 * 
	 * It does nothing in this implementation, but is present in order to
	 * provide extending capabilities.
	 */
	public void compileSQLStatements() {
		// nothing to do
	}

	/**
	 * Method sets the value of the identifier field.
	 * 
	 * @param entity
	 *            object to set id value on
	 * @param id
	 *            identifier value
	 */
	protected void setIdentifierValue(final Object entity, Long id) {
		try {
			FieldMetaData identifier = entityMData.getIdentifier();

			Class<?> cls = entity.getClass();

			Field idFld = cls.getField(identifier.getFieldName());
			idFld.set(entity, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method returns the value of the identifier field.
	 * 
	 * @param entity
	 * @return identifier value
	 */
	protected Long getIdentifierValue(final Object entity) {
		try {
			Long idValue;

			FieldMetaData identifier = entityMData.getIdentifier();

			// Try to find object in entity cache...
			Object obj = EntityTransactionCache.getInstance().read(identifier.getDeclaringClass());

			// ... if nothing is cached, given entity holds the id
			// information,...
			if (obj == null) {
				Field idFld = entity.getClass().getField(identifier.getFieldName());
				idValue = (Long) idFld.get(entity);
			} else {
				// ... otherwise, we are dealing with entity which is part of
				// "bigger" entity.
				// This should be the case of one-to-one relationship, for
				// example.
				// In this case, cached object - parent object, holds the id
				// information.
				Field idFld = obj.getClass().getField(identifier.getFieldName());
				idValue = (Long) idFld.get(obj);
			}

			return idValue;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method reads cursor column at specific index into the entity data
	 * parameter and sets that value as value of data field described with mFld
	 * parameter.
	 * 
	 * @param c
	 *            database cursor
	 * @param data
	 *            entity instance
	 * @param idx
	 *            index of column that should be read
	 * @param mFld
	 *            meta data describing the column and corresponding data field
	 * @throws RuntimeException
	 */
	private void readColumn(final Cursor c, final T data, int idx, FieldMetaData mFld) throws RuntimeException {
		if (mFld == null) {
			return;
		}

		try {
			Field fld = null;
			Object particle = null;
			Field particleField = null;
			Class<?> cls = entityMData.getDescribingClass();

			String fieldName = mFld.getFieldName();
			// Check declaring class of given field meta data.
			if (mFld.getDeclaringClass().equals(cls)) {
				fld = cls.getField(fieldName);
			} else {
				// In this case we should seek for given field in substructure
				// of given entity.
				Field[] flds = cls.getFields();
				Class<?> particleClass = mFld.getDeclaringClass();
				fld = particleClass.getField(mFld.getFieldName());
				for (Field f : flds) {
					if (f.getType().equals(particleClass)) {
						particleField = f;
						if (particleField.get(data) == null) {
							particle = particleClass.newInstance();
						} else {
							particle = particleField.get(data);
						}
						break;
					}
				}
			}

			if (particle == null) {
				setColumnValue(c, data, idx, fld);
			} else {
				setColumnValue(c, particle, idx, fld);
				particleField.set(data, particle);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param c
	 * @param data
	 * @param idx
	 * @param fld
	 * @throws IllegalAccessException
	 */
	private void setColumnValue(final Cursor c, final Object data, int idx, Field fld) throws IllegalAccessException {
		// process the null value
		if (c.isNull(idx)) {
			fld.set(data, null);
			return;
		}

		String typeName = fld.getType().getName();
		if (byte[].class.getName().equals(typeName)) {

			fld.set(data, c.getBlob(idx));

		} else if (Double.class.getName().equals(typeName)) {

			fld.set(data, c.getDouble(idx));

		} else if (Float.class.getName().equals(typeName)) {

			fld.set(data, c.getFloat(idx));

		} else if (Integer.class.getName().equals(typeName)) {

			fld.set(data, c.getInt(idx));

		} else if (Byte.class.getName().equals(typeName)) {

			fld.set(data, c.getInt(idx));

		} else if (Long.class.getName().equals(typeName)) {

			fld.set(data, c.getLong(idx));

		} else if (Short.class.getName().equals(typeName)) {

			fld.set(data, c.getShort(idx));

		} else if (String.class.getName().equals(typeName)) {

			fld.set(data, c.getString(idx));

		} else if (Character.class.getName().equals(typeName)) {

			String s = c.getString(idx);
			if (s.length() > 0) {
				fld.set(data, s.charAt(0));
			}

		} else if (Boolean.class.getName().equals(typeName)) {

			fld.set(data, c.getInt(idx) == 1 ? true : false);

		} else if (Date.class.getName().equals(typeName)) {

			fld.set(data, new Date(c.getLong(idx)));

		}
	}

	/**
	 * Method returns {@link FieldMetaData} that is paired with given column of
	 * database table described with given {@link EntityMetaData}. If field is
	 * not found, null value is returned.
	 * 
	 * @param mData
	 *            meta data describing entity and corresponding database table
	 * @param colName
	 *            name of a column in database table
	 * @return {@link FieldMetaData} that is paired with given column
	 */
	private FieldMetaData resolveMetaField(EntityMetaData mData, String colName) {
		FieldMetaData retVal = null;

		List<FieldMetaData> fldList = entityMData.getPersistentFields();
		if (fldList != null && !fldList.isEmpty()) {
			for (FieldMetaData mFld : fldList) {
				if (colName.equals(mFld.getColumnName())) {
					retVal = mFld;
					break;
				}
			}
			if (retVal == null) {
				// field not found in the list of fields
				// we should try the identifier field
				FieldMetaData idMFld = mData.getIdentifier();
				if (idMFld != null && colName.equals(idMFld.getColumnName())) {
					retVal = idMFld;
				}
			}
		}

		return retVal;
	}

	/**
	 * Method returns current {@link PersistenceApplicationContext}.
	 * 
	 * @return
	 */
	public PersistenceApplicationContext getPersistenceApplicationContext() {
		return context;
	}

}
