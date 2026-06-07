package com.vladopag.resourcelyi.dto;

public record DiskStats(String path, double totalGB, double usedGB, double freeGB, double usedPercent) {}
