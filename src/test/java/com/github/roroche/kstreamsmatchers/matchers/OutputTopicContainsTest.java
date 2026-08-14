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

import com.github.roroche.kstreamsmatchers.configuration.PassThroughConfiguration;
import com.github.roroche.kstreamsmatchers.extensions.TopologyTest;
import com.github.roroche.kstreamsmatchers.extensions.WithTopologyTestDriver;
import com.github.roroche.kstreamsmatchers.topology.PassThroughTopology;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.cactoos.Scalar;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link OutputTopicContains}.
 * @since 0.0.1
 */
@SuppressWarnings({
    "allpublic",
    "allfinal",
    "staticfree",
    "JTCOP.RuleProhibitStaticFields"
})
@TopologyTest(
    configuration = PassThroughConfiguration.class,
    topology = PassThroughTopology.class
)
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

    @Test
    void matchesWhenTopicContainsExpectedRecordsInOrder(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        new OutputTopicContainsTest.Input(
            driver
        ).value().pipeInput(
            OutputTopicContainsTest.KEY_1,
            OutputTopicContainsTest.VALUE_1
        );
        new OutputTopicContainsTest.Input(
            driver
        ).value().pipeInput(
            OutputTopicContainsTest.KEY_2,
            OutputTopicContainsTest.VALUE_2
        );
        MatcherAssert.assertThat(
            "When the output topic contains the expected records in order, the matcher should match",
            new OutputTopicContainsTest.Output(driver).value(),
            new OutputTopicContains<>(
                new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1),
                new KeyValue<>(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2)
            )
        );
    }

    @Test
    void matchesFromListOfKeyValues(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        new OutputTopicContainsTest.Input(
            driver
        ).value().pipeInput(
            OutputTopicContainsTest.KEY_1,
            OutputTopicContainsTest.VALUE_1
        );
        MatcherAssert.assertThat(
            "When constructed from a List of KeyValue, the matcher should match the output topic",
            new OutputTopicContainsTest.Output(driver).value(),
            new OutputTopicContains<>(
                new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1)
            )
        );
    }

    @Test
    void doesNotMatchWhenOrderDiffers(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        new OutputTopicContainsTest.Input(
            driver
        ).value().pipeInput(
            OutputTopicContainsTest.KEY_1,
            OutputTopicContainsTest.VALUE_1
        );
        new OutputTopicContainsTest.Input(
            driver
        ).value().pipeInput(
            OutputTopicContainsTest.KEY_2,
            OutputTopicContainsTest.VALUE_2
        );
        MatcherAssert.assertThat(
            "When the records are out of the expected order, the matcher should not match",
            new OutputTopicContains<>(
                new KeyValue<>(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2),
                new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1)
            ).matches(new OutputTopicContainsTest.Output(driver).value()),
            new IsEqual<>(false)
        );
    }

    @Test
    void doesNotMatchWhenARecordIsMissing(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        new OutputTopicContainsTest.Input(
            driver
        ).value().pipeInput(
            OutputTopicContainsTest.KEY_1,
            OutputTopicContainsTest.VALUE_1
        );
        MatcherAssert.assertThat(
            "When an expected record is missing, the matcher should not match",
            new OutputTopicContains<>(
                new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1),
                new KeyValue<>(OutputTopicContainsTest.KEY_2, OutputTopicContainsTest.VALUE_2)
            ).matches(new OutputTopicContainsTest.Output(driver).value()),
            new IsEqual<>(false)
        );
    }

    @Test
    void doesNotMatchWhenTopicIsEmpty(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        MatcherAssert.assertThat(
            "When the output topic is empty, the matcher should not match",
            new OutputTopicContains<>(
                new KeyValue<>(
                    OutputTopicContainsTest.KEY_1,
                    OutputTopicContainsTest.VALUE_1
                )
            ).matches(new OutputTopicContainsTest.Output(driver).value()),
            new IsEqual<>(false)
        );
    }

    @Test
    void describesMismatchWhenTopicDoesNotContainExpectedRecords(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        final StringDescription description = new StringDescription();
        new OutputTopicContains<>(
            new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1)
        ).describeMismatch(new OutputTopicContainsTest.Output(driver).value(), description);
        MatcherAssert.assertThat(
            "The mismatch description should explain why the topic did not match",
            description.toString(),
            new StringContains("was")
        );
    }

    @Test
    void describesMismatchWithDetailsFromTheDelegateMatcher(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        new OutputTopicContainsTest.Input(
            driver
        ).value().pipeInput(
            OutputTopicContainsTest.KEY_2,
            OutputTopicContainsTest.VALUE_2
        );
        final StringDescription description = new StringDescription();
        new OutputTopicContains<>(
            new KeyValue<>(OutputTopicContainsTest.KEY_1, OutputTopicContainsTest.VALUE_1)
        ).describeMismatch(new OutputTopicContainsTest.Output(driver).value(), description);
        MatcherAssert.assertThat(
            "Matcher contributes actual record details, not just the leading 'was' text",
            description.toString(),
            new IsNot<>(new IsEqual<>("was "))
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
            new StringContains(OutputTopicContainsTest.KEY_1)
        );
    }

    /**
     * Creates the input topic used by the tests.
     *
     * @param driver The topology test driver used to create the input topic
     */
    private record Input(TopologyTestDriver driver)
        implements Scalar<TestInputTopic<String, String>> {

        @Override
        public TestInputTopic<String, String> value() {
            return this.driver.createInputTopic(
                "input-topic",
                Serdes.String().serializer(),
                Serdes.String().serializer()
            );
        }
    }

    /**
     * Creates the output topic used by the tests.
     *
     * @param driver The topology test driver used to create the output topic
     */
    private record Output(TopologyTestDriver driver)
        implements Scalar<TestOutputTopic<String, String>> {

        @Override
        public TestOutputTopic<String, String> value() {
            return this.driver.createOutputTopic(
                "output-topic",
                Serdes.String().deserializer(),
                Serdes.String().deserializer()
            );
        }
    }
}
