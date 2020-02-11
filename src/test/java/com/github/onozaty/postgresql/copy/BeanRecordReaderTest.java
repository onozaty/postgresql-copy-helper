package com.github.onozaty.postgresql.copy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import lombok.Value;

/**
 * @author onozaty
 */
public class BeanRecordReaderTest {

    @Test
    public void read() throws IOException {

        try (BeanRecordReader<Record> recordReader = new BeanRecordReader<>(
                x -> new Object[] { x.getId(), x.getName() },
                new Record(1, "name1"),
                new Record(2, "name2"))) {

            assertThat(recordReader.read()).containsExactly(1, "name1");
            assertThat(recordReader.read()).containsExactly(2, "name2");
            assertThat(recordReader.read()).isNull();
        }
    }

    @Value
    private static class Record {

        private int id;

        private String name;
    }
}
