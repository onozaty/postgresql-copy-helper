package com.github.onozaty.postgresql.copy;

import java.io.IOException;

/**
 * @author onozaty
 */
class CharBuffer implements Appendable {

    private final StringBuilder stringBuffer = new StringBuilder();
    private int lastIndex = -1;

    public int get() {
        if (lastIndex + 1 < stringBuffer.length()) {
            lastIndex++;
            return stringBuffer.charAt(lastIndex);
        }

        stringBuffer.setLength(0);
        lastIndex = -1;

        return -1;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        stringBuffer.append(csq);
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        stringBuffer.append(csq, start, end);
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        stringBuffer.append(c);
        return this;
    }
}
