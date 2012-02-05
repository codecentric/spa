package de.codecentric.spa.metadata;

import java.util.HashMap;
import java.util.Set;

/**
 * EntityMetaDataProvider is singleton class containing {@link EntityMetaData}
 * information obtained through {@link EntityScanner} scanning process.
 * 
 * @see EntityScanner#scanClass(Class)
 */
public class EntityMetaDataProvider {

	private static final EntityMetaDataProvider INSTANCE = new EntityMetaDataProvider();

	private HashMap<Class<?>, EntityMetaData> metaDataMap;

	/**
	 * Hidden constructor.
	 */
	private EntityMetaDataProvider() {
		metaDataMap = new HashMap<Class<?>, EntityMetaData>(0);
	}

	/**
	 * Method adds {@link EntityMetaData} to this provider. This object can be
	 * latter retrieved via {@link EntityMetaDataProvider#getMetaData(Class)}
	 * method.
	 * 
	 * @param cls
	 *            class which meta data are passed in
	 * @param metaData
	 *            {@link EntityMetaData} to store in this provider
	 */
	public void addMetaData(Class<?> cls, EntityMetaData metaData) {
		metaDataMap.put(cls, metaData);
	}

	/**
	 * Method returns {@link EntityMetaData} describing given class.
	 * 
	 * @param cls
	 * @return {@link EntityMetaData}
	 */
	public EntityMetaData getMetaData(Class<?> cls) {
		return metaDataMap.get(cls);
	}

	/**
	 * Method returns an array of entity classes that were scanned with
	 * {@link EntityScanner}, i.e. persistent classes, or null if none was
	 * scanned.
	 * 
	 * @return array of persistent classes or null
	 */
	public Class<?>[] getPersistentClasses() {
		Set<Class<?>> clsSet = metaDataMap.keySet();
		if (clsSet != null && !clsSet.isEmpty()) {
			Class<?>[] clsArr = new Class<?>[clsSet.size()];
			return clsSet.toArray(clsArr);
		} else {
			return null;
		}
	}

	/**
	 * Method clears all previously cached meta data.
	 */
	public void clearMetaData() {
		metaDataMap.clear();
	}

	/**
	 * Method returns a singleton instance of this class.
	 * 
	 * @return singleton instance of this class
	 */
	public static EntityMetaDataProvider getInstance() {
		return INSTANCE;
	}

}
