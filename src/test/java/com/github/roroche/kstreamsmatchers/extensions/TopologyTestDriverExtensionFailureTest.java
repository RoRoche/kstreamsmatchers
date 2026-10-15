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

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

/**
 * Tests failures caused by missing topology test driver configuration or state.
 *
 * @since 0.0.4
 */
@SuppressWarnings({"allpublic", "allfinal", "JTCOP.RuleEveryTestHasProductionClass"})
@ExtendWith(ContextParameterExtension.class)
final class TopologyTestDriverExtensionFailureTest {

    @Test
    void rejectsMissingAnnotation(final ExtensionContext context) {
        final TopologyTestDriverExtension extension = new TopologyTestDriverExtension();
        MatcherAssert.assertThat(
            "A test class without @TopologyTest must be rejected with an explanation",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> extension.beforeEach(context)
            ).getMessage(),
            new IsEqual<>("Test class must be annotated with @TopologyTest")
        );
    }

    @Test
    void rejectsUninitializedDriver(
        final ParameterContext parameter,
        final ExtensionContext context
    ) {
        final TopologyTestDriverExtension extension = new TopologyTestDriverExtension();
        MatcherAssert.assertThat(
            "Resolving a driver before initialization must explain the missing state",
            Assertions.assertThrows(
                ParameterResolutionException.class,
                () -> extension.resolveParameter(parameter, context)
            ).getMessage(),
            new IsEqual<>("TopologyTestDriver has not been initialized")
        );
    }

    @Test
    void toleratesCleanupWithoutInitialization(final ExtensionContext context) {
        Assertions.assertDoesNotThrow(
            () -> new TopologyTestDriverExtension().afterEach(context),
            "Cleanup must tolerate a driver that was never initialized"
        );
    }
}
