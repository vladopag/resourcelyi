package com.vladopag.resourcelyi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resourcelyi")
public class ResourcelyiProperties {

    private String diskPath = "";

    public String getDiskPath() {
        return diskPath;
    }

    public void setDiskPath(String diskPath) {
        this.diskPath = diskPath;
    }

    public String resolvedDiskPath() {
        if (diskPath != null && !diskPath.isBlank()) {
            return diskPath.trim();
        }
        return DefaultDiskPath.get();
    }
}
