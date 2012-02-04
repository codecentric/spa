package de.codecentric.spa.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * RelationshipMetaDataProvider is singleton class containing {@link RelationshipMetaData} information obtained through
 * {@link EntityScanner} scanning process.
 * 
 * @see EntityScanner#scanClass(Class)
 */
public class RelationshipMetaDataProvider {

	private static final RelationshipMetaDataProvider INSTANCE = new RelationshipMetaDataProvider();

	private HashMap<Class<?>, List<RelationshipMetaData>> metaDataMap;

	/**
	 * Hidden constructor.
	 */
	private RelationshipMetaDataProvider() {
		metaDataMap = new HashMap<Class<?>, List<RelationshipMetaData>>(0);
	}

	/**
	 * Method adds {@link RelationshipMetaData} to this provider. This object can be latter retrieved via
	 * {@link RelationshipMetaDataProvider#getMetaData(Class)} method.
	 * 
	 * @param cls
	 *            relationship describing class
	 * @param metaData
	 *            {@link RelationshipMetaData} to store in this provider
	 */
	public void addMetaData(Class<?> cls, RelationshipMetaData metaData) {
		List<RelationshipMetaData> metaDataList = metaDataMap.get(cls);
		if (metaDataList == null) {
			metaDataList = new ArrayList<RelationshipMetaData>();
		}
		metaDataList.add(metaData);
		metaDataMap.put(cls, metaDataList);
	}

	/**
	 * Method returns a list of {@link RelationshipMetaData} describing relationships between entity classes.
	 * 
	 * @param cls
	 *            class which database table is parent of the relationships
	 * @return list of {@link RelationshipMetaData}
	 */
	public List<RelationshipMetaData> getMetaData(Class<?> cls) {
		return metaDataMap.get(cls);
	}

	/**
	 * Method returns a list of {@link RelationshipMetaData} describing relationship between entity classes.
	 * 
	 * @param cls
	 *            class which database table is child of the relationships
	 * @return list of {@link RelationshipMetaData}
	 */
	public List<RelationshipMetaData> getMetaDataByChild(Class<?> cls) {
		List<RelationshipMetaData> result = new ArrayList<RelationshipMetaData>(0);

		Collection<List<RelationshipMetaData>> meta = metaDataMap.values();
		if (meta != null && !meta.isEmpty()) {
			Iterator<List<RelationshipMetaData>> i = meta.iterator();
			for (; i.hasNext();) {
				List<RelationshipMetaData> rMetaDataList = i.next();
				for (RelationshipMetaData rmd : rMetaDataList) {
					if (cls.equals(rmd.getChildClass())) {
						result.add(rmd);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Method returns a singleton instance of this class.
	 * 
	 * @return singleton instance of this class
	 */
	public static RelationshipMetaDataProvider getInstance() {
		return INSTANCE;
	}

}
