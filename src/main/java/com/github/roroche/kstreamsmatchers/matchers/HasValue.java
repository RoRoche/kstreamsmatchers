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

import com.github.roroche.kstreamsmatchers.WithValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

/**
 * A Hamcrest matcher that checks if a {@link WithValue} object
 * has a value matching a specific value or matcher.
 *
 * @param <V> The type of the value
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Assert that a record has a specific value
 * KafkaRecord<String, String> record = new KafkaRecord<>(headers, "key", "HelloWorld");
 *
 * MatcherAssert.assertThat(
 *     record,
 *     new HasValue<>("HelloWorld")
 * );
 *
 * // Use with HasRecord matcher to validate complete record structure
 * MatcherAssert.assertThat(
 *     record,
 *     new HasRecord<>(
 *         new HasKey<>("key"),
 *         new HasValue<>("HelloWorld")
 *     )
 * );
 * }</pre>
 *
 * @since 0.0.1
 */
public final class HasValue<V> extends TypeSafeMatcher<WithValue<V>> {
    /**
     * The matcher for the value of the object.
     */
    private final Matcher<V> expected;

    /**
     * Constructs a HasValue matcher with the given expected value matcher.
     *
     * @param expected The matcher for the value of the object
     */
    public HasValue(final Matcher<V> expected) {
        this.expected = expected;
    }

    /**
     * Constructs a HasValue matcher with the given expected value.
     *
     * @param expected The expected value of the object
     */
    public HasValue(final V expected) {
        this(new IsEqual<>(expected));
    }

    @Override
    public boolean matchesSafely(final WithValue<V> actual) {
        return this.expected.matches(actual.value());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Value matching ");
        this.expected.describeTo(description);
    }
}
