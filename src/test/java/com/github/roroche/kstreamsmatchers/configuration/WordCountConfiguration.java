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
package com.github.roroche.kstreamsmatchers.configuration;

import com.github.roroche.eoconfig.ConfigurationEnvelope;
import com.github.roroche.eoconfig.MapConfiguration;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.cactoos.map.MapEntry;

/**
 * A configuration for the WordCount topology in tests.
 *
 * @since 0.0.1
 */
public final class WordCountConfiguration extends ConfigurationEnvelope {
    /**
     * Constructs a WordCountConfiguration with the given state directory.
     */
    public WordCountConfiguration() {
        super(
            new MapConfiguration(
                new MapEntry<>(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-test"),
                new MapEntry<>(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234"),
                new MapEntry<>(
                    StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                    Serdes.String().getClass().getName()
                ),
                new MapEntry<>(
                    StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                    Serdes.String().getClass().getName()
                )
            )
        );
    }
}
