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

import com.github.roroche.kstreamsmatchers.KafkaRecord;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;
import org.awaitility.pollinterval.PollInterval;
import org.cactoos.list.ListEnvelope;
import org.cactoos.scalar.Unchecked;

/**
 * A list of records polled from a consumer,
 * waiting until the expected number of records is polled or the maximum duration is reached.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 * @since 0.0.1
 */
public final class PolledRecords<K, V> extends ListEnvelope<KafkaRecord<K, V>> {
    /**
     * Primary ctor.
     *
     * @param consumer The consumer to poll from
     * @param timeout The maximum duration to wait for the expected records to be polled
     * @param interval The interval between polls
     * @param size The expected number of records to be polled
     */
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    /*
     * @checkstyle ParameterNumberCheck (25 lines)
     */
    public PolledRecords(
        final Consumer<K, V> consumer,
        final Duration timeout,
        final PollInterval interval,
        final int size
    ) {
        super(
            new Unchecked<>(
                () -> {
                    final List<KafkaRecord<K, V>> records = new ArrayList<>(size);
                    Awaitility.await()
                        .atMost(timeout)
                        .pollInterval(interval)
                        .until(
                            () -> {
                                consumer.poll(Duration.ofMillis(500))
                                    .forEach(
                                        (final ConsumerRecord<K, V> crecord) -> records.add(
                                            new KafkaRecord<>(crecord)
                                        )
                                    );
                                return records.size() >= size;
                            }
                        );
                    return records;
                }
            ).value()
        );
    }
}
