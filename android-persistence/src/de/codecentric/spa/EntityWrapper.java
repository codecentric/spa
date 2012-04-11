package de.codecentric.spa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.codecentric.spa.ctx.PersistenceApplicationContext;

/**
 * Wrapper for {@link EntityHelper}. Contains list of all helpers that can be
 * used.
 */
public class EntityWrapper {

	// TODO Consider making this class singleton. Map should be filled during
	// scanning phase, when persistent classes are discovered.
	private Map<Class<?>, EntityHelper<?>> entityHelperMap;
	private PersistenceApplicationContext cont;

	/**
	 * Constructor which require context object in order to properly initialize
	 * entity helper classes.
	 * 
	 * @param con
	 *            PersistenceApplicationContext object
	 */
	public EntityWrapper(PersistenceApplicationContext context) {
		cont = context;
		entityHelperMap = new HashMap<Class<?>, EntityHelper<?>>(0);
	}

	/**
	 * Returns map which contains EntityClass-EntityHelper pairs.
	 * 
	 * @return map contains entity-helper pairs
	 */
	public Map<Class<?>, EntityHelper<?>> getEntityHelperMap() {
		return entityHelperMap;
	}

	/**
	 * Set map with entity-helper pairs.
	 * 
	 * @param entityHelperMap
	 *            map contains entity helpers.
	 */
	public void setEntityHelperMap(Map<Class<?>, EntityHelper<?>> entityHelperMap) {
		this.entityHelperMap = entityHelperMap;
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

	public <T> void saveOrUpdate(T entity) {
		// TODO consider returning boolean here in order to know if everything
		// went fine
		@SuppressWarnings("unchecked")
		EntityHelper<T> entityHelper = (EntityHelper<T>) getEntityHelper(entity.getClass());
		entityHelper.saveOrUpdate(entity);
	}

	/**
	 * Return entity helper for given entity class.
	 * 
	 * @param <T>
	 *            entity type
	 * @param clazz
	 *            entity class
	 * @return entity helper instance
	 */

	@SuppressWarnings("unchecked")
	private <T> EntityHelper<T> getEntityHelper(Class<T> clazz) {
		// Check if there is EntityHelper for this class, if not try to
		// initialize it and add into map.
		EntityHelper<T> entityHelper = null;
		if (entityHelperMap.containsKey(clazz)) {
			entityHelper = (EntityHelper<T>) entityHelperMap.get(clazz);
		} else {
			entityHelper = new EntityHelper<T>(cont, clazz);
			entityHelperMap.put(clazz, entityHelper);
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

	/**
	 * Find all entries which comply to the given condition.
	 * 
	 * @param <T>
	 *            type of entity
	 * @param condition
	 *            condition for search
	 * @param clazz
	 *            entity class
	 * @return list of entries
	 */
	public <T> List<T> findBy(String condition, Class<T> clazz) {
		EntityHelper<T> entityHelper = getEntityHelper(clazz);
		return entityHelper.findBy(condition);
	}
}
