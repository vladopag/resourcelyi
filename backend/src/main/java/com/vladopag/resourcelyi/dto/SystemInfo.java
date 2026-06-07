package com.vladopag.resourcelyi.dto;

public record SystemInfo(
        String hostname,
        String os,
        String osVersion,
        String osFamily,
        String kernel,
        String arch,
        String uptime,
        long uptimeSeconds,
        String bootTime) {}
