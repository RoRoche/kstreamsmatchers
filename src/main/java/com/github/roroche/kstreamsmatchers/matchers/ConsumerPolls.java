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
import com.github.roroche.kstreamsmatchers.topics.PolledRecords;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.awaitility.core.DurationFactory;
import org.awaitility.pollinterval.FixedPollInterval;
import org.awaitility.pollinterval.PollInterval;
import org.cactoos.list.ListOf;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.collection.IsIterableContainingInOrder;

/**
 * Matcher that checks if a consumer polls the expected records from the given topics.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Assert that a Kafka consumer receives expected word counts
 * try (Consumer<String, Long> consumer = new KafkaConsumer<>(config)) {
 *     MatcherAssert.assertThat(
 *         consumer,
 *         new ConsumerPolls<>(
 *             new MapEntry<>("hello", 1L),
 *             new MapEntry<>("kafka", 2L),
 *             new MapEntry<>("streams", 1L)
 *         )
 *     );
 * }
 * }</pre>
 *
 * @since 0.0.1
 */
@SuppressWarnings("allfinal")
public final class ConsumerPolls<K, V> extends TypeSafeDiagnosingMatcher<Consumer<K, V>> {

    /**
     * The expected records to be polled.
     */
    private final Matcher<Iterable<? extends KafkaRecord<K, V>>> expected;

    /**
     * The maximum duration to wait for the expected records to be polled.
     */
    private final Duration timeout;

    /**
     * The interval between polls.
     */
    private final PollInterval interval;

    /**
     * The expected number of records to be polled.
     */
    private final int size;

    /**
     * Primary ctor.
     *
     * @param expected The expected records to be polled
     * @param timeout The maximum duration to wait for the expected records to be polled
     * @param interval The interval between polls
     * @param size The expected number of records to be polled
     */
    /*
     * @checkstyle ParameterNumberCheck (25 lines)
     */
    public ConsumerPolls(
        final Matcher<Iterable<? extends KafkaRecord<K, V>>> expected,
        final Duration timeout,
        final PollInterval interval,
        final int size
    ) {
        this.expected = expected;
        this.timeout = timeout;
        this.interval = interval;
        this.size = size;
    }

    /**
     * Secondary ctor, for convenience.
     *
     * @param timeout The maximum duration to wait for the expected records to be polled
     * @param interval The interval between polls
     * @param expected The expected records to be polled
     */
    public ConsumerPolls(
        final Duration timeout,
        final PollInterval interval,
        final List<Matcher<KafkaRecord<K, V>>> expected
    ) {
        this(
            IsIterableContainingInOrder.contains(
                expected.toArray(Matcher[]::new)
            ),
            timeout,
            interval,
            expected.size()
        );
    }

    /**
     * Secondary ctor, for convenience.
     *
     * @param timeout The maximum duration to wait for the expected records to be polled
     * @param interval The interval between polls
     * @param expected The expected records to be polled
     */
    @SafeVarargs
    public ConsumerPolls(
        final Duration timeout,
        final PollInterval interval,
        final Map.Entry<K, V>... expected
    ) {
        this(
            timeout,
            interval,
            new ListOf<>(Arrays.stream(expected).map(HasRecord::new).toList())
        );
    }

    /**
     * Secondary ctor, for convenience.
     *
     * @param expected The expected records to be polled
     */
    @SafeVarargs
    public ConsumerPolls(final Map.Entry<K, V>... expected) {
        this(
            DurationFactory.of(1, TimeUnit.MINUTES),
            new FixedPollInterval(DurationFactory.of(200, TimeUnit.MILLISECONDS)),
            expected
        );
    }

    @Override
    public boolean matchesSafely(final Consumer<K, V> consumer, final Description description) {
        final List<KafkaRecord<K, V>> records =
            new PolledRecords<>(
                consumer,
                this.timeout,
                this.interval,
                this.size
            );
        boolean matches = true;
        if (!this.expected.matches(records)) {
            description.appendText("was ");
            this.expected.describeMismatch(records, description);
            matches = false;
        }
        return matches;
    }

    @Override
    public void describeTo(final Description description) {
        this.expected.describeTo(description);
    }

}
