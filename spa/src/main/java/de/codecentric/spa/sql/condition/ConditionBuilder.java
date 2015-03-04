package de.codecentric.spa.sql.condition;

import java.util.ArrayList;
import java.util.List;

public class ConditionBuilder {

    /**
     * Method returns the condition made of the given column name and list of values (columnName in (...values...)). If given list of values is null or empty, empty string will be returned.
     * 
     * @param columnName
     * @param values
     * @return
     */
    public static String buildStringValueInCondition(String columnName, List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        String condition = columnName + " in (";
        for (int i = 0; i < values.size(); i++) {
            condition += values.get(i);
            if (i < values.size() - 1) {
                condition += ", ";
            }
        }
        condition += ")";

        return condition;
    }

    /**
     * Method returns the condition made of the given column name and list of values (columnName in (...values...)). If given list of values is null or empty, empty string will be returned.
     * 
     * @param columnName
     * @param values
     * @return
     */
    public static String buildIntValueInCondition(String columnName, List<Integer> values) {
        List<String> stringValues = transformToListOfStringValues(values);
        return buildStringValueInCondition(columnName, stringValues);
    }

    /**
     * Method returns the condition made of the given column name and list of values (columnName in (...values...)). If given list of values is null or empty, empty string will be returned.
     * 
     * @param columnName
     * @param values
     * @return
     */
    public static String buildDoubleValueInCondition(String columnName, List<Double> values) {
        List<String> stringValues = transformToListOfStringValues(values);
        return buildStringValueInCondition(columnName, stringValues);
    }

    /**
     * Method returns the condition made of the given column name and list of values (columnName in (...values...)). If given list of values is null or empty, empty string will be returned.
     * 
     * @param columnName
     * @param values
     * @return
     */
    public static String buildFloatValueInCondition(String columnName, List<Float> values) {
        List<String> stringValues = transformToListOfStringValues(values);
        return buildStringValueInCondition(columnName, stringValues);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<String> transformToListOfStringValues(List values) {
        if (values == null || values.isEmpty()) {
            return values;
        }

        List<String> transformed = new ArrayList<String>(values.size());

        for (Object i : values) {
            transformed.add(i.toString());
        }

        return transformed;
    }

}
