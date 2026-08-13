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
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link IgnoreHeaders}.
 * @since 0.0.3
 */
@SuppressWarnings({"allpublic", "allfinal"})
final class IgnoreHeadersTest {

    @Test
    void matchesWhenThereAreNoHeaders() {
        MatcherAssert.assertThat(
            "The matcher should match a record with no headers",
            new KafkaRecord<>(new RecordHeaders(), "key", "value"),
            new IgnoreHeaders()
        );
    }

    @Test
    void matchesRegardlessOfHeaderContent() {
        final RecordHeaders headers = new RecordHeaders();
        headers.add("request-id", "12345".getBytes(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(
            "The matcher should match a record with any headers",
            new KafkaRecord<>(headers, "key", "value"),
            new IgnoreHeaders()
        );
    }

    @Test
    void describesItself() {
        final StringDescription description = new StringDescription();
        new IgnoreHeaders().describeTo(description);
        MatcherAssert.assertThat(
            "The description should mention that headers are ignored",
            description.toString(),
            new IsEqual<>("Ignored headers")
        );
    }
}
