package com.vladopag.resourcelyi.dto;

import java.util.List;

public record CpuStats(
        String model,
        String vendor,
        int physicalCores,
        int logicalCores,
        double totalPercent,
        List<Double> perCorePercent,
        double load1,
        double load5,
        double load15) {}
