package com.vladopag.resourcelyi.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CliArgumentsTest {

    @Test
    void isCliModeWhenFlagPresent() {
        assertTrue(CliArguments.isCliMode("--cli"));
        assertTrue(CliArguments.isCliMode("--disk", "--cli", "--interval", "2"));
    }

    @Test
    void isCliModeWhenFlagAbsent() {
        assertFalse(CliArguments.isCliMode());
        assertFalse(CliArguments.isCliMode("--interval", "2"));
    }

    @Test
    void parseIntervalFromShortFlag() {
        assertEquals(5, CliArguments.parseInterval("-i", "5"));
    }

    @Test
    void parseIntervalFromLongFlag() {
        assertEquals(3, CliArguments.parseInterval("--interval", "3"));
    }

    @Test
    void parseIntervalFromEqualsForm() {
        assertEquals(10, CliArguments.parseInterval("--interval=10"));
    }

    @Test
    void parseIntervalDefaultsToOne() {
        assertEquals(1, CliArguments.parseInterval());
    }
}
