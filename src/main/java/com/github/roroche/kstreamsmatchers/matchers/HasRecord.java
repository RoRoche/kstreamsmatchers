/*
 * MIT License
 *
 * Copyright (c) 2026 Romain Rochegude
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.roroche.kstreamsmatchers.matchers;

import com.github.roroche.kstreamsmatchers.KafkaRecord;
import com.github.roroche.kstreamsmatchers.WithHeaders;
import com.github.roroche.kstreamsmatchers.WithKey;
import com.github.roroche.kstreamsmatchers.WithValue;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.test.TestRecord;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * A Hamcrest matcher that checks if a {@link KafkaRecord} has specific headers, key and value.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Assert that a KafkaRecord has specific key and value
 * KafkaRecord<String, String> record = new KafkaRecord<>(headers, "user-1", "Hello World");
 * MatcherAssert.assertThat(
 *     record,
 *     new HasRecord<>("user-1", "Hello World")
 * );
 * }</pre>
 *
 * @since 0.0.1
 */
public final class HasRecord<K, V> extends TypeSafeMatcher<KafkaRecord<K, V>> {

    /**
     * The matcher for the headers of the record.
     */
    private final Matcher<WithHeaders> headers;

    /**
     * The matcher for the key of the record.
     */
    private final Matcher<WithKey<K>> key;

    /**
     * The matcher for the value of the record.
     */
    private final Matcher<WithValue<V>> value;

    /**
     * Constructs a HasRecord matcher with the given expected headers, key and value matchers.
     *
     * @param headers The matcher for the headers of the record
     * @param key The matcher for the key of the record
     * @param value The matcher for the value of the record
     */
    public HasRecord(
        final Matcher<WithHeaders> headers,
        final Matcher<WithKey<K>> key,
        final Matcher<WithValue<V>> value
    ) {
        this.headers = headers;
        this.key = key;
        this.value = value;
    }

    /**
     * Constructs a HasRecord matcher with
     * the expected key and value matchers, and ignoring headers.
     *
     * @param key The expected key of the record
     * @param value The expected value of the record
     */
    public HasRecord(final K key, final V value) {
        this(
            new IgnoreHeaders(),
            new HasKey<>(key),
            new HasValue<>(value)
        );
    }

    /**
     * Constructs a HasRecord matcher with the given expected key and value, and ignoring headers.
     *
     * @param keyvalue The expected key and value of the record
     */
    public HasRecord(final KeyValue<K, V> keyvalue) {
        this(keyvalue.key, keyvalue.value);
    }

    /**
     * Constructs a HasRecord matcher with the given expected key and value, and ignoring headers.
     *
     * @param entry The expected key and value of the record
     */
    public HasRecord(final Map.Entry<K, V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    @Override
    public boolean matchesSafely(final KafkaRecord<K, V> actual) {
        return Stream.of(
            this.headers,
            this.key,
            this.value
        ).allMatch(
            (final Matcher<?> matcher) -> matcher.matches(actual)
        );
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Record matching: [");
        this.headers.describeTo(description);
        description.appendText("], [");
        this.key.describeTo(description);
        description.appendText("], [");
        this.value.describeTo(description);
        description.appendText("]");
    }

    /**
     * A Hamcrest matcher that checks if a {@link TestRecord}
     * has specific headers, key and value, by converting it to a {@link KafkaRecord}.
     *
     * @param <K> The type of the key
     * @param <V> The type of the value
     *
     * @since 0.0.1
     */
    @SuppressWarnings("staticfree")
    public static final class FromTestRecord<K, V> extends TypeSafeMatcher<TestRecord<K, V>> {
        /**
         * The delegate matcher for the KafkaRecord converted from the TestRecord.
         */
        private final Matcher<KafkaRecord<K, V>> delegate;

        /**
         * Constructs a FromTestRecord matcher with the given delegate matcher.
         *
         * @param delegate The delegate matcher for the KafkaRecord converted from the TestRecord
         */
        public FromTestRecord(final Matcher<KafkaRecord<K, V>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean matchesSafely(final TestRecord<K, V> record) {
            return this.delegate.matches(
                new KafkaRecord<>(record)
            );
        }

        @Override
        public void describeTo(final Description description) {
            this.delegate.describeTo(description);
        }
    }
}
