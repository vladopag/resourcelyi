package com.vladopag.resourcelyi.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AppVersionTest {

    @Test
    void exposesVersionFromManifestOrFallback() {
        AppVersion appVersion = new AppVersion();
        assertNotNull(appVersion.version());
        assertNotNull(appVersion.displayVersion());
    }

    @Test
    void formatDisplayVersion() {
        assertEquals("v3.3", AppVersion.formatDisplay("3.3.0"));
        assertEquals("v3.3", AppVersion.formatDisplay("3.3.1"));
        assertEquals("v4.0", AppVersion.formatDisplay("4.0.0"));
    }
}
