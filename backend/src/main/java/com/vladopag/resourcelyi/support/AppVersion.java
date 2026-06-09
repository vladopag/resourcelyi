package com.vladopag.resourcelyi.support;

import org.springframework.stereotype.Component;

@Component
public class AppVersion {

    private final String version;
    private final String displayVersion;

    public AppVersion() {
        String impl = AppVersion.class.getPackage().getImplementationVersion();
        if (impl == null || impl.isBlank()) {
            this.version = "unknown";
            this.displayVersion = "unknown";
        } else {
            this.version = impl;
            this.displayVersion = formatDisplay(impl);
        }
    }

    public String version() {
        return version;
    }

    public String displayVersion() {
        return displayVersion;
    }

    static String formatDisplay(String impl) {
        String[] parts = impl.split("\\.");
        if (parts.length >= 2) {
            return "v" + parts[0] + "." + parts[1];
        }
        return "v" + impl;
    }
}
