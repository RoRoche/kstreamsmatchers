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

import java.nio.charset.StandardCharsets;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.test.TestRecord;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link KafkaRecord}.
 * @since 0.0.2
 */
@SuppressWarnings({
    "allpublic",
    "allfinal",
    "staticfree",
    "JTCOP.RuleProhibitStaticFields"
})
final class KafkaRecordTest {

    /**
     * Constant for topic.
     */
    private static final String TOPIC = "topic";

    /**
     * Constant for key.
     */
    private static final String KEY = "key-1";

    /**
     * Constant for value.
     */
    private static final String VALUE = "value-1";

    /**
     * Constant for header key.
     */
    private static final String HEADER_KEY = "request-id";

    /**
     * Constant for header value.
     */
    private static final String HEADER_VALUE = "12345";

    @Test
    void isPrimaryConstructorExposingGivenKey() {
        MatcherAssert.assertThat(
            "When built from headers, key and value, the key should be exposed as is",
            new KafkaRecord<>(
                new RecordHeaders(),
                KafkaRecordTest.KEY,
                KafkaRecordTest.VALUE
            ).key(),
            new IsEqual<>(KafkaRecordTest.KEY)
        );
    }

    @Test
    void isPrimaryConstructorExposingGivenValue() {
        MatcherAssert.assertThat(
            "When built from headers, key and value, the value should be exposed as is",
            new KafkaRecord<>(
                new RecordHeaders(),
                KafkaRecordTest.KEY,
                KafkaRecordTest.VALUE
            ).value(),
            new IsEqual<>(KafkaRecordTest.VALUE)
        );
    }

    @Test
    void isPrimaryConstructorExposingGivenHeaders() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add(
            KafkaRecordTest.HEADER_KEY,
            KafkaRecordTest.HEADER_VALUE.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "When built from headers, key and value, the headers should be exposed as is",
            new KafkaRecord<>(headers, KafkaRecordTest.KEY, KafkaRecordTest.VALUE)
                .headers()
                .lastHeader(KafkaRecordTest.HEADER_KEY)
                .value(),
            new IsEqual<>(KafkaRecordTest.HEADER_VALUE.getBytes(StandardCharsets.UTF_8))
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Test
    void isConstructedFromTestRecordAdaptingKey() {
        MatcherAssert.assertThat(
            "When adapted from a TestRecord, the key should be taken from that test record",
            new KafkaRecord<>(new TestRecord<>(KafkaRecordTest.KEY, KafkaRecordTest.VALUE)).key(),
            new IsEqual<>(KafkaRecordTest.KEY)
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Test
    void isConstructedFromTestRecordAdaptingValue() {
        MatcherAssert.assertThat(
            "When adapted from a TestRecord, the value should be taken from that test record",
            new KafkaRecord<>(
                new TestRecord<>(KafkaRecordTest.KEY, KafkaRecordTest.VALUE)
            ).value(),
            new IsEqual<>(KafkaRecordTest.VALUE)
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Test
    void isConstructedFromTestRecordAdaptingHeaders() {
        MatcherAssert.assertThat(
            "When adapted from a TestRecord, the headers should be taken from that test record",
            new KafkaRecord<>(
                new TestRecord<>(KafkaRecordTest.KEY, KafkaRecordTest.VALUE)
            ).headers(),
            new IsNot<>(new IsNull<>())
        );
    }

    @Test
    void isConstructedFromConsumerRecordAdaptingKey() {
        MatcherAssert.assertThat(
            "When adapted from a ConsumerRecord, the key should be taken from that record",
            new KafkaRecord<>(
                new ConsumerRecord<>(
                    KafkaRecordTest.TOPIC, 0, 0, KafkaRecordTest.KEY, KafkaRecordTest.VALUE
                )
            ).key(),
            new IsEqual<>(KafkaRecordTest.KEY)
        );
    }

    @Test
    void isConstructedFromConsumerRecordAdaptingValue() {
        MatcherAssert.assertThat(
            "When adapted from a ConsumerRecord, the value should be taken from that record",
            new KafkaRecord<>(
                new ConsumerRecord<>(
                    KafkaRecordTest.TOPIC, 0, 0, KafkaRecordTest.KEY, KafkaRecordTest.VALUE
                )
            ).value(),
            new IsEqual<>(KafkaRecordTest.VALUE)
        );
    }

    @Test
    void isConstructedFromConsumerRecordAdaptingHeaders() {
        MatcherAssert.assertThat(
            "When adapted from a ConsumerRecord, the headers should be taken from that record",
            new KafkaRecord<>(
                new ConsumerRecord<>(
                    KafkaRecordTest.TOPIC, 0, 0, KafkaRecordTest.KEY, KafkaRecordTest.VALUE
                )
            ).headers(),
            new IsNot<>(new IsNull<>())
        );
    }

    @Test
    void isToStringContainsHeadersKeyAndValue() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add(
            KafkaRecordTest.HEADER_KEY,
            KafkaRecordTest.HEADER_VALUE.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "The textual representation should mention the key and the value",
            new KafkaRecord<>(headers, KafkaRecordTest.KEY, KafkaRecordTest.VALUE).toString(),
            new AllOf<>(
                new StringContains(KafkaRecordTest.KEY),
                new StringContains(KafkaRecordTest.VALUE)
            )
        );
    }
}
