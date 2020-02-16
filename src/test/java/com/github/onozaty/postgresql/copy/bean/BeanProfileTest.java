package com.github.onozaty.postgresql.copy.bean;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.IntrospectionException;
import java.util.Arrays;

import org.junit.Test;

import lombok.Getter;

public class BeanProfileTest {

    @Test
    public void of() throws IntrospectionException {

        BeanProfile<Entity> beanProfile = BeanProfile.of(Entity.class);

        assertThat(beanProfile)
                .returns("table1", BeanProfile::getTableName)
                .returns(Arrays.asList("column1", "column2"), BeanProfile::getColumnNames);

    }

    @Table("table1")
    @Getter
    private static class Entity {

        @Column("column1")
        private int id;

        @Column("column2")
        private String name;
    }
}
