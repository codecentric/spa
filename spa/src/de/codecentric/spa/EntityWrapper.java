package de.codecentric.spa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.codecentric.spa.ctx.PersistenceApplicationContext;

/**
 * Singleton wrapper for {@link EntityHelper}. Contains list of all helpers that can be used.
 */
public class EntityWrapper {

    private static EntityWrapper INSTANCE;

    private Map<Class<?>, EntityHelper<?>> entityHelperMap;
    private PersistenceApplicationContext context;

    /**
     * Constructor which require context object in order to properly initialize entity helper classes.
     * 
     * @param con
     *            PersistenceApplicationContext object
     */
    private EntityWrapper(PersistenceApplicationContext context) {
        this.context = context;
        entityHelperMap = new HashMap<Class<?>, EntityHelper<?>>();
    }

    /**
     * Factory method.
     * 
     * @param context
     *            application persistence context
     * @return the singleton instance of this class
     */
    public static EntityWrapper getInstance(PersistenceApplicationContext context) {
        if (INSTANCE == null) {
            INSTANCE = new EntityWrapper(context);
        }
        return INSTANCE;
    }

    /**
     * Put entity helper into map.
     * 
     * @param clazz
     *            entity
     * @param entityHelper
     *            helper for current entity
     */
    public void putEntityHelper(Class<?> clazz, EntityHelper<?> entityHelper) {
        entityHelperMap.put(clazz, entityHelper);
    }

    /**
     * Remove entry identified with given entity class from map.
     * 
     * @param clazz
     *            remove map entry identified by given entity class.
     */
    public void removeEntityHelper(Class<?> clazz) {
        entityHelperMap.remove(clazz);
    }

    /**
     * Method persists given entity.
     * 
     * If it was a case of inserting new row in the database, an identifier value will be set on a given entity after calling this method.
     * 
     * @param entity
     *            entity to persist
     */
    @SuppressWarnings("unchecked")
    public <T> void saveOrUpdate(final T entity) {
        EntityHelper<T> entityHelper = (EntityHelper<T>) getEntityHelper(entity.getClass());
        entityHelper.saveOrUpdate(entity);
    }

    /**
     * Return entity helper for given entity class or null if none is already instantiated.
     * 
     * @param <T>
     *            entity type
     * @param clazz
     *            entity class
     * @return entity helper instance
     */

    @SuppressWarnings("unchecked")
    public <T> EntityHelper<T> getEntityHelper(Class<T> clazz) {
        EntityHelper<T> entityHelper = null;
        if (entityHelperMap.containsKey(clazz)) {
            entityHelper = (EntityHelper<T>) entityHelperMap.get(clazz);
        }
        return entityHelper;
    }

    /**
     * Delete entity for given id and class.
     * 
     * @param <T>
     * @param id
     *            primary key for given entity.
     * @param clazz
     *            entity class
     */
    public <T> void delete(Long id, Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        entityHelper.delete(id);
    }

    /**
     * Find entity for given id and class.
     * 
     * @param <T>
     *            entity type
     * @param id
     *            entity id
     * @param clazz
     *            entity class
     * @return entity with given id
     */
    public <T> T findById(Long id, Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        return entityHelper.findById(id);
    }

    /**
     * Find all entries which comply to the given condition.
     * 
     * @param <T>
     *            type of entity
     * @param condition
     *            condition for search (should not include 'where' word)
     * @param clazz
     *            entity class
     * @return list of entries
     */
    public <T> List<T> findBy(String condition, String[] args, Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        return entityHelper.findBy(condition, args);
    }

    /**
     * Method executes given SQL as already prepared select statement
     * 
     * @param sql
     * @param clazz
     * @return single instance or null
     */
    public <T> T executeSelectSingle(String sql, Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        return entityHelper.executeSelectSingle(sql);
    }

    /**
     * Method executes given SQL as already prepared select statement
     * 
     * @param sql
     * @param clazz
     * @return a list of instances or empty list
     */
    public <T> List<T> executeSelect(String sql, Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        return entityHelper.executeSelect(sql);
    }

    /**
     * Delete all entries of given class.
     * 
     * @param <T>
     *            type of entity
     * @param clazz
     *            entity class
     */
    public <T> void deleteAll(Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        entityHelper.deleteAll();
    }

    /**
     * Method deletes database rows using given where clause.
     * 
     * @param where
     *            a where clause (should not contain 'where' word)
     */
    public <T> void deleteBy(String where, String[] args, Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        entityHelper.deleteBy(where, args);
    }

    /**
     * List all entries of given class.
     * 
     * @param <T>
     *            entity type
     * @param clazz
     *            entity class
     * @return return list of entries
     */
    public <T> List<T> listAll(Class<T> clazz) {
        EntityHelper<T> entityHelper = getEntityHelper(clazz);
        return entityHelper.listAll();
    }

}
