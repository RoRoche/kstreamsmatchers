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
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * A Hamcrest matcher that ignores the headers of a {@link WithHeaders} object.
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // When you don't care about headers, use IgnoreHeaders with HasRecord
 * KafkaRecord<String, String> record = new KafkaRecord<>(headers, "key", "value");
 *
 * MatcherAssert.assertThat(
 *     record,
 *     new HasRecord<>(
 *         new IgnoreHeaders(),  // Ignore headers validation
 *         new HasKey<>("key"),
 *         new HasValue<>("value")
 *     )
 * );
 *
 * // Or use the simplified HasRecord(K, V) constructor which ignores headers
 * MatcherAssert.assertThat(
 *     record,
 *     new HasRecord<>("key", "value")
 * );
 * }</pre>
 *
 * @since 0.0.1
 * @checkstyle ProtectedMethodInFinalClassCheck (69 lines)
 */
@SuppressWarnings("allpublic")
public final class IgnoreHeaders extends TypeSafeMatcher<WithHeaders> {
    @Override
    public void describeTo(final Description description) {
        description.appendText("Ignored headers");
    }

    @Override
    protected boolean matchesSafely(final WithHeaders actual) {
        return true;
    }
}
