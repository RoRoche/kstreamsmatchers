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

import com.github.roroche.kstreamsmatchers.WithKey;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

/**
 * A Hamcrest matcher that checks if a {@link WithKey} object has a key
 * matching a specific value or matcher.
 *
 * @param <K> The type of the key
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Assert that a record has a specific key
 * KafkaRecord<String, String> record = new KafkaRecord<>(headers, "user-123", "data");
 *
 * MatcherAssert.assertThat(
 *     record,
 *     new HasKey<>("user-123")
 * );
 *
 * // Use with HasRecord matcher to check multiple fields
 * MatcherAssert.assertThat(
 *     record,
 *     new HasRecord<>(
 *         new HasKey<>("user-123"),
 *         new HasValue<>("data")
 *     )
 * );
 * }</pre>
 *
 * @since 0.0.1
 * @checkstyle ProtectedMethodInFinalClassCheck (97 lines)
 */
@SuppressWarnings("allpublic")
public final class HasKey<K> extends TypeSafeMatcher<WithKey<K>> {
    /**
     * The matcher for the key of the object.
     */
    private final Matcher<K> expected;

    /**
     * Constructs a HasKey matcher with the given expected key matcher.
     *
     * @param expected The matcher for the key of the object
     */
    public HasKey(final Matcher<K> expected) {
        this.expected = expected;
    }

    /**
     * Constructs a HasKey matcher with the given expected key.
     *
     * @param expected The expected key of the object
     */
    public HasKey(final K expected) {
        this(new IsEqual<>(expected));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Key matching ");
        this.expected.describeTo(description);
    }

    @Override
    protected boolean matchesSafely(final WithKey<K> actual) {
        return this.expected.matches(actual.key());
    }
}
