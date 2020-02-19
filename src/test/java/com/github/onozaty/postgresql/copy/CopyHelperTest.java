package com.github.onozaty.postgresql.copy;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
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

            List<Item> items = getItems(connection);

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

            List<Item> items = getItems(connection);

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
    public void copyFrom_bean_datetime() throws SQLException, IOException, IntrospectionException {

        try (BaseConnection connection = getConnection()) {

            createTables(connection);

            CopyHelper.copyFrom(
                    connection,
                    Arrays.asList(
                            Item.builder()
                                    .date(LocalDate.of(2020, 12, 24))
                                    .time(LocalTime.of(19, 10, 50, 123456789))
                                    .timestamp(LocalDateTime.of(2020, 12, 24, 23, 59, 59, 990000000))
                                    .timestampWithTimeZone(
                                            OffsetDateTime.of(
                                                    2019, 1, 2, 13, 14, 15, 123456000, ZoneOffset.ofHours(10)))
                                    .build()),
                    Item.class);

            // Since DbUtils does not support Date and Time API
            List<Map<String, Object>> itemMaps = getItemMaps(connection);

            assertThat(itemMaps)
                    .hasSize(1);
            assertThat(itemMaps.get(0))
                    .containsEntry("integer", null)
                    .containsEntry("text", null)
                    .containsEntry("date", toDate(LocalDate.of(2020, 12, 24)))
                    .containsEntry("time", toDate(LocalTime.of(19, 10, 50, 123456789)))
                    .containsEntry("timestamp", toTimestamp(LocalDateTime.of(2020, 12, 24, 23, 59, 59, 990000000)))
                    .containsEntry("timestamp_with_time_zone",
                            toTimestamp(OffsetDateTime.of(2019, 1, 2, 13, 14, 15, 123456000, ZoneOffset.ofHours(10))));

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
                        + "\"timestamp_with_time_zone\" timestamp with time zone)");
    }

    private List<Item> getItems(BaseConnection connection) throws SQLException {

        return new QueryRunner().query(
                connection,
                "SELECT * FROM items",
                new BeanListHandler<Item>(Item.class));
    }

    private List<Map<String, Object>> getItemMaps(BaseConnection connection) throws SQLException {

        return new QueryRunner().query(
                connection,
                "SELECT * FROM items",
                new MapListHandler());
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date toDate(LocalTime localTime) {

        return toDate(localTime.atDate(LocalDate.ofEpochDay(0)));
    }

    private Date toDate(LocalDateTime localDateTime) {

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Timestamp toTimestamp(LocalDateTime localDateTime) {

        return Timestamp.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date toTimestamp(OffsetDateTime offsetDateTime) {

        return Timestamp.from(offsetDateTime.toInstant());
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

        @Column("date")
        private LocalDate date;

        @Column("time")
        private LocalTime time;

        @Column("timestamp")
        private LocalDateTime timestamp;

        @Column("timestamp_with_time_zone")
        private OffsetDateTime timestampWithTimeZone;
    }
}
