package com.vladopag.resourcelyi.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DurationFormatterTest {

    @Test
    void formatsMinutesAndSeconds() {
        assertEquals("5m 9s", DurationFormatter.formatSeconds(309));
    }

    @Test
    void formatsHoursMinutesAndSeconds() {
        assertEquals("2h 15m 30s", DurationFormatter.formatSeconds(8130));
    }

    @Test
    void formatsDaysHoursMinutesAndSeconds() {
        assertEquals("1d 1h 3m 4s", DurationFormatter.formatSeconds(90184));
    }

    @Test
    void formatsZeroSeconds() {
        assertEquals("0m 0s", DurationFormatter.formatSeconds(0));
    }
}
