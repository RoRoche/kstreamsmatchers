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

import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.cactoos.BiFunc;
import org.cactoos.Scalar;
import org.cactoos.scalar.Unchecked;

/**
 * A {@link BiFunc} that pipes input to a {@link TestInputTopic}
 * and returns a {@link TestOutputTopic}.
 *
 * @param <X> The type of the key of the input topic
 * @param <Z> The type of the value of the input topic
 * @param <K> The type of the key of the output topic
 * @param <V> The type of the value of the output topic
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Pipe input to a topology and get output topic
 * TopologyTestDriver driver = new TopologyTestDriver(topology, config);
 * TestOutputTopic<String, Long> result = new PipedOutputTopic<>(
 *     new WordCountInputTopic(driver),
 *     new WordCountOutputTopic(driver)
 * ).apply("key1", "Hello Kafka Kafka Streams");
 *
 * MatcherAssert.assertThat(
 *     result,
 *     new OutputTopicContains<>(
 *         new KeyValue<>("hello", 1L),
 *         new KeyValue<>("kafka", 2L),
 *         new KeyValue<>("streams", 1L)
 *     )
 * );
 * }</pre>
 *
 * @since 0.0.1
 */
public final class PipedOutputTopic<X, Z, K, V> implements BiFunc<X, Z, TestOutputTopic<K, V>> {

    /**
     * The input topic to which the key and value will be piped.
     */
    private final TestInputTopic<X, Z> input;

    /**
     * The output topic that will be returned after piping the input.
     */
    private final TestOutputTopic<K, V> output;

    /**
     * Primary ctor.
     *
     * @param input The input topic to which the key and value will be piped
     * @param output The output topic that will be returned after piping the input
     */
    public PipedOutputTopic(final TestInputTopic<X, Z> input, final TestOutputTopic<K, V> output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Ctor that accepts scalars for the input and output topics.
     *
     * @param input The scalar for the input topic to which the key and value will be piped
     * @param output The scalar for the output topic that will be returned after piping the input
     */
    public PipedOutputTopic(
        final Scalar<TestInputTopic<X, Z>> input,
        final Scalar<TestOutputTopic<K, V>> output
    ) {
        this(
            new Unchecked<>(input).value(),
            new Unchecked<>(output).value()
        );
    }

    @Override
    public TestOutputTopic<K, V> apply(final X key, final Z value) {
        this.input.pipeInput(key, value);
        return this.output;
    }
}
