package com.github.onozaty.postgresql.copy.bean;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import lombok.Value;

public class BeanProfileTest {

    @Test
    public void of_field() throws IntrospectionException {

        BeanProfile<Entity1> beanProfile = BeanProfile.of(Entity1.class);

        assertThat(beanProfile.getTableName())
                .isEqualTo("table1");

        Map<String, Object> columnValueMap = getColumnValueMap(
                beanProfile, new Entity1(10, "name2", "address3"));
        assertThat(columnValueMap)
                .hasSize(3)
                .containsEntry("column1", 10)
                .containsEntry("column2", "name2")
                .containsEntry("column3", "address3");
    }

    @Test
    public void of_method() throws IntrospectionException {

        BeanProfile<Entity2> beanProfile = BeanProfile.of(Entity2.class);

        assertThat(beanProfile.getTableName())
                .isEqualTo("table2");

        Map<String, Object> columnValueMap = getColumnValueMap(beanProfile, new Entity2());
        assertThat(columnValueMap)
                .hasSize(3)
                .containsEntry("column1", 1)
                .containsEntry("column2", "a")
                .containsEntry("column3", "b");
    }

    @Test
    public void of_field_method() throws IntrospectionException {

        BeanProfile<Entity3> beanProfile = BeanProfile.of(Entity3.class);

        assertThat(beanProfile.getTableName())
                .isEqualTo("table3");

        Map<String, Object> columnValueMap = getColumnValueMap(beanProfile, new Entity3(9, "addr"));
        assertThat(columnValueMap)
                .hasSize(3)
                .containsEntry("column1", 9)
                .containsEntry("column2", "x")
                .containsEntry("column3", "addr");
    }

    private <T> Map<String, Object> getColumnValueMap(BeanProfile<T> beanProfile, T bean) {
        Object[] values = beanProfile.getColumnValuesAccessor().apply(bean);

        List<String> columnNames = beanProfile.getColumnNames();
        Map<String, Object> columnValueMap = new HashMap<>();

        for (int i = 0; i < values.length; i++) {
            columnValueMap.put(columnNames.get(i), values[i]);
        }

        return columnValueMap;
    }

    @Table("table1")
    @Value
    private static class Entity1 {

        @Column("column1")
        private int id;

        @Column("column2")
        private String name;

        @Column("column3")
        private String address;
    }

    @Table("table2")
    private static class Entity2 {

        @Column("column1")
        public int getId() {
            return 1;
        }

        @Column("column2")
        public String getName() {
            return "a";
        }

        @Column("column3")
        public String getAddress() {
            return "b";
        }
    }

    @Table("table3")
    @Value
    private static class Entity3 {

        @Column("column1")
        private final int id;

        @Column("column2")
        public String getName() {
            return "x";
        }

        @Column("column3")
        private String address;
    }
}
