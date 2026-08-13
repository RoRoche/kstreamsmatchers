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
package com.github.roroche.kstreamsmatchers.topology;

import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.Record;
import org.cactoos.Scalar;

/**
 * Topology whose processor reports when it is closed.
 * @since 0.0.4
 */
public final class CloseTrackingTopology implements Scalar<Topology> {

    @Override
    public Topology value() {
        final Topology topology = new Topology();
        topology.addSource("source", "input-topic");
        topology.addProcessor(
            "processor",
            () -> new Processor<String, String, String, String>() {
                @Override
                public void process(final Record<String, String> rec) {
                    throw new UnsupportedOperationException("Not used by this test");
                }

                @Override
                public void close() {
                    System.setProperty(
                        "kstreamsmatchers.test.driver.closed",
                        "true"
                    );
                }
            },
            "source"
        );
        return topology;
    }
}
