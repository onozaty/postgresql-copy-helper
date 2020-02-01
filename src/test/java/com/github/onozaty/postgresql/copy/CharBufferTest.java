package com.github.onozaty.postgresql.copy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

/**
 * @author onozaty
 */
public class CharBufferTest {

    @Test
    public void get() {

        CharBuffer buffer = new CharBuffer();

        assertThat(buffer.get()).isEqualTo(-1);
        assertThat(buffer.get()).isEqualTo(-1);
    }

    @Test
    public void append_get() throws IOException {

        CharBuffer buffer = new CharBuffer();

        buffer.append('a');

        assertThat(buffer.get()).isEqualTo('a');
        assertThat(buffer.get()).isEqualTo(-1);
    }

    @Test
    public void append_append_get() throws IOException {

        CharBuffer buffer = new CharBuffer();

        buffer.append('a').append('b');

        assertThat(buffer.get()).isEqualTo('a');
        assertThat(buffer.get()).isEqualTo('b');
        assertThat(buffer.get()).isEqualTo(-1);
    }

    @Test
    public void get_append_get() throws IOException {

        CharBuffer buffer = new CharBuffer();

        assertThat(buffer.get()).isEqualTo(-1);

        buffer.append('a');

        assertThat(buffer.get()).isEqualTo('a');
        assertThat(buffer.get()).isEqualTo(-1);
    }

    @Test
    public void append_string() throws IOException {

        CharBuffer buffer = new CharBuffer();

        buffer.append("abc");

        assertThat(buffer.get()).isEqualTo('a');
        assertThat(buffer.get()).isEqualTo('b');
        assertThat(buffer.get()).isEqualTo('c');
        assertThat(buffer.get()).isEqualTo(-1);
    }

    @Test
    public void append_string_range() throws IOException {

        CharBuffer buffer = new CharBuffer();

        buffer.append("abcdefg", 2, 4);

        assertThat(buffer.get()).isEqualTo('c');
        assertThat(buffer.get()).isEqualTo('d');
        assertThat(buffer.get()).isEqualTo(-1);
    }

}
