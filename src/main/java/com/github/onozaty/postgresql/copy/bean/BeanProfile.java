package com.github.onozaty.postgresql.copy.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * @author onozaty
 * @param <T> The target bean type
 */
@Builder(access = AccessLevel.PRIVATE)
@Value
public class BeanProfile<T> {

    private final String tableName;

    @Singular
    private final List<String> columnNames;

    private final Function<T, Object[]> columnValuesAccessor;

    public static <T> BeanProfile<T> of(Class<T> clazz) throws IntrospectionException {

        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        Map<String, Method> getterMap = Stream.of(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(
                        PropertyDescriptor::getName,
                        PropertyDescriptor::getReadMethod));

        BeanProfileBuilder<T> builder = BeanProfile.builder();

        Table table = clazz.getAnnotation(Table.class);

        if (table == null) {
            throw new IllegalArgumentException("Table annotation is not set.");
        }

        builder.tableName(table.value());

        List<Method> getters = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                builder.columnName(column.value());

                Method getter = getterMap.get(field.getName());
                if (getter == null) {
                    throw new IllegalArgumentException(field.getName() + " accessor not found.");
                }
                getters.add(getter);
            }
        }

        builder.columnValuesAccessor(newColumnValuesAccessor(getters));

        return builder.build();
    }

    private static <T> Function<T, Object[]> newColumnValuesAccessor(List<Method> getters) {

        return target -> {

            Object[] values = new Object[getters.size()];

            for (int i = 0; i < getters.size(); i++) {
                try {
                    values[i] = getters.get(i).invoke(target);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            return values;
        };
    }
}
