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
import com.github.roroche.kstreamsmatchers.configuration.WordCountConfiguration;
import com.github.roroche.kstreamsmatchers.matchers.OutputTopicContains;
import com.github.roroche.kstreamsmatchers.topics.PipedOutputTopic;
import com.github.roroche.kstreamsmatchers.topics.WordCountInputTopic;
import com.github.roroche.kstreamsmatchers.topics.WordCountOutputTopic;
import com.github.roroche.kstreamsmatchers.topology.WordCountTopology;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.nio.file.Path;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test class for the word count topology.
 *
 * @since 0.0.1
 */
@SuppressWarnings({"allpublic", "allfinal", "JTCOP.RuleEveryTestHasProductionClass"})
@ExtendWith(MktmpResolver.class)
final class WordCountTopologyTest {

    /**
     * The topology test driver used to run the tests.
     */
    private TopologyTestDriver driver;

    @BeforeEach
    void setUp(@Mktmp final Path tmp) {
        this.driver = new TopologyTestDriver(
            new WordCountTopology().value(),
            new OverlayConfiguration(
                new WordCountConfiguration(),
                new MapEntry<>(StreamsConfig.STATE_DIR_CONFIG, tmp.toString())
            ).properties()
        );
    }

    @SuppressWarnings("nullfree")
    @AfterEach
    void tearDown() {
        if (this.driver != null) {
            this.driver.close();
        }
    }

    @Test
    void isOk() {
        MatcherAssert.assertThat(
            "When a word count topology is applied to an input topic, the output topic contains the correct word counts",
            new PipedOutputTopic<>(
                new WordCountInputTopic(this.driver),
                new WordCountOutputTopic(this.driver)
            ).apply("key1", "Hello Kafka Kafka Streams"),
            new OutputTopicContains<>(
                new KeyValue<>("hello", 1L),
                new KeyValue<>("kafka", 1L),
                new KeyValue<>("kafka", 2L),
                new KeyValue<>("streams", 1L)
            )
        );
    }
}
