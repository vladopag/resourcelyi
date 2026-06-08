package com.vladopag.resourcelyi.support;

import java.nio.file.Files;
import java.nio.file.Path;

public final class RuntimeEnvironment {

    private RuntimeEnvironment() {}

    /** Returns {@code docker} when running inside a container, otherwise {@code host}. */
    public static String detect() {
        if (Files.exists(Path.of("/.dockerenv")) || Files.exists(Path.of("/run/.containerenv"))) {
            return "docker";
        }
        return "host";
    }
}
