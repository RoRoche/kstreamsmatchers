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
package com.github.roroche.kstreamsmatchers.matchers;

import com.github.roroche.kstreamsmatchers.KafkaRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link HasValue}.
 * @since 0.0.1
 */
@SuppressWarnings({"allpublic", "allfinal", "staticfree", "JTCOP.RuleProhibitStaticFields"})
final class HasValueTest {

    /**
     * Constant to define key 1.
     */
    private static final String KEY_1 = "key-1";

    /**
     * Constant to define value 1.
     */
    private static final String VALUE_1 = "value-1";

    @Test
    void matchesWhenValueIsEqual() {
        MatcherAssert.assertThat(
            "When the actual value equals the expected value, the matcher should match",
            new KafkaRecord<>(new RecordHeaders(), HasValueTest.KEY_1, HasValueTest.VALUE_1),
            new HasValue<>(HasValueTest.VALUE_1)
        );
    }

    @Test
    void doesNotMatchWhenValueIsDifferent() {
        MatcherAssert.assertThat(
            "When the actual value differs from the expected value, the matcher should not match",
            new HasValue<>(HasValueTest.VALUE_1).matches(
                new KafkaRecord<>(new RecordHeaders(), HasValueTest.KEY_1, "value-2")
            ),
            Matchers.is(false)
        );
    }

    @Test
    void matchesUsingDelegateMatcher() {
        MatcherAssert.assertThat(
            "When constructed with a delegate matcher, it should be used to match the value",
            new KafkaRecord<>(new RecordHeaders(), HasValueTest.KEY_1, "value-123"),
            new HasValue<>(Matchers.startsWith("value-"))
        );
    }

    @Test
    void doesNotMatchWhenDelegateMatcherFails() {
        MatcherAssert.assertThat(
            "When the delegate matcher does not match, the matcher should not match",
            new HasValue<>(Matchers.startsWith("data-")).matches(
                new KafkaRecord<>(new RecordHeaders(), HasValueTest.KEY_1, "value-123")
            ),
            Matchers.is(false)
        );
    }

    @Test
    void describesExpectedValue() {
        final StringDescription description = new StringDescription();
        new HasValue<>(HasValueTest.VALUE_1).describeTo(description);
        MatcherAssert.assertThat(
            "The description should mention the expected value",
            description.toString(),
            Matchers.containsString(HasValueTest.VALUE_1)
        );
    }
}
