package com.github.onozaty.postgresql.copy;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author onozaty
 */
public class ArrayRecordReader implements RecordReader {

    private final List<Object[]> records;

    private int lastIndex = -1;

    public ArrayRecordReader(List<Object[]> records) {
        this.records = records;
    }

    public ArrayRecordReader(Object[]... records) {
        this(Arrays.asList(records));
    }

    @Override
    public Object[] read() {

        if (lastIndex + 1 >= records.size()) {
            return null;
        }

        lastIndex++;
        return records.get(lastIndex);
    }

    @Override
    public void close() throws IOException {
    }
}
