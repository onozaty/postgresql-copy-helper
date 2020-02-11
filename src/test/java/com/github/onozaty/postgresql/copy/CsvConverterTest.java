package com.github.onozaty.postgresql.copy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class CsvConverterTest {

    private static final char INITIAL_VALUE = (char) 0;

    @Test
    public void read() throws IOException {

        try (CsvConverter csvConverter = new CsvConverter(
                new ArrayRecordReader(
                        new Object[] { "a", "b" },
                        new Object[] { 1, 2 }))) {

            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo('a');
            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo(',');
            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo('b');
            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo('\n');

            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo('1');
            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo(',');
            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo('2');
            assertThat(csvConverter.read()).isEqualTo('"');
            assertThat(csvConverter.read()).isEqualTo('\n');

            assertThat(csvConverter.read()).isEqualTo(-1);
        }
    }

    @Test
    public void read_range() throws IOException {

        try (CsvConverter csvConverter = new CsvConverter(
                new ArrayRecordReader(
                        new Object[] { "a", "b" },
                        new Object[] { 1, 2 }))) {

            char[] buf = new char[10];

            {
                Arrays.fill(buf, INITIAL_VALUE);
                int readSize = csvConverter.read(buf, 0, buf.length);

                assertThat(readSize).isEqualTo(10);
                assertThat(buf).containsExactly('"', 'a', '"', ',', '"', 'b', '"', '\n', '"', '1');
            }

            {
                Arrays.fill(buf, (char) 0);
                int readSize = csvConverter.read(buf, 0, buf.length);

                assertThat(readSize).isEqualTo(6);
                assertThat(buf).containsExactly(
                        '"', ',', '"', '2', '"', '\n',
                        INITIAL_VALUE, INITIAL_VALUE, INITIAL_VALUE, INITIAL_VALUE);
            }

            {
                Arrays.fill(buf, (char) 0);
                int readSize = csvConverter.read(buf, 0, buf.length);

                assertThat(readSize).isEqualTo(-1);
            }
        }
    }

    @Test
    public void read_range_offset() throws IOException {

        try (CsvConverter csvConverter = new CsvConverter(
                new ArrayRecordReader(
                        new Object[] { "a", "b" },
                        new Object[] { 1, 2 }))) {

            char[] buf = new char[10];

            {
                Arrays.fill(buf, INITIAL_VALUE);
                int readSize = csvConverter.read(buf, 2, 6);

                assertThat(readSize).isEqualTo(6);
                assertThat(buf).containsExactly(
                        INITIAL_VALUE, INITIAL_VALUE,
                        '"', 'a', '"', ',', '"', 'b',
                        INITIAL_VALUE, INITIAL_VALUE);
            }

            {
                Arrays.fill(buf, (char) 0);
                int readSize = csvConverter.read(buf, 1, 7);

                assertThat(readSize).isEqualTo(7);
                assertThat(buf).containsExactly(
                        INITIAL_VALUE,
                        '"', '\n', '"', '1', '"', ',', '"',
                        INITIAL_VALUE, INITIAL_VALUE);
            }

            {
                Arrays.fill(buf, (char) 0);
                int readSize = csvConverter.read(buf, 5, 5);

                assertThat(readSize).isEqualTo(3);
                assertThat(buf).containsExactly(
                        INITIAL_VALUE, INITIAL_VALUE, INITIAL_VALUE, INITIAL_VALUE, INITIAL_VALUE,
                        '2', '"', '\n',
                        INITIAL_VALUE, INITIAL_VALUE);
            }

            {
                Arrays.fill(buf, (char) 0);
                int readSize = csvConverter.read(buf, 0, buf.length);

                assertThat(readSize).isEqualTo(-1);
            }
        }
    }

    @Test
    public void read_range_over() throws IOException {

        try (CsvConverter csvConverter = new CsvConverter(
                new ArrayRecordReader(
                        new Object[] { "a", "b" },
                        new Object[] { 1, 2 }))) {

            char[] buf = new char[10];

            assertThatThrownBy(() -> csvConverter.read(buf, -1, 6))
                    .isInstanceOf(IndexOutOfBoundsException.class);

            assertThatThrownBy(() -> csvConverter.read(buf, 0, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);

            assertThatThrownBy(() -> csvConverter.read(buf, 0, 11))
                    .isInstanceOf(IndexOutOfBoundsException.class);

            assertThatThrownBy(() -> csvConverter.read(buf, 5, 6))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }
}
