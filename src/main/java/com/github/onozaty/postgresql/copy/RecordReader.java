package com.github.onozaty.postgresql.copy;

import java.io.Closeable;

/**
 * @author onozaty
 */
public interface RecordReader extends Closeable {

    Object[] read();
}
