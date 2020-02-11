package com.github.onozaty.postgresql.copy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @author onozaty
 */
public class BeanRecordReader<T> implements RecordReader {

    private final Iterator<T> recordIterator;

    private final Function<T, Object[]> propertyAccessor;

    public BeanRecordReader(Function<T, Object[]> propertyAccessor, Iterator<T> recordIterator) {
        this.propertyAccessor = propertyAccessor;
        this.recordIterator = recordIterator;
    }

    public BeanRecordReader(Function<T, Object[]> propertyAccessor, List<T> records) {
        this(propertyAccessor, records.iterator());
    }

    public BeanRecordReader(Function<T, Object[]> propertyAccessor, @SuppressWarnings("unchecked") T... records) {
        this(propertyAccessor, Arrays.asList(records));
    }

    @Override
    public Object[] read() {

        if (recordIterator.hasNext()) {
            return propertyAccessor.apply(recordIterator.next());
        }

        return null;
    }

    @Override
    public void close() throws IOException {
    }
}
