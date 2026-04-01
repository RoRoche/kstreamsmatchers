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
package com.github.roroche.kstreamsmatchers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.test.TestRecord;

/**
 * A Kafka record with headers, key and value (following the Adapter pattern).
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Create a KafkaRecord from a ConsumerRecord
 * ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(
 *     "topic", 0, 0, "key", "value"
 * );
 * KafkaRecord<String, String> record = new KafkaRecord(consumerRecord);
 *
 * // Access headers, key, and value
 * Headers headers = record.headers();
 * String key = record.key();
 * String value = record.value();
 * }</pre>
 *
 * @since 0.0.1
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public final class KafkaRecord<K, V> implements WithHeaders, WithKey<K>, WithValue<V> {

    /**
     * The headers of the record.
     */
    private final Headers headers;

    /**
     * The key of the record.
     */
    private final K key;

    /**
     * The value of the record.
     */
    private final V value;

    /**
     * Primary ctor.
     *
     * @param headers The headers of the record
     * @param key The key of the record
     * @param value The value of the record
     */
    public KafkaRecord(final Headers headers, final K key, final V value) {
        this.headers = headers;
        this.key = key;
        this.value = value;
    }

    /**
     * Secondary ctor, from a {@link TestRecord}.
     *
     * @param trecord The test record to adapt
     */
    public KafkaRecord(final TestRecord<K, V> trecord) {
        this(
            trecord.headers(),
            trecord.key(),
            trecord.value()
        );
    }

    /**
     * Secondary ctor, from a {@link ConsumerRecord}.
     *
     * @param record The consumer record to adapt
     */
    public KafkaRecord(final ConsumerRecord<K, V> record) {
        this(
            record.headers(),
            record.key(),
            record.value()
        );
    }

    @Override
    public Headers headers() {
        return this.headers;
    }

    @Override
    public K key() {
        return this.key;
    }

    @Override
    public V value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("[headers=%s, key=%s, value=%s]", this.headers, this.key, this.value);
    }
}
