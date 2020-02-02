package com.github.onozaty.postgresql.copy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

/**
 * @author onozaty
 */
public class ArrayRecordReaderTest {

    @Test
    public void read() throws IOException {

        try (ArrayRecordReader recordReader = new ArrayRecordReader(
                new Object[] { "a", "b", "c" },
                new Object[] { 1, 2, 3 })) {

            assertThat(recordReader.read()).containsExactly("a", "b", "c");
            assertThat(recordReader.read()).containsExactly(1, 2, 3);
            assertThat(recordReader.read()).isNull();
        }
    }

}
