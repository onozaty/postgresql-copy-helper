package com.github.onozaty.postgresql.copy;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;

/**
 * @author onozaty
 */
public class CsvConverter extends Reader {

    private final CSVFormat csvFormat;

    private final RecordReader recordReader;

    private final CharBuffer charBuffer = new CharBuffer();

    public CsvConverter(RecordReader recordReader, CSVFormat csvFormat) {
        this.recordReader = recordReader;
        this.csvFormat = csvFormat;
    }

    public CsvConverter(RecordReader recordReader) {
        this(recordReader, CSVFormat.POSTGRESQL_CSV);
    }

    @Override
    public int read() throws IOException {

        int ch = charBuffer.get();

        if (ch == -1) {

            Object[] values = recordReader.read();

            if (values != null) {
                csvFormat.printRecord(charBuffer, values);
                ch = charBuffer.get();
            }
        }

        return ch;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        if ((off < 0) || (len < 0) || (off + len > cbuf.length)) {
            throw new IndexOutOfBoundsException();
        }

        int readSize = 0;
        for (int i = 0; i < len; i++) {

            int ch = read();
            if (ch == -1) {
                break;
            }

            cbuf[off + i] = (char) ch;
            readSize++;
        }

        return readSize == 0 ? -1 : readSize;
    }

    @Override
    public void close() throws IOException {
        recordReader.close();
    }
}
