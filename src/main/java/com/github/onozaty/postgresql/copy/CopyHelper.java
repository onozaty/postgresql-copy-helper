package com.github.onozaty.postgresql.copy;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import com.github.onozaty.postgresql.copy.bean.BeanProfile;

/**
 * Helper for PostgreSQL COPY command.
 * 
 * @author onozaty
 */
public class CopyHelper {

    /**
     * Use the COPY command to copying from a reader into a database.
     *
     * @param connection Database connection
     * @param tableName The name of an existing table
     * @param columnNames List of columns to be copied
     * @param reader CSV content reader
     * @return number of rows updated
     * @throws SQLException
     * @throws IOException
     */
    public static long copyFrom(BaseConnection connection, String tableName, List<String> columnNames, Reader reader)
            throws SQLException, IOException {

        String sql = createCopySql(tableName, columnNames);

        return new CopyManager(connection).copyIn(sql, reader);
    }

    /**
     * Use the COPY command to copying from a record object list into a database.
     * 
     * @param <T> The target record type
     * @param connection Database connection
     * @param records A record object list
     * @param recordClass Type of the class for record object
     * @return number of rows updated
     * @throws SQLException
     * @throws IOException
     * @throws IntrospectionException
     */
    public static <T> long copyFrom(BaseConnection connection, List<T> records, Class<T> recordClass)
            throws SQLException, IOException, IntrospectionException {

        return copyFrom(connection, records.iterator(), recordClass);
    }

    /**
     * Use the COPY command to copying from a record object iterator into a database.
     * 
     * @param <T> The target record type
     * @param connection Database connection
     * @param records A record object iterator
     * @param recordClass Type of the class for record object
     * @return number of rows updated
     * @throws SQLException
     * @throws IOException
     * @throws IntrospectionException
     */
    public static <T> long copyFrom(BaseConnection connection, Iterator<T> records, Class<T> recordClass)
            throws SQLException, IOException, IntrospectionException {

        BeanProfile<T> beanProfile = BeanProfile.of(recordClass);

        String sql = createCopySql(beanProfile.getTableName(), beanProfile.getColumnNames());

        BeanRecordReader<T> recordReader = new BeanRecordReader<T>(beanProfile.getColumnValuesAccessor(), records);
        CsvConverter converter = new CsvConverter(recordReader);

        return new CopyManager(connection).copyIn(sql, converter);
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
