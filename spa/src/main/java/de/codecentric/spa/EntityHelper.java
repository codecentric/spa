package de.codecentric.spa;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.ctx.PersistenceContext;
import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.metadata.EntityScanner.StringUtils;
import de.codecentric.spa.metadata.FieldMetaData;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;

/**
 * Entity helper class - it provides methods for basic operations with database table.
 * 
 * @param <T>
 */
public class EntityHelper<T> {

    private PersistenceContext persistenceContext;
    private EntityMetaData entityMData;
    private ContentValuesPreparer contentValuesPreparer;

    private String selectAllStmtSQL;
    private String selectSingleStmtSQL;
    private String insertStmtSQL;

    /**
     * Constructor - during the construction of instance {@link EntityMetaData} is retrieved from {@link EntityMetaDataProvider} used in given {@link PersistenceContext}. All further operations are
     * performed on class and database table described with that {@link EntityMetaData}.
     * 
     * @param ctx
     *            Application context in use
     * @param cls
     *            Class to be bound for this helper. Should be the same as parameterized class.
     */
    public EntityHelper(PersistenceContext ctx, Class<?> cls) {
        persistenceContext = ctx;
        entityMData = ctx.getEntityMetaDataProvider().getMetaData(cls);
        contentValuesPreparer = new ContentValuesPreparer(ctx);

        SQLStatements sql = ctx.getSQLProvider().getSQL(cls);
        selectSingleStmtSQL = sql.getSelectSingleSQL();
        selectAllStmtSQL = sql.getSelectAllSQL();
        insertStmtSQL = sql.getInsertSQL();
    }

    /**
     * Method returns single entity representing single row in database table or null if not found.
     * 
     * NOTE: method works based on lazy loading logic - it will not load any relationship data.
     * 
     * @param id
     *            identifier value
     * @return entity representing single row in database table or null if not found
     */
    @SuppressWarnings("unchecked")
    public T findById(Long id) {
        try {
            Class<?> cls = entityMData.getDescribingClass();
            T entity = (T) cls.newInstance();

            Cursor c = persistenceContext.getDatabaseHelper().getDatabase().rawQuery(selectSingleStmtSQL, new String[] { String.valueOf(id) });
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
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method returns a list of objects that fulfill given condition.
     * 
     * NOTE: method works based on lazy loading logic - it will not load any relationship data.
     * 
     * @param condition
     *            a 'where' clause built before calling this method with {@link ConditionBuilder} (should not include 'where' word)
     * @return list of objects or empty list if nothing is found
     */
    @SuppressWarnings("unchecked")
    public List<T> findBy(String condition, String[] parameters) {
        try {
            Class<?> cls = entityMData.getDescribingClass();
            List<T> list = new ArrayList<T>();

            String query = selectAllStmtSQL;
            if (condition != null && !condition.trim().equals("")) {
                query += " WHERE " + condition;
            }

            Cursor c = persistenceContext.getDatabaseHelper().getDatabase().rawQuery(query, parameters);
            while (c.moveToNext()) {
                T entity = (T) cls.newInstance();
                int columnCount = c.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String colName = c.getColumnName(i);
                    FieldMetaData mFld = resolveMetaField(entityMData, colName);
                    readColumn(c, entity, i, mFld);
                }
                list.add(entity);
            }
            c.close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method executes given SQL as already prepared select statement.
     * 
     * @param sql
     * @return a list of instances or empty list
     */
    @SuppressWarnings("unchecked")
    public List<T> executeSelect(String sql) {
        try {
            Class<?> cls = entityMData.getDescribingClass();
            List<T> list = new ArrayList<T>();

            Cursor c = persistenceContext.getDatabaseHelper().getDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                T entity = (T) cls.newInstance();
                int columnCount = c.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String colName = c.getColumnName(i);
                    FieldMetaData mFld = resolveMetaField(entityMData, colName);
                    readColumn(c, entity, i, mFld);
                }
                list.add(entity);
            }
            c.close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method executes given SQL as already prepared select statement.
     * 
     * @param sql
     * @return single instance or null
     */
    @SuppressWarnings("unchecked")
    public T executeSelectSingle(String sql) {
        try {
            Class<?> cls = entityMData.getDescribingClass();
            T entity = (T) cls.newInstance();

            Cursor c = persistenceContext.getDatabaseHelper().getDatabase().rawQuery(sql, null);
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
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
     * Get foreign key value for entity. This method is used for many-to-one relations, to be able to obtain "one" part of relation.
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

        Cursor c = persistenceContext.getDatabaseHelper().getDatabase().rawQuery(selectSingleStmtSQL, new String[] { String.valueOf(primaryFieldChild.get(entity)) });
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
        Field identifier = null;

        for (Field field : declaredFields) {
            if (field.getAnnotation(Id.class) != null) {
                identifier = field;
                break;
            }
        }

        if (identifier == null) {
            Class<?> superClass = entityMData.getDescribingClass().getSuperclass();
            if (superClass != null) {
                identifier = getPrimaryKeyField(superClass.getDeclaredFields());
            }
        }

        return identifier;
    }

    /**
     * Method returns list of all entities persisted in database table or empty list if nothing is found.
     * 
     * @return list of all entities persisted in database table or empty list if nothing is found
     * @throws RuntimeException
     */
    @SuppressWarnings("unchecked")
    public List<T> listAll() throws RuntimeException {
        try {
            Class<?> cls = entityMData.getDescribingClass();
            List<T> list = new ArrayList<T>();
            Cursor c = persistenceContext.getDatabaseHelper().getDatabase().rawQuery(selectAllStmtSQL, new String[] {});
            while (c.moveToNext()) {
                T entity = (T) cls.newInstance();
                int columnCount = c.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String colName = c.getColumnName(i);
                    FieldMetaData mFld = resolveMetaField(entityMData, colName);
                    readColumn(c, entity, i, mFld);
                }
                list.add(entity);
            }
            c.close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method inserts a batch of objects of a given class.
     * 
     * NOTE: it doesn't do save/update check, it just treats collection of objects as new objects and does the insert.
     * 
     * @param collection
     */
    public void batchInsert(List<T> collection) {
        List<FieldMetaData> fields = entityMData.getPersistentFields();

        SQLiteDatabase database = persistenceContext.getDatabaseHelper().getDatabase();
        SQLiteStatement statement = database.compileStatement(insertStmtSQL);

        try {
            database.beginTransaction();

            for (T entity : collection) {
                statement.clearBindings();
                ContentValues values = contentValuesPreparer.prepareValues(entity, entityMData, null);

                for (int i = 1; i <= fields.size(); i++) {
                    String column = fields.get(i - 1).getColumnName();
                    Object value = values.get(column);

                    if (value == null) {
                        statement.bindNull(i);
                    } else {
                        if (value instanceof Double) {
                            statement.bindDouble(i, (double) value);
                        } else if (value instanceof Long) {
                            statement.bindLong(i, (long) value);
                        } else if (value instanceof String) {
                            statement.bindString(i, (String) value);
                        } else if (value instanceof Integer || value instanceof Float || value instanceof Short) {
                            statement.bindString(i, String.valueOf(value));
                        } // TODO blob
                    }

                }
                statement.execute();
            }

            database.setTransactionSuccessful();
        } catch (SQLException exc) {

        } finally {
            database.endTransaction();
        }
    }

    /**
     * Method saves or updates the record in database table representing given entity.
     * 
     * @param object
     *            entity to save or update
     */
    public void saveOrUpdate(final T object) {
        SQLiteDatabase db = persistenceContext.getDatabaseHelper().getDatabase();
        db.beginTransaction();
        try {
            doSaveOrUpdate(object, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            db.endTransaction();
            EntityTransactionCache.getInstance().clear();
        }
    }

    /**
     * Method persists given object.
     * 
     * @param object
     * @param fieldName
     *            field defining the relationship containing given object; if this parameter is null, object will be treated as root of the relationship
     */
    private void doSaveOrUpdate(final T object, String fieldName) {
        if (object == null) {
            return;
        }

        SQLiteDatabase db = persistenceContext.getDatabaseHelper().getDatabase();

        try {
            Long idVal = getIdentifierValue(object);
            if (idVal != 0) { // update entity
                String idColumn = entityMData.getIdentifier().getColumnName();
                String where = idColumn + " = ?";
                int rowsAffected = db.update(entityMData.getTableName(), contentValuesPreparer.prepareValues(object, entityMData, fieldName), where, new String[] { String.valueOf(idVal) });
                if (rowsAffected == 0) {
                    throw new RuntimeException("No row to update! Database not consistent, problematic table: " + entityMData.getTableName() + ", row identifier: " + idVal);
                }
            } else { // new one, insert it
                Long rowId = db.insert(entityMData.getTableName(), null, contentValuesPreparer.prepareValues(object, entityMData, fieldName));
                if (rowId != -1) {
                    setIdentifierValue(object, rowId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method deletes the entity with given id.
     * 
     * @param id
     *            identifier value
     * @return true if only one entity is deleted, otherwise false
     */
    public boolean delete(Long id) {
        SQLiteDatabase db = persistenceContext.getDatabaseHelper().getDatabase();
        db.beginTransaction();

        try {
            String tableName = entityMData.getTableName();
            String idColumn = entityMData.getIdentifier().getColumnName();
            String where = idColumn + " = ?";
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
     * Method deletes all database rows from table for which entity helper instance is responsible.
     */
    public void deleteAll() {
        SQLiteDatabase db = persistenceContext.getDatabaseHelper().getDatabase();
        db.beginTransaction();

        try {
            String tableName = entityMData.getTableName();
            db.delete(tableName, null, new String[] {});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Method deletes database rows fulfilling given 'where' clause from table for which entity helper instance is responsible. This does not include deletion of attached objects (no cascading).
     * 
     * @param where
     *            a where clause (should not contain 'where' word)
     * @param args
     *            where clause arguments
     */
    public void deleteBy(String where, String[] args) {
        SQLiteDatabase db = persistenceContext.getDatabaseHelper().getDatabase();
        db.beginTransaction();

        try {
            db.delete(entityMData.getTableName(), where, args);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Method should close used cursors and statements.
     * 
     * It does nothing in this implementation, but is present in order to provide extending capabilities.
     */
    public void close() {
        // nothing to do
    }

    /**
     * Method should compile used SQL statements.
     * 
     * It does nothing in this implementation, but is present in order to provide extending capabilities.
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
     * Method reads cursor column at specific index into the entity data parameter and sets that value as value of data field described with mFld parameter.
     * 
     * @param c
     *            database cursor
     * @param data
     *            entity instance
     * @param idx
     *            index of column that should be read
     * @param mFld
     *            meta data describing the column and corresponding data field
     */
    private void readColumn(final Cursor c, final T data, int idx, FieldMetaData mFld) {
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
     * Method returns {@link FieldMetaData} that is paired with given column of database table described with given {@link EntityMetaData}. If field is not found, null value is returned.
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
     * Method returns current {@link PersistenceContext}.
     * 
     * @return
     */
    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

}
