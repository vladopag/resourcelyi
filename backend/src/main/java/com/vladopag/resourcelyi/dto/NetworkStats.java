package com.vladopag.resourcelyi.dto;

import java.util.List;

public record NetworkStats(List<NetworkInterfaceStat> interfaces, double totalRxMBps, double totalTxMBps) {}
