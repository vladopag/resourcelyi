package com.vladopag.resourcelyi.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class RuntimeEnvironmentTest {

    @Test
    void detectReturnsKnownEnvironment() {
        String environment = RuntimeEnvironment.detect();
        assertNotNull(environment);
        assertEquals(true, environment.equals("host") || environment.equals("docker"));
    }
}
