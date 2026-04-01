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
package com.github.roroche.kstreamsmatchers;

import com.github.roroche.eoconfig.OverlayConfiguration;
import com.github.roroche.kstreamsmatchers.configuration.ItConsumerConfiguration;
import com.github.roroche.kstreamsmatchers.configuration.ItProducerConfiguration;
import com.github.roroche.kstreamsmatchers.configuration.ItStreamsConfiguration;
import com.github.roroche.kstreamsmatchers.matchers.ConsumerPolls;
import com.github.roroche.kstreamsmatchers.topology.WordCountTopology;
import com.yegor256.MayBeSlow;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration test class for the word count topology.
 *
 * @since 0.0.1
 */
@SuppressWarnings({"allpublic", "allfinal", "JTCOP.RuleEveryTestHasProductionClass"})
@ExtendWith(MktmpResolver.class)
final class WordCountIntegrationTest {

    /**
     * The Kafka container used to run the tests.
     */
    private ConfluentKafkaContainer kafka;

    @BeforeEach
    void setUp() {
        this.kafka = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
        );
        this.kafka.start();
    }

    @SuppressWarnings("nullfree")
    @AfterEach
    void tearDown() {
        if (this.kafka != null) {
            this.kafka.stop();
        }
    }

    @Test
    @ExtendWith(MayBeSlow.class)
    void isProducingFinalWordCounts(@Mktmp final Path tmp) throws Exception {
        try (KafkaStreams streams = new KafkaStreams(
            new WordCountTopology().value(),
            new OverlayConfiguration(
                new ItStreamsConfiguration(),
                new MapEntry<>(
                    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                    this.kafka.getBootstrapServers()
                ),
                new MapEntry<>(
                    StreamsConfig.STATE_DIR_CONFIG,
                    tmp.toString()
                )
            ).properties()
        )) {
            streams.start();
            try (Producer<String, String> producer = new KafkaProducer<>(
                new OverlayConfiguration(
                    new ItProducerConfiguration(),
                    new MapEntry<>(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        this.kafka.getBootstrapServers()
                    )
                ).properties()
            )) {
                producer.send(
                    new ProducerRecord<>(
                        "input-topic",
                        "key1",
                        "Hello Kafka Kafka Streams"
                    )
                ).get();
            }
            try (Consumer<String, Long> consumer = new KafkaConsumer<>(
                new OverlayConfiguration(
                    new ItConsumerConfiguration(),
                    new MapEntry<>(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        this.kafka.getBootstrapServers()
                    )
                ).properties()
            )) {
                consumer.subscribe(Collections.singletonList("output-topic"));
                MatcherAssert.assertThat(
                    "Output topic should contain the expected word counts in order",
                    consumer,
                    new ConsumerPolls<>(
                        new MapEntry<>("hello", 1L),
                        new MapEntry<>("kafka", 2L),
                        new MapEntry<>("streams", 1L)
                    )
                );
            } finally {
                streams.close(Duration.ofSeconds(5));
            }
        }
    }
}
