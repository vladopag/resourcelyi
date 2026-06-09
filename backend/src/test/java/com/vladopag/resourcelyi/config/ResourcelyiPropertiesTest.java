package com.vladopag.resourcelyi.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ResourcelyiPropertiesTest {

    @Test
    void usesConfiguredDiskPathWhenSet() {
        ResourcelyiProperties properties = new ResourcelyiProperties();
        properties.setDiskPath("/custom");

        assertEquals("/custom", properties.resolvedDiskPath());
    }

    @Test
    void trimsConfiguredDiskPath() {
        ResourcelyiProperties properties = new ResourcelyiProperties();
        properties.setDiskPath("  /data  ");

        assertEquals("/data", properties.resolvedDiskPath());
    }

    @Test
    void fallsBackToDefaultWhenBlank() {
        ResourcelyiProperties properties = new ResourcelyiProperties();
        properties.setDiskPath("   ");

        assertEquals(DefaultDiskPath.get(), properties.resolvedDiskPath());
    }
}
