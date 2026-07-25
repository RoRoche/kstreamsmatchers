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

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.KeyValue;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.pollinterval.FixedPollInterval;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ConsumerPolls}.
 * @since 0.0.1
 */
@SuppressWarnings({"allpublic", "allfinal", "staticfree", "JTCOP.RuleProhibitStaticFields"})
final class ConsumerPollsTest {

    /**
     * Constant for test topic.
     */
    private static final String TOPIC = "topic";

    /**
     * Constant for hello.
     */
    private static final String HELLO = "hello";

    /**
     * Constant for kafka.
     */
    private static final String KAFKA = "kafka";

    @Test
    void matchesFromKeyValueVarargs() {
        final MockConsumer<String, Long> consumer = ConsumerPollsTest.consumer();
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 0, ConsumerPollsTest.HELLO, 1L
            )
        );
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 1, ConsumerPollsTest.KAFKA, 2L
            )
        );
        MatcherAssert.assertThat(
            "When the consumer polls the expected records, the matcher should match",
            consumer,
            new ConsumerPolls<>(
                Duration.ofSeconds(2),
                new FixedPollInterval(Duration.ofMillis(50)),
                new KeyValue<>(ConsumerPollsTest.HELLO, 1L),
                new KeyValue<>(ConsumerPollsTest.KAFKA, 2L)
            )
        );
    }

    @Test
    void matchesFromMapEntryVarargs() {
        final MockConsumer<String, Long> consumer = ConsumerPollsTest.consumer();
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 0, ConsumerPollsTest.HELLO, 1L
            )
        );
        MatcherAssert.assertThat(
            "When constructed from Map.Entry varargs, the matcher should match the polled records",
            consumer,
            new ConsumerPolls<>(
                Duration.ofSeconds(2),
                new FixedPollInterval(Duration.ofMillis(50)),
                new MapEntry<>(ConsumerPollsTest.HELLO, 1L)
            )
        );
    }

    @Test
    void matchesFromListOfMatchers() {
        final MockConsumer<String, Long> consumer = ConsumerPollsTest.consumer();
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 0, ConsumerPollsTest.HELLO, 1L
            )
        );
        MatcherAssert.assertThat(
            "When constructed from a list of record matchers, the matcher should match the polled records",
            consumer,
            new ConsumerPolls<>(
                Duration.ofSeconds(2),
                new FixedPollInterval(Duration.ofMillis(50)),
                List.of(new HasRecord<>(ConsumerPollsTest.HELLO, 1L))
            )
        );
    }

    @Test
    void matchesUsingDefaultTimeoutAndInterval() {
        final MockConsumer<String, Long> consumer = ConsumerPollsTest.consumer();
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 0, ConsumerPollsTest.HELLO, 1L
            )
        );
        MatcherAssert.assertThat(
            "When using the default timeout and interval, the matcher should still match",
            consumer,
            new ConsumerPolls<>(new KeyValue<>(ConsumerPollsTest.HELLO, 1L))
        );
    }

    @Test
    void doesNotMatchWhenRecordOrderDiffers() {
        final MockConsumer<String, Long> consumer = ConsumerPollsTest.consumer();
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 0, ConsumerPollsTest.HELLO, 1L
            )
        );
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 1, ConsumerPollsTest.KAFKA, 2L
            )
        );
        MatcherAssert.assertThat(
            "When the polled records are out of the expected order, the matcher should not match",
            new ConsumerPolls<>(
                Duration.ofSeconds(2),
                new FixedPollInterval(Duration.ofMillis(50)),
                new KeyValue<>(ConsumerPollsTest.KAFKA, 2L),
                new KeyValue<>(ConsumerPollsTest.HELLO, 1L)
            ).matches(consumer),
            Matchers.is(false)
        );
    }

    @Test
    void throwsWhenExpectedNumberOfRecordsIsNeverPolledBeforeTimeout() {
        try (MockConsumer<String, Long> consumer = ConsumerPollsTest.consumer()) {
            consumer.addRecord(
                new ConsumerRecord<>(
                    ConsumerPollsTest.TOPIC, 0, 0, ConsumerPollsTest.HELLO, 1L
                )
            );
            Assertions.assertThrows(
                ConditionTimeoutException.class,
                () -> new ConsumerPolls<>(
                    Duration.ofMillis(300),
                    new FixedPollInterval(Duration.ofMillis(50)),
                    new KeyValue<>(ConsumerPollsTest.HELLO, 1L),
                    new KeyValue<>(ConsumerPollsTest.KAFKA, 2L)
                ).matches(consumer),
                "The matcher should timeout when the expected records are never polled."
            );
        }
    }

    @Test
    void describesMismatchWhenPolledRecordsDoNotMatch() {
        final MockConsumer<String, Long> consumer = ConsumerPollsTest.consumer();
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 0, ConsumerPollsTest.HELLO, 1L
            )
        );
        consumer.addRecord(
            new ConsumerRecord<>(
                ConsumerPollsTest.TOPIC, 0, 1, "other", 99L
            )
        );
        final StringDescription description = new StringDescription();
        new ConsumerPolls<>(
            Duration.ofSeconds(2),
            new FixedPollInterval(Duration.ofMillis(50)),
            new KeyValue<>(ConsumerPollsTest.HELLO, 1L),
            new KeyValue<>(ConsumerPollsTest.KAFKA, 2L)
        ).describeMismatch(consumer, description);
        MatcherAssert.assertThat(
            "The mismatch description should explain why the matcher failed",
            description.toString(),
            Matchers.containsString("was")
        );
    }

    @Test
    void describesExpectedRecords() {
        final StringDescription description = new StringDescription();
        new ConsumerPolls<>(new KeyValue<>(ConsumerPollsTest.HELLO, 1L)).describeTo(description);
        MatcherAssert.assertThat(
            "The description should mention the expected records",
            description.toString(),
            Matchers.containsString(ConsumerPollsTest.HELLO)
        );
    }

    /**
     * Builds a {@link MockConsumer} assigned to a single partition, ready to have records added.
     * @return A ready-to-use mock consumer
     */
    private static MockConsumer<String, Long> consumer() {
        final MockConsumer<String, Long> consumer = new MockConsumer<>("earliest");
        final TopicPartition partition = new TopicPartition(ConsumerPollsTest.TOPIC, 0);
        consumer.assign(Collections.singletonList(partition));
        final Map<TopicPartition, Long> beginning = new HashMap<>();
        beginning.put(partition, 0L);
        consumer.updateBeginningOffsets(beginning);
        return consumer;
    }
}
