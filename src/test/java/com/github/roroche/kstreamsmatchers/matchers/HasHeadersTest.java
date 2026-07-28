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
import com.github.roroche.kstreamsmatchers.WithHeaders;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link HasHeaders}.
 * @since 0.0.2
 */
@SuppressWarnings({"allpublic", "allfinal", "staticfree", "JTCOP.RuleProhibitStaticFields"})
final class HasHeadersTest {

    /**
     * Constant for key.
     */
    private static final String KEY = "key";

    /**
     * Constant for value.
     */
    private static final String VALUE = "value";

    /**
     * Constant for request id.
     */
    private static final String REQUEST_ID = "request-id";

    /**
     * Request for 12345.
     */
    private static final String STR_12345 = "12345";

    /**
     * Marker text used to prove that the delegate matcher's own
     * description is actually appended, and not just synthesized text.
     */
    private static final String MARKER = "marker-value-matcher-description";

    @Test
    void matchesWhenHeaderValueIsEqual() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add(
            HasHeadersTest.REQUEST_ID,
            HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "When the header value equals the expected value, the matcher should match",
            new KafkaRecord<>(headers, HasHeadersTest.KEY, HasHeadersTest.VALUE),
            new HasHeaders(
                HasHeadersTest.REQUEST_ID,
                HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    @Test
    void matchesUsingDelegateMatcher() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add(
            HasHeadersTest.REQUEST_ID,
            HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "When constructed with a delegate matcher, it should be used to match the header value",
            new KafkaRecord<>(headers, HasHeadersTest.KEY, HasHeadersTest.VALUE),
            new HasHeaders(
                HasHeadersTest.REQUEST_ID,
                Matchers.equalTo(HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8))
            )
        );
    }

    @Test
    void doesNotMatchWhenHeaderValueDiffers() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add(
            HasHeadersTest.REQUEST_ID,
            HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "When the header value differs from the expected value, the matcher should not match",
            new HasHeaders(HasHeadersTest.REQUEST_ID, "other".getBytes(StandardCharsets.UTF_8))
                .matches(new KafkaRecord<>(headers, HasHeadersTest.KEY, HasHeadersTest.VALUE)),
            Matchers.is(false)
        );
    }

    @Test
    void doesNotMatchWhenHeaderIsMissing() {
        MatcherAssert.assertThat(
            "When the header is missing, the matcher should not match",
            new HasHeaders(
                HasHeadersTest.REQUEST_ID,
                HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
            ).matches(
                new KafkaRecord<>(
                    new RecordHeaders(),
                    HasHeadersTest.KEY,
                    HasHeadersTest.VALUE
                )
            ),
            Matchers.is(false)
        );
    }

    @Test
    void doesNotMatchWhenHeadersAreOtherwisePresentButKeyIsAbsent() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add("other-header", HasHeadersTest.VALUE.getBytes(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(
            "When headers exist but not the expected key, the matcher should not match",
            new HasHeaders(
                HasHeadersTest.REQUEST_ID,
                HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
            ).matches(
                new KafkaRecord<>(headers, HasHeadersTest.KEY, HasHeadersTest.VALUE)
            ),
            Matchers.is(false)
        );
    }

    @SuppressWarnings("nullfree")
    @Test
    void doesNotMatchWhenHeadersAreNull() {
        MatcherAssert.assertThat(
            "When the headers themselves are null, the matcher should not match",
            new HasHeaders(
                HasHeadersTest.REQUEST_ID,
                HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
            ).matches((WithHeaders) () -> null),
            Matchers.is(false)
        );
    }

    @Test
    void describesExpectedKeyAndValue() {
        final StringDescription description = new StringDescription();
        new HasHeaders(
            HasHeadersTest.REQUEST_ID,
            HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
        ).describeTo(description);
        MatcherAssert.assertThat(
            "The description should mention the expected header key",
            description.toString(),
            Matchers.containsString(HasHeadersTest.REQUEST_ID)
        );
    }

    @Test
    void describesExpectedValueUsingDelegateMatcherDescription() {
        final StringDescription description = new StringDescription();
        new HasHeaders(
            HasHeadersTest.REQUEST_ID,
            new HasHeadersTest.MarkerMatcher()
        ).describeTo(description);
        MatcherAssert.assertThat(
            "Description includes the delegate matcher's own description of the expected value",
            description.toString(),
            Matchers.containsString(HasHeadersTest.MARKER)
        );
    }

    @Test
    void describesMismatchWhenHeaderIsMissing() {
        final StringDescription description = new StringDescription();
        new HasHeaders(
            HasHeadersTest.REQUEST_ID,
            HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
        ).describeMismatch(
            new KafkaRecord<>(
                new RecordHeaders(),
                HasHeadersTest.KEY,
                HasHeadersTest.VALUE
            ),
            description
        );
        MatcherAssert.assertThat(
            "The mismatch description should mention the header was missing",
            description.toString(),
            Matchers.containsString("header was missing")
        );
    }

    @Test
    void describesMismatchWhenHeaderValueDiffers() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add(
            HasHeadersTest.REQUEST_ID,
            HasHeadersTest.STR_12345.getBytes(StandardCharsets.UTF_8)
        );
        final StringDescription description = new StringDescription();
        new HasHeaders(
            HasHeadersTest.REQUEST_ID,
            "other".getBytes(StandardCharsets.UTF_8)
        ).describeMismatch(
            new KafkaRecord<>(headers, HasHeadersTest.KEY, HasHeadersTest.VALUE),
            description
        );
        MatcherAssert.assertThat(
            "The mismatch description should mention the actual header value",
            description.toString(),
            Matchers.containsString(HasHeadersTest.STR_12345)
        );
    }

    /**
     * A matcher that always matches and describes itself with {@link HasHeadersTest#MARKER},
     * used solely to prove that {@link HasHeaders#describeTo(Description)}
     * delegates to the expected matcher's own description.
     * @since 0.0.2
     */
    private static final class MarkerMatcher extends BaseMatcher<byte[]> {

        @Override
        public boolean matches(final Object item) {
            return true;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText(HasHeadersTest.MARKER);
        }
    }
}
