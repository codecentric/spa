package de.codecentric.spa.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.Transient;
import de.codecentric.spa.metadata.EntityScanner.StringUtils;
import de.codecentric.spa.sql.SQLiteTypeMapper;

/**
 * Utility that scans class fields and returns {@link FieldMetaData}.
 */
public class FieldScanner {

    /**
     * Method checks if the given field is persistent field, i.e. if it is not annotated with {@link Transient} annotation.
     * 
     * @param f
     * @return true if the field is NOT annotated with {@link Transient} annotation
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
     * Method checks if the given field is identifier field, i.e. if it is annotated with {@link Id} annotation.
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

}
