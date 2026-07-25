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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link PipedOutputTopic}.
 * @since 0.0.1
 */
@SuppressWarnings({"allpublic", "allfinal", "staticfree", "JTCOP.RuleProhibitStaticFields"})
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
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "piped-output-topic-test");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.driver = new TopologyTestDriver(builder.build(), properties);
    }

    @AfterEach
    void tearDown() {
        this.driver.close();
    }

    @Test
    void pipesInputAndReturnsOutputTopic() {
        MatcherAssert.assertThat(
            "The returned output topic should contain the piped record",
            new PipedOutputTopic<>(
                this.input(),
                this.output()
            ).apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1).readKeyValuesToList(),
            Matchers.contains(
                new KeyValue<>(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1)
            )
        );
    }

    @Test
    void returnsTheSameOutputTopicInstanceItWasConstructedWith() {
        final TestOutputTopic<String, String> output = this.output();
        MatcherAssert.assertThat(
            "The output topic returned should be the same instance provided to the constructor",
            new PipedOutputTopic<>(
                this.input(),
                output
            ).apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1),
            Matchers.sameInstance(output)
        );
    }

    @Test
    void pipesSuccessiveCallsInOrder() {
        final PipedOutputTopic<String, String, String, String> piped = new PipedOutputTopic<>(
            this.input(),
            this.output()
        );
        piped.apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1);
        MatcherAssert.assertThat(
            "Successive calls should pipe additional records that accumulate on the output topic",
            piped.apply(
                PipedOutputTopicTest.KEY_2,
                PipedOutputTopicTest.VALUE_2
            ).readKeyValuesToList(),
            Matchers.contains(
                new KeyValue<>(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1),
                new KeyValue<>(PipedOutputTopicTest.KEY_2, PipedOutputTopicTest.VALUE_2)
            )
        );
    }

    @Test
    void wrapsScalarsForInputAndOutputTopics() {
        MatcherAssert.assertThat(
            "When constructed from scalars, it should still pipe input correctly",
            new PipedOutputTopic<>(
                this::input,
                this::output
            ).apply(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1).readKeyValuesToList(),
            Matchers.contains(
                new KeyValue<>(PipedOutputTopicTest.KEY_1, PipedOutputTopicTest.VALUE_1)
            )
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
