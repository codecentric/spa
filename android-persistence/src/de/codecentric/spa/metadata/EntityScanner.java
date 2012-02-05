package de.codecentric.spa.metadata;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.Transient;
import de.codecentric.spa.metadata.RelationshipMetaData.RelationshipType;

/**
 * Utility that scans entity class and returns {@link EntityMetaData} containing the information how should the given
 * class be persisted.
 * 
 * Only classes annotated with {@link Entity} annotation can be scanned with this scanner.
 * 
 * NOTE: Discovery of foreign keys is not yet supported. Only fields declared as public are supported.
 */
public class EntityScanner {

	/**
	 * Method scans the given class and puts it's descriptor in shape of {@link EntityMetaData} into
	 * {@link EntityMetaDataProvider}. Class hierarchy of the given class is scanned starting from the given class to
	 * the most general class. Scanning process stops at the point where it comes to {@link Object} class.
	 * 
	 * If class is not annotated with {@link Entity} annotation, method will do nothing. Fields annotated with
	 * {@link Transient} annotation will be ignored since those should not be persisted.
	 * 
	 * Each class can have one identifier field (that field will be primary key in database table structure). Identifier
	 * field is field annotated with {@link Id} annotation. If there is more than one field annotated as identifier,
	 * field that is first resolved as identifier will be kept.
	 * 
	 * @param cls
	 *            class that will be scanned
	 * @param hasStructure
	 *            should be true if class should have separate structure, i.e. table, otherwise false
	 */
	public static void scanClass(Class<?> cls, boolean hasStructure) {
		EntityMetaData result = null;

		// Check if class should be scanned at all.
		if (isPersistentClass(cls)) {
			// Check if class is already scanned and if it is - do nothing, otherwise - scan it.
			EntityMetaDataProvider entityMetaDataProvider = EntityMetaDataProvider.getInstance();
			if (entityMetaDataProvider.getMetaData(cls) == null) {
				result = doScan(cls);

				if (result != null) {
					result.setHasStructure(hasStructure);
					// Put result in appropriate meta data provider.
					entityMetaDataProvider.addMetaData(cls, result);
				}

				// Check the relationship meta data related to this class.
				List<RelationshipMetaData> rMetaDataList = RelationshipMetaDataProvider.getInstance().getMetaData(cls);
				if (rMetaDataList != null && !rMetaDataList.isEmpty()) {

					// Scan all relationship classes implicitly.
					for (RelationshipMetaData rmd : rMetaDataList) {
						Class<?> toScan;
						RelationshipType rType = rmd.getRelationshipType();
						if (RelationshipType.MANY_TO_ONE.equals(rType)) {
							toScan = rmd.getParentClass();
						} else {
							toScan = rmd.getChildClass();
						}
						if (entityMetaDataProvider.getMetaData(toScan) == null) {
							scanClass(toScan, !RelationshipType.ONE_TO_ONE.equals(rType));
						}
					}

					// After scanning is done, iterate once again through the relationship meta data in order to...
					for (RelationshipMetaData rmd : rMetaDataList) {
						// ... use ONE_TO_ONE relationship to copy persistent fields to the parent class.
						//TODO Check this! This way we do not have distinction between one-to-one relation in eager and lazy mode. In both cases 
						// there is EAGER strategy.
						if (RelationshipType.ONE_TO_ONE.equals(rmd.getRelationshipType())) {
							EntityMetaData child = entityMetaDataProvider.getMetaData(rmd.getChildClass());
							result.getPersistentFields().addAll(child.getPersistentFields());
						}
					}
				}
			}
		}
	}

	/**
	 * Method does actual scanning of the given class and calls itself recursively in order to retrieve information
	 * about all it's super classes.
	 * 
	 * @param cls
	 *            class that will be scanned
	 * @return scanning result {@link EntityMetaData}
	 */
	private static EntityMetaData doScan(Class<?> cls) {
		if (Object.class.equals(cls)) {
			return null;
		}

		EntityMetaData result = new EntityMetaData(cls);

		Field[] fields = cls.getDeclaredFields();
		if (fields.length > 0) {
			for (Field f : fields) {
				if (!FieldScanner.isIdentifierField(f)) {
					if (FieldScanner.isRelationshipField(f)) {
						FieldScanner.scanRelationshipField(f);
					} else if (FieldScanner.isPersistentField(f)) {
						result.addPersistentField(FieldScanner.scanField(f));
					}
				} else {
					if (result.getIdentifier() == null && FieldScanner.isPersistentField(f)) {
						result.setIdentifier(FieldScanner.scanField(f));
					}
				}
			}
		}

		Class<?> superClass = cls.getSuperclass();
		if (superClass != null) {
			EntityMetaData recursiveResult = doScan(superClass);
			if (recursiveResult != null) {
				result = merge(result, recursiveResult);
			}
		}

		return result;
	}

	/**
	 * Method merges two results.
	 * 
	 * Merging will be done only if right describing class {@link EntityMetaData} is super class of left describing
	 * class {@link EntityMetaData}.
	 * 
	 * @param left
	 *            class being scanned
	 * @param right
	 *            super class of class being scanned
	 * @return merged result
	 * 
	 * @see EntityMetaData#getDescribingClass()
	 */
	private static EntityMetaData merge(EntityMetaData left, EntityMetaData right) {
		EntityMetaData result = new EntityMetaData(left.getDescribingClass());

		if (right.getIdentifier() != null) {
			result.setIdentifier(right.getIdentifier());
		} else if (left.getIdentifier() != null) {
			result.setIdentifier(left.getIdentifier());
		}

		List<FieldMetaData> rightPersistentFlds = right.getPersistentFields();
		if (rightPersistentFlds != null && !rightPersistentFlds.isEmpty()) {
			for (FieldMetaData f : rightPersistentFlds) {
				result.addPersistentField(f);
			}
		}

		List<FieldMetaData> leftPersistentFlds = left.getPersistentFields();
		if (leftPersistentFlds != null && !leftPersistentFlds.isEmpty()) {
			for (FieldMetaData f : leftPersistentFlds) {
				result.addPersistentField(f);
			}
		}

		return result;
	}

	/**
	 * Method checks if the given class is persistent class, i.e. if it is annotated with {@link Entity} annotation.
	 * 
	 * @param cls
	 * @return true if the class is annotated with {@link Entity} annotation.
	 */
	private static boolean isPersistentClass(Class<?> cls) {
		Entity e = cls.getAnnotation(Entity.class);
		return e != null;
	}

	/**
	 * Utility class containing methods easing the work with {@link String}.
	 */
	public static class StringUtils {

		/**
		 * Method uncamelizes the input string.
		 * 
		 * Input string is converted from camel case to a string in a form of string which words are separated with "_".
		 * For example, for given input "SimpleClassName" table name will be "simple_class_name".
		 * 
		 * @param input
		 * @return uncamelized string
		 */
		public static String uncamelize(String input) {
			if (input == null) {
				return null;
			}

			Pattern p = Pattern.compile("\\p{Lu}\\p{Lu}*");
			Matcher m = p.matcher(input);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				if (m.group().length() > 1) {
					StringBuffer caps = new StringBuffer(m.group());
					m.appendReplacement(sb, caps.insert(m.group().length() - 1, '_').toString());
				} else {
					m.appendReplacement(sb, ' ' + m.group());
				}
			}
			m.appendTail(sb);
			String whiteSpaceName = sb.toString().trim();
			return whiteSpaceName.toLowerCase().replace(' ', '_');
		}

	}

}
