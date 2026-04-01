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

import com.github.roroche.kstreamsmatchers.WithHeaders;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

/**
 * A Hamcrest matcher that checks if a {@link WithHeaders} object
 * has a header with a specific key and value.
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Assert that a record has a specific header
 * RecordHeaders headers = new RecordHeaders();
 * headers.add("request-id", "12345".getBytes(StandardCharsets.UTF_8));
 * KafkaRecord<String, String> record = new KafkaRecord<>(headers, "key", "value");
 *
 * MatcherAssert.assertThat(
 *     record,
 *     new HasHeaders("request-id", "12345".getBytes(StandardCharsets.UTF_8))
 * );
 *
 * // Use with HasRecord matcher to validate complete record
 * MatcherAssert.assertThat(
 *     record,
 *     new HasRecord<>(
 *         new HasHeaders("request-id", "12345".getBytes(StandardCharsets.UTF_8)),
 *         new HasKey<>("key"),
 *         new HasValue<>("value")
 *     )
 * );
 * }</pre>
 *
 * @since 0.0.1
 */
public final class HasHeaders extends TypeSafeMatcher<WithHeaders> {
    /**
     * The key of the header to check.
     */
    private final String key;

    /**
     * The matcher for the value of the header.
     */
    private final Matcher<byte[]> expected;

    /**
     * Constructs a HasHeaders matcher with the given key and expected value matcher.
     *
     * @param key The key of the header to check
     * @param expected The matcher for the value of the header
     */
    public HasHeaders(final String key, final Matcher<byte[]> expected) {
        this.key = key;
        this.expected = expected;
    }

    /**
     * Constructs a HasHeaders matcher with the given key and expected value.
     *
     * @param key The key of the header to check
     * @param expected The expected value of the header
     */
    public HasHeaders(final String key, final byte[] expected) {
        this(key, new IsEqual<>(expected));
    }

    @SuppressWarnings({"nullfree", "allfinal"})
    @Override
    public boolean matchesSafely(final WithHeaders actual) {
        final Headers headers = actual.headers();
        boolean matches = false;
        if (headers != null) {
            final Header header = headers.lastHeader(this.key);
            if (header != null) {
                matches = this.expected.matches(header.value());
            }
        }
        return matches;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("a TestRecord containing header ")
            .appendValue(this.key)
            .appendText(" with value ");
        this.expected.describeTo(description);
    }

    @SuppressWarnings("nullfree")
    @Override
    public void describeMismatchSafely(final WithHeaders actual, final Description description) {
        final Header header = actual.headers().lastHeader(this.key);
        if (header == null) {
            description.appendText("header was missing");
        } else {
            description.appendText("value was ")
                .appendValue(new String(header.value(), StandardCharsets.UTF_8));
        }
    }
}
