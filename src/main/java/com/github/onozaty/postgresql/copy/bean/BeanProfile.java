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
import lombok.Value;

/**
 * @author onozaty
 * @param <T> The target bean type
 */
@Builder(access = AccessLevel.PRIVATE)
@Value
public class BeanProfile<T> {

    private final String tableName;

    private final List<String> columnNames;

    private final Function<T, Object[]> columnValuesAccessor;

    public static <T> BeanProfile<T> of(Class<T> clazz) throws IntrospectionException {

        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        Map<String, Method> propertyMethodMap = Stream.of(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(
                        PropertyDescriptor::getName,
                        PropertyDescriptor::getReadMethod));

        BeanProfileBuilder<T> builder = BeanProfile.builder();

        Table table = clazz.getAnnotation(Table.class);

        if (table == null) {
            throw new IllegalArgumentException("Table annotation is not set.");
        }

        builder.tableName(table.value());

        List<ColumnProperty> columnProperties = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {

                Method method = propertyMethodMap.get(field.getName());
                if (method == null) {
                    throw new IllegalArgumentException(field.getName() + " accessor not found.");
                }

                columnProperties.add(
                        ColumnProperty.builder()
                                .columnName(column.value())
                                .accessor(method)
                                .build());
            }
        }

        for (Method method : clazz.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null) {

                columnProperties.add(
                        ColumnProperty.builder()
                                .columnName(column.value())
                                .accessor(method)
                                .build());
            }
        }

        builder.columnNames(
                columnProperties.stream()
                        .map(ColumnProperty::getColumnName)
                        .collect(Collectors.toList()));
        builder.columnValuesAccessor(newColumnValuesAccessor(columnProperties));

        return builder.build();
    }

    private static <T> Function<T, Object[]> newColumnValuesAccessor(List<ColumnProperty> columnProperties) {

        return target -> {

            Object[] values = new Object[columnProperties.size()];

            for (int i = 0; i < columnProperties.size(); i++) {
                try {
                    values[i] = columnProperties.get(i).getAccessor().invoke(target);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            return values;
        };
    }

    @Builder
    @Value
    private static class ColumnProperty {

        private String columnName;

        private Method accessor;
    }
}
