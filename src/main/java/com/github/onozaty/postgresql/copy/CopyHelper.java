package com.github.onozaty.postgresql.copy;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

/**
 * @author onozaty
 */
public class CopyHelper {

    public static long copyFrom(BaseConnection connection, String tableName, List<String> columnNames, Reader reader)
            throws SQLException, IOException {

        String sql = createCopySql(tableName, columnNames);

        return new CopyManager(connection).copyIn(sql, reader);
    }

    private static String createCopySql(String tableName, List<String> columnNames) {

        return String.format(
                "COPY %s (%s) FROM STDIN (FORMAT csv)",
                tableName,
                columnNames.stream()
                        .map(x -> "\"" + x + "\"")
                        .collect(Collectors.joining(", ")));
    }
}