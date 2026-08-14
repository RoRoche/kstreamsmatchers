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

import com.github.roroche.kstreamsmatchers.configuration.PassThroughConfiguration;
import com.github.roroche.kstreamsmatchers.extensions.TopologyTest;
import com.github.roroche.kstreamsmatchers.extensions.WithTopologyTestDriver;
import com.github.roroche.kstreamsmatchers.matchers.OutputTopicContains;
import com.github.roroche.kstreamsmatchers.topology.PassThroughTopology;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.cactoos.Scalar;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsSame;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link PipedOutputTopic}.
 * @since 0.0.3
 */
@SuppressWarnings({"allpublic", "allfinal", "staticfree", "JTCOP.RuleProhibitStaticFields"})
@TopologyTest(
    configuration = PassThroughConfiguration.class,
    topology = PassThroughTopology.class
)
final class PipedOutputTopicTest {

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
    void pipesInputAndReturnsOutputTopic(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        MatcherAssert.assertThat(
            "The returned output topic should contain the piped record",
            new PipedOutputTopic<>(
                new PipedOutputTopicTest.Input(driver).value(),
                new PipedOutputTopicTest.Output(driver).value()
            ).apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1),
            new OutputTopicContains<>(
                new KeyValue<>(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1)
            )
        );
    }

    @Test
    void returnsTheSameOutputTopicInstanceItWasConstructedWith(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        final TestOutputTopic<String, String> output = new PipedOutputTopicTest.Output(
            driver
        ).value();
        MatcherAssert.assertThat(
            "The output topic returned should be the same instance provided to the constructor",
            new PipedOutputTopic<>(
                new PipedOutputTopicTest.Input(driver).value(),
                output
            ).apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1),
            new IsSame<>(output)
        );
    }

    @Test
    void pipesSuccessiveCallsInOrder(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        final PipedOutputTopic<String, String, String, String> piped = new PipedOutputTopic<>(
            new PipedOutputTopicTest.Input(driver).value(),
            new PipedOutputTopicTest.Output(driver).value()
        );
        piped.apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1);
        MatcherAssert.assertThat(
            "Successive calls should pipe additional records that accumulate on the output topic",
            piped.apply(
                PipedOutputTopicTest.KEY_2,
                PipedOutputTopicTest.VALUE_2
            ),
            new OutputTopicContains<>(
                new KeyValue<>(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1),
                new KeyValue<>(PipedOutputTopicTest.KEY_2, PipedOutputTopicTest.VALUE_2)
            )
        );
    }

    @Test
    void wrapsScalarsForInputAndOutputTopics(
        @WithTopologyTestDriver final TopologyTestDriver driver
    ) {
        MatcherAssert.assertThat(
            "When constructed from scalars, it should still pipe input correctly",
            new PipedOutputTopic<>(
                new Input(driver),
                new Output(driver)
            ).apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1),
            new OutputTopicContains<>(
                new KeyValue<>(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1)
            )
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
