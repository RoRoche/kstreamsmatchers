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
package com.github.roroche.kstreamsmatchers.topics;

import com.github.roroche.kstreamsmatchers.matchers.HasRecord;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.common.TopicPartition;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.pollinterval.FixedPollInterval;
import org.cactoos.Scalar;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link PolledRecords}.
 * @since 0.0.2
 */
@SuppressWarnings({"allpublic", "allfinal", "staticfree", "JTCOP.RuleProhibitStaticFields"})
final class PolledRecordsTest {

    /**
     * Constant to define test topic name.
     */
    private static final String TOPIC = "test-topic";

    @Test
    void containsThePolledRecordsInOrder() {
        final MockConsumer<String, Long> consumer = new PolledRecordsTest.Consumer().value();
        consumer.addRecord(new ConsumerRecord<>(PolledRecordsTest.TOPIC, 0, 0, "hello", 1L));
        consumer.addRecord(new ConsumerRecord<>(PolledRecordsTest.TOPIC, 0, 1, "kafka", 2L));
        MatcherAssert.assertThat(
            "The polled records should be returned in the order they were polled",
            new PolledRecords<>(
                consumer,
                Duration.ofSeconds(2),
                new FixedPollInterval(Duration.ofMillis(50)),
                2
            ),
            new IsIterableContainingInOrder<>(
                new ListOf<>(
                    new HasRecord<>("hello", 1L),
                    new HasRecord<>("kafka", 2L)
                )
            )
        );
    }

    @Test
    void stopsAsSoonAsTheExpectedSizeIsReached() {
        final MockConsumer<String, Long> consumer = new PolledRecordsTest.Consumer().value();
        consumer.addRecord(new ConsumerRecord<>(PolledRecordsTest.TOPIC, 0, 0, "hello", 1L));
        consumer.addRecord(new ConsumerRecord<>(PolledRecordsTest.TOPIC, 0, 1, "kafka", 2L));
        MatcherAssert.assertThat(
            "The list should contain at least the expected number of records",
            new PolledRecords<>(
                consumer,
                Duration.ofSeconds(2),
                new FixedPollInterval(Duration.ofMillis(50)),
                1
            ),
            new IsCollectionWithSize<>(Matchers.greaterThanOrEqualTo(1))
        );
    }

    @Test
    void isEmptyWhenNoRecordsAreExpected() {
        MatcherAssert.assertThat(
            "When no records are expected, the list should be empty",
            new PolledRecords<>(
                new PolledRecordsTest.Consumer().value(),
                Duration.ofSeconds(1),
                new FixedPollInterval(Duration.ofMillis(50)),
                0
            ),
            new IsEmptyCollection<>()
        );
    }

    @Test
    void throwsWhenExpectedSizeIsNeverReachedBeforeTimeout() {
        final MockConsumer<String, Long> consumer = new PolledRecordsTest.Consumer().value();
        consumer.addRecord(new ConsumerRecord<>(PolledRecordsTest.TOPIC, 0, 0, "hello", 1L));
        Assertions.assertThrows(
            ConditionTimeoutException.class,
            () -> new PolledRecords<>(
                consumer,
                Duration.ofMillis(300),
                new FixedPollInterval(Duration.ofMillis(50)),
                2
            ),
            "The matcher should timeout when the expected number of records is never reached."
        );
    }

    /**
     * Builds a {@link MockConsumer} assigned to a single partition, ready to have records added.
     * @since 0.0.2
     */
    private static final class Consumer implements Scalar<MockConsumer<String, Long>> {

        @Override
        public MockConsumer<String, Long> value() {
            final MockConsumer<String, Long> consumer = new MockConsumer<>("earliest");
            final TopicPartition partition = new TopicPartition(PolledRecordsTest.TOPIC, 0);
            consumer.assign(Collections.singletonList(partition));
            final Map<TopicPartition, Long> beginning = new HashMap<>();
            beginning.put(partition, 0L);
            consumer.updateBeginningOffsets(beginning);
            return consumer;
        }
    }
}
