package com.github.onozaty.postgresql.copy;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;
import org.postgresql.core.BaseConnection;

import com.github.onozaty.postgresql.copy.bean.Column;
import com.github.onozaty.postgresql.copy.bean.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CopyHelperTest {

    private static final String DATABASE_URL = "jdbc:postgresql://192.168.33.10:5432/testdb";
    private static final String DATABASE_USER = "user1";
    private static final String DATABASE_PASSWORD = "pass1";

    @Test
    public void copyFrom_reader() throws SQLException, IOException {

        try (BaseConnection connection = getConnection()) {

            createTables(connection);

            CopyHelper.copyFrom(
                    connection,
                    "items",
                    Arrays.asList("integer", "text"),
                    new StringReader("1,text1\n2,text2"));

            List<Item> items = getItem(connection);

            assertThat(items)
                    .containsExactlyInAnyOrder(
                            Item.builder()
                                    .integer(1)
                                    .text("text1")
                                    .build(),
                            Item.builder()
                                    .integer(2)
                                    .text("text2")
                                    .build());
        }
    }

    @Test
    public void copyFrom_bean() throws SQLException, IOException, IntrospectionException {

        try (BaseConnection connection = getConnection()) {

            createTables(connection);

            CopyHelper.copyFrom(
                    connection,
                    Arrays.asList(
                            Item.builder()
                                    .integer(1)
                                    .text("text1")
                                    .build(),
                            Item.builder()
                                    .integer(2)
                                    .text("text2")
                                    .build()),
                    Item.class);

            List<Item> items = getItem(connection);

            assertThat(items)
                    .containsExactlyInAnyOrder(
                            Item.builder()
                                    .integer(1)
                                    .text("text1")
                                    .build(),
                            Item.builder()
                                    .integer(2)
                                    .text("text2")
                                    .build());
        }
    }

    private BaseConnection getConnection() throws SQLException {

        return (BaseConnection) DriverManager.getConnection(
                DATABASE_URL,
                DATABASE_USER,
                DATABASE_PASSWORD);
    }

    private void createTables(BaseConnection connection) throws SQLException {

        new QueryRunner().update(
                connection,
                "DROP TABLE IF EXISTS items;"
                        + "CREATE TEMPORARY TABLE items ("
                        + "\"integer\" integer, "
                        + "\"text\" text, "
                        + "\"date\" date, "
                        + "\"time\" time, "
                        + "\"timestamp\" timestamp,"
                        + "\"timestamp_with_time_zone\" timestamp without time zone)");
    }

    private List<Item> getItem(BaseConnection connection) throws SQLException {

        return new QueryRunner().query(
                connection,
                "SELECT * FROM items",
                new BeanListHandler<Item>(Item.class));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table("items")
    public static class Item {

        @Column("integer")
        private Integer integer;

        @Column("text")
        private String text;

        private LocalDate date;

        private LocalTime time;

        private LocalDateTime timestamp;

        private OffsetDateTime timestampWithTimeZone;
    }
}
