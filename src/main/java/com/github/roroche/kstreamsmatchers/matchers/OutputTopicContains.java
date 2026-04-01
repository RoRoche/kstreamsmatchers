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

import java.util.List;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.test.TestRecord;
import org.cactoos.list.ListOf;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.collection.IsIterableContainingInOrder;

/**
 * A Hamcrest matcher that checks if a {@link TestOutputTopic} contains specific records in order.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Assert that an output topic contains expected word counts
 * TopologyTestDriver driver = new TopologyTestDriver(topology, config);
 * TestOutputTopic<String, Long> outputTopic = driver.createOutputTopic(...);
 *
 * MatcherAssert.assertThat(
 *     outputTopic,
 *     new OutputTopicContains<>(
 *         new KeyValue<>("hello", 1L),
 *         new KeyValue<>("kafka", 2L),
 *         new KeyValue<>("streams", 1L)
 *     )
 * );
 * }</pre>
 * @since 0.0.1
 */
public final class OutputTopicContains<K, V>
    extends TypeSafeDiagnosingMatcher<TestOutputTopic<K, V>> {

    /**
     * The matcher for the records in the output topic.
     */
    private final Matcher<? super Iterable<TestRecord<K, V>>> expected;

    /**
     * Constructs an OutputTopicContains matcher with the given expected records matcher.
     *
     * @param expected The matcher for the records in the output topic
     */
    public OutputTopicContains(
        final Matcher<? super Iterable<TestRecord<K, V>>> expected
    ) {
        this.expected = expected;
    }

    /**
     * Constructs an OutputTopicContains matcher with the given expected records.
     *
     * @param expected The expected records in the output topic
     */
    @SuppressWarnings("unchecked")
    public OutputTopicContains(final List<KeyValue<K, V>> expected) {
        this(
            IsIterableContainingInOrder.contains(
                expected.stream()
                    .map(
                        (final KeyValue<K, V> kv) ->
                            new HasRecord.FromTestRecord<>(new HasRecord<>(kv))
                    ).toArray(Matcher[]::new)
            )
        );
    }

    /**
     * Constructs an OutputTopicContains matcher with the given expected records.
     *
     * @param expected The expected records in the output topic
     */
    @SafeVarargs
    public OutputTopicContains(final KeyValue<K, V>... expected) {
        this(new ListOf<>(expected));
    }

    @SuppressWarnings("allfinal")
    @Override
    public boolean matchesSafely(
        final TestOutputTopic<K, V> topic,
        final Description description
    ) {
        final List<TestRecord<K, V>> actual = topic.readRecordsToList();
        boolean matches = true;
        if (!this.expected.matches(actual)) {
            description.appendText("was ");
            this.expected.describeMismatch(actual, description);
            matches = false;
        }
        return matches;
    }

    @Override
    public void describeTo(final Description description) {
        this.expected.describeTo(description);
    }
}
