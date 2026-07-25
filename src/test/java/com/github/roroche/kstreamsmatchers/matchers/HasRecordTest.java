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
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.test.TestRecord;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link HasRecord}.
 * @since 0.0.2
 */
@SuppressWarnings({
    "allpublic",
    "allfinal",
    "staticfree",
    "JTCOP.RuleNotContainsTestWord",
    "JTCOP.RuleProhibitStaticFields"
})
final class HasRecordTest {

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

    /**
     * Constant to define value 2.
     */
    private static final String VALUE_2 = "value-2";

    @Test
    void matchesWhenKeyAndValueAreEqualAndHeadersAreIgnored() {
        MatcherAssert.assertThat(
            "When key and value match, and headers are ignored, the matcher should match",
            new KafkaRecord<>(new RecordHeaders(), HasRecordTest.KEY_1, HasRecordTest.VALUE_1),
            new HasRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1)
        );
    }

    @Test
    void matchesFromKeyValue() {
        MatcherAssert.assertThat(
            "When constructed from a KeyValue, the matcher should match a record with the same key and value",
            new KafkaRecord<>(new RecordHeaders(), HasRecordTest.KEY_1, HasRecordTest.VALUE_1),
            new HasRecord<>(new KeyValue<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1))
        );
    }

    @Test
    void matchesFromMapEntry() {
        MatcherAssert.assertThat(
            "When constructed from a Map.Entry, the matcher should match a record with the same key and value",
            new KafkaRecord<>(new RecordHeaders(), HasRecordTest.KEY_1, HasRecordTest.VALUE_1),
            new HasRecord<>(new MapEntry<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1))
        );
    }

    @Test
    void doesNotMatchWhenKeyDiffers() {
        MatcherAssert.assertThat(
            "When the key differs, the matcher should not match",
            new HasRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1).matches(
                new KafkaRecord<>(new RecordHeaders(), HasRecordTest.KEY_2, HasRecordTest.VALUE_1)
            ),
            Matchers.is(false)
        );
    }

    @Test
    void doesNotMatchWhenValueDiffers() {
        MatcherAssert.assertThat(
            "When the value differs, the matcher should not match",
            new HasRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1).matches(
                new KafkaRecord<>(
                    new RecordHeaders(),
                    HasRecordTest.KEY_1,
                    HasRecordTest.VALUE_2
                )
            ),
            Matchers.is(false)
        );
    }

    @Test
    void matchesUsingExplicitHeaderKeyAndValueMatchers() {
        MatcherAssert.assertThat(
            "When constructed with explicit delegate matchers, they should all be applied",
            new KafkaRecord<>(new RecordHeaders(), HasRecordTest.KEY_1, HasRecordTest.VALUE_1),
            new HasRecord<>(
                new IgnoreHeaders(),
                new HasKey<>(HasRecordTest.KEY_1),
                new HasValue<>(HasRecordTest.VALUE_1)
            )
        );
    }

    @Test
    void doesNotMatchWhenHeaderMatcherFails() {
        MatcherAssert.assertThat(
            "When the header matcher fails, the overall record matcher should not match",
            new HasRecord<>(
                new HasHeaders("missing-header", "value".getBytes(StandardCharsets.UTF_8)),
                new HasKey<>(HasRecordTest.KEY_1),
                new HasValue<>(HasRecordTest.VALUE_1)
            ).matches(
                new KafkaRecord<>(
                    new RecordHeaders(),
                    HasRecordTest.KEY_1,
                    HasRecordTest.VALUE_1
                )
            ),
            Matchers.is(false)
        );
    }

    @Test
    void describesHeadersKeyAndValue() {
        final StringDescription description = new StringDescription();
        new HasRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1).describeTo(description);
        MatcherAssert.assertThat(
            "The description should mention the headers, key and value matchers",
            description.toString(),
            Matchers.allOf(
                Matchers.containsString("Ignored headers"),
                Matchers.containsString(HasRecordTest.KEY_1),
                Matchers.containsString(HasRecordTest.VALUE_1)
            )
        );
    }

    @Test
    void hasRecordFromTestRecordDelegatesToKafkaRecordMatcher() {
        MatcherAssert.assertThat(
            "The FromTestRecord matcher should delegate to a KafkaRecord matcher built from the TestRecord",
            new TestRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1),
            new HasRecord.FromTestRecord<>(
                new HasRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1)
            )
        );
    }

    @Test
    void isFalseFromTestRecordDoesNotMatchWhenDelegateDoesNotMatch() {
        MatcherAssert.assertThat(
            "When the delegate matcher does not match, the FromTestRecord matcher should not match either",
            new HasRecord.FromTestRecord<>(
                new HasRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1)
            ).matches(
                new TestRecord<>(HasRecordTest.KEY_2, HasRecordTest.VALUE_1)
            ),
            Matchers.is(false)
        );
    }

    @Test
    void containsKeyFromTestRecordDescribesTheDelegateMatcher() {
        final StringDescription description = new StringDescription();
        new HasRecord.FromTestRecord<>(
            new HasRecord<>(HasRecordTest.KEY_1, HasRecordTest.VALUE_1)
        ).describeTo(description);
        MatcherAssert.assertThat(
            "The FromTestRecord matcher should describe the delegate matcher",
            description.toString(),
            Matchers.containsString(HasRecordTest.KEY_1)
        );
    }
}
