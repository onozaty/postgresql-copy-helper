package com.github.onozaty.postgresql.copy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author onozaty
 */
public class ArrayRecordReader implements RecordReader {

    private final Iterator<Object[]> recordIterator;

    public ArrayRecordReader(Iterator<Object[]> recordIterator) {
        this.recordIterator = recordIterator;
    }

    public ArrayRecordReader(List<Object[]> records) {
        this(records.iterator());
    }

    public ArrayRecordReader(Object[]... records) {
        this(Arrays.asList(records));
    }

    @Override
    public Object[] read() {

        if (recordIterator.hasNext()) {
            return recordIterator.next();
        }

        return null;
    }

    @Override
    public void close() throws IOException {
    }
}
