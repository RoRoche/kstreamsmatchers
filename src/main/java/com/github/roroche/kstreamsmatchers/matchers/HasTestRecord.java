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
import org.apache.kafka.streams.test.TestRecord;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * A Hamcrest matcher that checks if a {@link TestRecord}
 * has specific headers, key and value, by converting it to a {@link KafkaRecord}.
 * @param <K> The type of the key
 * @param <V> The type of the value
 * @since 0.0.1
 */
@SuppressWarnings("staticfree")
public final class HasTestRecord<K, V> extends TypeSafeMatcher<TestRecord<K, V>> {

    /**
     * The delegate matcher for the KafkaRecord converted from the TestRecord.
     */
    private final Matcher<KafkaRecord<K, V>> delegate;

    /**
     * Constructs a FromTestRecord matcher with the given delegate matcher.
     * @param delegate The delegate matcher for the KafkaRecord converted from the TestRecord
     */
    public HasTestRecord(final Matcher<KafkaRecord<K, V>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void describeTo(final Description description) {
        this.delegate.describeTo(description);
    }

    // @checkstyle ProtectedMethodInFinalClassCheck (7 line)
    @SuppressWarnings("allpublic")
    @Override
    protected boolean matchesSafely(final TestRecord<K, V> trecord) {
        return this.delegate.matches(
            new KafkaRecord<>(trecord)
        );
    }
}
