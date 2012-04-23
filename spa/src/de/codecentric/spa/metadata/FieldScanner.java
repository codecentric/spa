package de.codecentric.spa.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.ManyToMany;
import de.codecentric.spa.annotations.ManyToOne;
import de.codecentric.spa.annotations.OneToMany;
import de.codecentric.spa.annotations.OneToOne;
import de.codecentric.spa.annotations.Transient;
import de.codecentric.spa.metadata.EntityScanner.StringUtils;
import de.codecentric.spa.metadata.RelationshipMetaData.RelationshipType;
import de.codecentric.spa.sql.SQLiteTypeMapper;

/**
 * Utility that scans class fields and returns {@link FieldMetaData}.
 */
public class FieldScanner {

	/**
	 * Method checks if the given field is relationship field, i.e. if it is
	 * annotated with one of annotations defining any kind of relationship
	 * (one-to-one, one-to-many, many-to-one, many-to-many).
	 * 
	 * @param f
	 * @return true if the field declares relationship between two classes
	 */
	public static boolean isRelationshipField(Field f) {
		if (Modifier.isStatic(f.getModifiers())) {
			return false;
		}

		return f.getAnnotation(OneToOne.class) != null || f.getAnnotation(OneToMany.class) != null
				|| f.getAnnotation(ManyToOne.class) != null || f.getAnnotation(ManyToMany.class) != null;
	}

	/**
	 * Method checks if the given field is persistent field, i.e. if it is
	 * annotated with {@link Transient} annotation.
	 * 
	 * @param f
	 * @return true if the field is NOT annotated with {@link Transient}
	 *         annotation
	 */
	public static boolean isPersistentField(Field f) {
		if (!Modifier.isStatic(f.getModifiers())) {
			Transient t = f.getAnnotation(Transient.class);
			return t == null;
		} else {
			return false;
		}

	}

	/**
	 * Method checks if the given field is identifier field, i.e. if it is
	 * annotated with {@link Id} annotation.
	 * 
	 * @param f
	 * @return true if the field is annotated with {@link Transient} annotation
	 */
	public static boolean isIdentifierField(Field f) {
		Id id = f.getAnnotation(Id.class);
		return id != null;
	}

	/**
	 * Method scans {@link Field} and returns appropriate {@link FieldMetaData}.
	 * 
	 * @param f
	 *            field to scan
	 * @return field meta data
	 */
	public static FieldMetaData scanField(Field f) {
		FieldMetaData mData = new FieldMetaData();
		mData.setFieldName(f.getName());
		mData.setColumnName(StringUtils.uncamelize(f.getName()));
		mData.setColumnType(SQLiteTypeMapper.mapFieldType(f));
		mData.setDeclaringClass(f.getDeclaringClass());
		return mData;
	}

	/**
	 * Method scans {@link Field} on which is declared relationship between two
	 * entity classes.
	 * 
	 * @param f
	 *            field to scan
	 */
	public static void scanRelationshipField(Field f) {
		if (f.getAnnotation(OneToOne.class) != null) {
			scanOneToOne(f);
		} else if (f.getAnnotation(OneToMany.class) != null) {
			scanOneToMany(f);
		} else if (f.getAnnotation(ManyToOne.class) != null) {
			scanManyToOne(f);
		} else {
			// TODO many-to-many
		}
	}

	/**
	 * Method scans {@link Field} on which is declared one-to-one relationship
	 * between two entity classes and sets appropriate columns as primary key
	 * for child of this relationship.
	 * 
	 * @param f
	 *            field to scan
	 */
	private static void scanOneToOne(Field f) {
		RelationshipMetaData result = new RelationshipMetaData();

		Class<?> cls = f.getType();
		result.setParentClass(cls);
		Class<?> declaringCls = f.getDeclaringClass();
		result.setChildClass(declaringCls);
		result.setRelationshipType(RelationshipType.ONE_TO_ONE);
		result.setFieldName(f.getName());

		Column c = f.getAnnotation(Column.class);
		if (c != null) {
			result.setForeignKeyColumnName(c.name());
		} else {
			result.setForeignKeyColumnName(StringUtils.uncamelize(cls.getSimpleName() + "_" + f.getName() + "_fk"));
		}

		RelationshipMetaDataProvider.getInstance().addMetaData(declaringCls, result);
	}

	/**
	 * Method scans {@link Field} on which is declared one-to-many relationship
	 * between two entity classes and puts appropriate
	 * {@link RelationshipMetaData} into {@link RelationshipMetaDataProvider}.
	 * 
	 * @param f
	 *            field to scan
	 */
	private static void scanOneToMany(Field f) {
		RelationshipMetaData result = new RelationshipMetaData();

		Class<?> cls = f.getDeclaringClass();
		result.setParentClass(cls);
		result.setRelationshipType(RelationshipType.ONE_TO_MANY);
		result.setFieldName(f.getName());

		Column c = f.getAnnotation(Column.class);
		if (c != null) {
			result.setForeignKeyColumnName(c.name());
		} else {
			result.setForeignKeyColumnName(StringUtils.uncamelize(cls.getSimpleName() + "_" + f.getName() + "_fk"));
		}

		Class<?> relationshipClass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
		result.setChildClass(relationshipClass);

		RelationshipMetaDataProvider.getInstance().addMetaData(cls, result);
	}

	/**
	 * Method scans {@link Field} on which is declared many-to-one relationship
	 * between two entity classes and puts appropriate
	 * {@link RelationshipMetaData} into {@link RelationshipMetaDataProvider}.
	 * 
	 * @param f
	 *            field to scan
	 */
	private static void scanManyToOne(Field f) {
		RelationshipMetaData result = new RelationshipMetaData();

		Class<?> cls = f.getDeclaringClass();
		result.setParentClass(f.getType());
		result.setChildClass(cls);
		result.setRelationshipType(RelationshipType.MANY_TO_ONE);
		result.setFieldName(f.getName());

		Column c = f.getAnnotation(Column.class);
		if (c != null) {
			result.setForeignKeyColumnName(c.name());
		} else {
			result.setForeignKeyColumnName(StringUtils.uncamelize(cls.getSimpleName() + "_" + f.getName() + "_fk"));
		}

		RelationshipMetaDataProvider.getInstance().addMetaData(cls, result);
	}

}
