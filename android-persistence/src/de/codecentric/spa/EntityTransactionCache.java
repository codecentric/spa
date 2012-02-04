package de.codecentric.spa;

import java.util.HashMap;

/**
 * This class represents a cache of entities which should have very short life - only while one transaction (operation)
 * lasts.
 * 
 * Example of usage: saving of complex entity being relationship parent class. In order to save child of any
 * relationship, values from parent object are needed (to insert/update them in foreign key columns). In that situation,
 * parent objects should be sought for in entity transaction cache.
 * 
 * With above said, proper usage of this cache would be like this: save relationship parent object, cache it, save child
 * object (read values of parent from cached entity), clear the cache.
 * 
 * NOTE: This class is designed to cache only one entity per given class at the time. If user tries to cache more than
 * one object of a single class in the same moment, objects most recently cached will overwrite those cached earlier.
 */
public class EntityTransactionCache {

	private static final EntityTransactionCache INSTANCE = new EntityTransactionCache();

	private HashMap<Class<?>, Object> cache;

	private EntityTransactionCache() {
		cache = new HashMap<Class<?>, Object>(0);
	}

	/**
	 * Method puts the entity into the cache.
	 * 
	 * @param entity
	 */
	public void cache(Object entity) {
		cache.put(entity.getClass(), entity);
	}

	/**
	 * Method returns the cached entity or null if there is no cached entity of given class.
	 * 
	 * @param cls
	 * @return cached entity or null
	 */
	public Object read(Class<?> cls) {
		return cache.get(cls);
	}

	/**
	 * Method clears whole cache.
	 */
	public void clear() {
		cache.clear();
	}

	/**
	 * Method returns the singleton instance of this cache.
	 * 
	 * @return instance of the cache
	 */
	public static EntityTransactionCache getInstance() {
		return INSTANCE;
	}

}
