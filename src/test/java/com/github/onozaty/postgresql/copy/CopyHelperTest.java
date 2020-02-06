package com.github.onozaty.postgresql.copy;

import static org.assertj.core.api.Assertions.assertThat;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CopyHelperTest {

    private static final String DATABASE_URL = "jdbc:postgresql://192.168.33.10:5432/testdb";
    private static final String DATABASE_USER = "user1";
    private static final String DATABASE_PASSWORD = "pass1";

    @Test
    public void copyFrom() throws SQLException, IOException {

        try (BaseConnection connection = getConnection()) {

            createTables(connection);

            CopyHelper.copyFrom(
                    connection,
                    "items",
                    Arrays.asList("integer", "text"),
                    new StringReader("1,a\n2,bb"));

            List<Item> items = getItem(connection);

            assertThat(items)
                    .containsExactlyInAnyOrder(
                            Item.builder()
                                    .integer(1)
                                    .text("a")
                                    .build(),
                            Item.builder()
                                    .integer(2)
                                    .text("bb")
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
    public static class Item {

        private Integer integer;

        private String text;

        private LocalDate date;

        private LocalTime time;

        private LocalDateTime timestamp;

        private OffsetDateTime timestampWithTimeZone;
    }
}
