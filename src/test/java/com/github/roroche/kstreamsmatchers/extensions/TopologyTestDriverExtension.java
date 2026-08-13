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
package com.github.roroche.kstreamsmatchers.extensions;

import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Provides a pass-through topology test driver to test method parameters.
 * @since 0.0.2
 */
@SuppressWarnings("staticfree")
public final class TopologyTestDriverExtension implements BeforeEachCallback,
    AfterEachCallback, ParameterResolver {

    /**
     * Constant for driver parameter.
     */
    private static final String DRIVER = "driver";

    @Override
    public void beforeEach(final ExtensionContext context) {
        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream("input-topic", Consumed.with(Serdes.String(), Serdes.String()))
            .to("output-topic", Produced.with(Serdes.String(), Serdes.String()));
        final Properties properties = new Properties();
        properties.put(
            StreamsConfig.APPLICATION_ID_CONFIG,
            String.format("%s-test", context.getRequiredTestClass().getSimpleName())
        );
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        context.getStore(
            ExtensionContext.Namespace.create(TopologyTestDriverExtension.class)
        ).put(
            TopologyTestDriverExtension.DRIVER, new TopologyTestDriver(builder.build(), properties)
        );
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        context.getStore(
            ExtensionContext.Namespace.create(TopologyTestDriverExtension.class)
        ).remove(TopologyTestDriverExtension.DRIVER, TopologyTestDriver.class).close();
    }

    @Override
    public boolean supportsParameter(
        final ParameterContext parameter,
        final ExtensionContext context
    ) {
        return parameter.isAnnotated(TopologyDriver.class)
            && parameter.getParameter().getType().equals(TopologyTestDriver.class);
    }

    @Override
    public Object resolveParameter(
        final ParameterContext parameter,
        final ExtensionContext context
    ) {
        return context.getStore(
            ExtensionContext.Namespace.create(TopologyTestDriverExtension.class)
        ).get(TopologyTestDriverExtension.DRIVER, TopologyTestDriver.class);
    }
}
