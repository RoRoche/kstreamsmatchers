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

import java.util.Arrays;
import java.util.Locale;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.cactoos.Scalar;

/**
 * A simple WordCount topology for testing purposes.
 *
 * @since 0.0.1
 */
public final class WordCountTopology implements Scalar<Topology> {
    @Override
    public Topology value() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> source = builder.stream(
            "input-topic",
            Consumed.with(Serdes.String(), Serdes.String())
        );
        final KTable<String, Long> counts = source
            .flatMapValues(
                (final String value) ->
                    Arrays.stream(
                        value.toLowerCase(Locale.ROOT).split("\\W+")
                    ).filter(
                        (final String word) -> !word.isEmpty()
                    ).toList()
            ).groupBy(
                (final String key, final String word) -> word,
                Grouped.with(Serdes.String(), Serdes.String())
            ).count(
                Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
            );
        counts.toStream().to("output-topic", Produced.with(Serdes.String(), Serdes.Long()));
        return builder.build();
    }
}
