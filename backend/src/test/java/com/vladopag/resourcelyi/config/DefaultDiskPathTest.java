package com.vladopag.resourcelyi.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

class DefaultDiskPathTest {

    @Test
    void returnsRootOnUnixLikeSystems() {
        assumeTrue(!System.getProperty("os.name", "").toLowerCase().contains("win"));
        assertEquals("/", DefaultDiskPath.get());
    }

    @Test
    void returnsDriveOnWindows() {
        assumeTrue(System.getProperty("os.name", "").toLowerCase().contains("win"));
        assertEquals("C:\\", DefaultDiskPath.get());
    }
}
