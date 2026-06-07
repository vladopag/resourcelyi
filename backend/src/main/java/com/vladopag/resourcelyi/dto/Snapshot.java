package com.vladopag.resourcelyi.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Snapshot(
        String timestamp,
        SystemInfo system,
        CpuStats cpu,
        MemoryStats memory,
        DiskStats disk,
        List<DiskIOStat> diskIO,
        NetworkStats network) {}
