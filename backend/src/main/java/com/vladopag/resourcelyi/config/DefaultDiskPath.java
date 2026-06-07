package com.vladopag.resourcelyi.config;

public final class DefaultDiskPath {

    private DefaultDiskPath() {}

    public static String get() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return "C:\\";
        }
        return "/";
    }
}
