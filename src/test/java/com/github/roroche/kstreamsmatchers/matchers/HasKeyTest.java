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
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link HasKey}.
 * @since 0.0.3
 */
@SuppressWarnings({"allpublic", "allfinal", "staticfree", "JTCOP.RuleProhibitStaticFields"})
final class HasKeyTest {

    /**
     * Constant to define key 1.
     */
    private static final String KEY_1 = "key-1";

    /**
     * Constant to define value 1.
     */
    private static final String VALUE_1 = "value-1";

    /**
     * Constant to define key 2.
     */
    private static final String KEY_2 = "key-2";

    @Test
    void matchesWhenKeyIsEqual() {
        MatcherAssert.assertThat(
            "When the actual key equals the expected key, the matcher should match",
            new KafkaRecord<>(new RecordHeaders(), HasKeyTest.KEY_1, HasKeyTest.VALUE_1),
            new HasKey<>(HasKeyTest.KEY_1)
        );
    }

    @Test
    void doesNotMatchWhenKeyIsDifferent() {
        MatcherAssert.assertThat(
            "When the actual key differs from the expected key, the matcher should not match",
            new HasKey<>(HasKeyTest.KEY_1).matches(
                new KafkaRecord<>(new RecordHeaders(), HasKeyTest.KEY_2, HasKeyTest.VALUE_1)
            ),
            new IsEqual<>(false)
        );
    }

    @Test
    void matchesUsingDelegateMatcher() {
        MatcherAssert.assertThat(
            "When constructed with a delegate matcher, it should be used to match the key",
            new KafkaRecord<>(new RecordHeaders(), HasKeyTest.KEY_1, HasKeyTest.VALUE_1),
            new HasKey<>(new StringStartsWith("key-"))
        );
    }

    @Test
    void doesNotMatchWhenDelegateMatcherFails() {
        MatcherAssert.assertThat(
            "When the delegate matcher does not match, the matcher should not match",
            new HasKey<>(new StringStartsWith("id-")).matches(
                new KafkaRecord<>(new RecordHeaders(), "key-123", HasKeyTest.VALUE_1)
            ),
            new IsEqual<>(false)
        );
    }

    @Test
    void describesExpectedKey() {
        final StringDescription description = new StringDescription();
        new HasKey<>(HasKeyTest.KEY_1).describeTo(description);
        MatcherAssert.assertThat(
            "The description should mention the expected key",
            description.toString(),
            new StringContains(HasKeyTest.KEY_1)
        );
    }
}
