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

import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link OutputTopicContains}.
 * @since 0.0.1
 */
@SuppressWarnings({
    "allpublic",
    "allfinal",
    "staticfree",
    "PMD.TooManyMethods",
    "JTCOP.RuleProhibitStaticFields"
})
final class OutputTopicContainsTest {

    /**
     * Constant to define key 1.
     */
    private static final String KEY_1 = "key-1";

    /**
     * Constant to define value 1.
     */
    private static final String VALUE_1 = "value-1";

    /**
     * Constant to define key 2.
     */
    private static final String KEY_2 = "key-2";

    /**
     * Constant to define value 2.
     */
    private static final String VALUE_2 = "value-2";

    /**
     * The topology test driver, wired with a pass-through topology, used for each test.
     * @checkstyle ProhibitFieldsInTestClassesCheck (4 lines)
     */
    private TopologyTestDriver driver;

    @BeforeEach
    void setUp() {
        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream("input-topic", Consumed.with(Serdes.String(), Serdes.String()))
            .to("output-topic", Produced.with(Serdes.String(), Serdes.String()));
        final Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "output-topic-contains-test");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.driver = new TopologyTestDriver(builder.build(), properties);
    }

    @AfterEach
    void tearDown() {
        this.driver.close();
    }

    @Test
    void matchesWhenTopicContainsExpectedRecordsInOrder() {
        this.input().pipeInput(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1);
        this.input().pipeInput(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2);
        MatcherAssert.assertThat(
            "When the output topic contains the expected records in order, the matcher should match",
            this.output(),
            new OutputTopicContains<>(
                new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1),
                new KeyValue<>(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2)
            )
        );
    }

    @Test
    void matchesFromListOfKeyValues() {
        this.input().pipeInput(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1);
        MatcherAssert.assertThat(
            "When constructed from a List of KeyValue, the matcher should match the output topic",
            this.output(),
            new OutputTopicContains<>(
                new ListOf<>(
                    new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1)
                )
            )
        );
    }

    @Test
    void doesNotMatchWhenOrderDiffers() {
        this.input().pipeInput(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1);
        this.input().pipeInput(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2);
        MatcherAssert.assertThat(
            "When the records are out of the expected order, the matcher should not match",
            new OutputTopicContains<>(
                new KeyValue<>(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2),
                new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1)
            ).matches(this.output()),
            Matchers.is(false)
        );
    }

    @Test
    void doesNotMatchWhenARecordIsMissing() {
        this.input().pipeInput(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1);
        MatcherAssert.assertThat(
            "When an expected record is missing, the matcher should not match",
            new OutputTopicContains<>(
                new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1),
                new KeyValue<>(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2)
            ).matches(this.output()),
            Matchers.is(false)
        );
    }

    @Test
    void doesNotMatchWhenTopicIsEmpty() {
        MatcherAssert.assertThat(
            "When the output topic is empty, the matcher should not match",
            new OutputTopicContains<>(
                new KeyValue<>(
                    OutputTopicContainsTest.KEY_1,
                    OutputTopicContainsTest.VALUE_1
                )
            ).matches(this.output()),
            Matchers.is(false)
        );
    }

    @Test
    void describesMismatchWhenTopicDoesNotContainExpectedRecords() {
        final StringDescription description = new StringDescription();
        new OutputTopicContains<>(
            new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1)
        ).describeMismatch(this.output(), description);
        MatcherAssert.assertThat(
            "The mismatch description should explain why the topic did not match",
            description.toString(),
            Matchers.containsString("was")
        );
    }

    @Test
    void describesExpectedRecords() {
        final StringDescription description = new StringDescription();
        new OutputTopicContains<>(
            new KeyValue<>(
                OutputTopicContainsTest.KEY_1,
                OutputTopicContainsTest.VALUE_1
            )
        ).describeTo(description);
        MatcherAssert.assertThat(
            "The description should mention the expected records",
            description.toString(),
            Matchers.containsString(OutputTopicContainsTest.KEY_1)
        );
    }

    /**
     * Creates the input topic used by the tests.
     * @return The input topic
     */
    private TestInputTopic<String, String> input() {
        return this.driver.createInputTopic(
            "input-topic",
            Serdes.String().serializer(),
            Serdes.String().serializer()
        );
    }

    /**
     * Creates the output topic used by the tests.
     * @return The output topic
     */
    private TestOutputTopic<String, String> output() {
        return this.driver.createOutputTopic(
            "output-topic",
            Serdes.String().deserializer(),
            Serdes.String().deserializer()
        );
    }
}
