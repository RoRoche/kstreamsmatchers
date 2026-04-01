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

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.cactoos.Scalar;

/**
 * A {@link Scalar} that creates a {@link TestInputTopic} for the word count topology.
 *
 * @since 0.0.1
 */
public final class WordCountInputTopic implements Scalar<TestInputTopic<String, String>> {
    /**
     * The topology test driver used to create the input topic.
     */
    private final TopologyTestDriver driver;

    /**
     * Primary ctor.
     *
     * @param driver The topology test driver used to create the input topic
     */
    public WordCountInputTopic(final TopologyTestDriver driver) {
        this.driver = driver;
    }

    @Override
    public TestInputTopic<String, String> value() {
        return this.driver.createInputTopic(
            "input-topic",
            Serdes.String().serializer(),
            Serdes.String().serializer()
        );
    }
}
