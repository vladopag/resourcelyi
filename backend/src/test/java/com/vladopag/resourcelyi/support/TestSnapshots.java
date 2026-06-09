package com.vladopag.resourcelyi.support;

import java.util.List;

import com.vladopag.resourcelyi.dto.CpuStats;
import com.vladopag.resourcelyi.dto.DiskIOStat;
import com.vladopag.resourcelyi.dto.DiskStats;
import com.vladopag.resourcelyi.dto.MemoryStats;
import com.vladopag.resourcelyi.dto.NetworkInterfaceStat;
import com.vladopag.resourcelyi.dto.NetworkStats;
import com.vladopag.resourcelyi.dto.Snapshot;
import com.vladopag.resourcelyi.dto.SystemInfo;

public final class TestSnapshots {

    private TestSnapshots() {}

    public static Snapshot sample() {
        return new Snapshot(
                "2026-06-08T12:00:00Z",
                new SystemInfo(
                        "test-host",
                        "Linux",
                        "6.8.0",
                        "ubuntu",
                        "Linux 6.8.0",
                        "amd64",
                        "2h 15m 30s",
                        8130,
                        "2026-06-08 09:44:30",
                        "host"),
                new CpuStats(
                        "Intel Core i7",
                        "GenuineIntel",
                        4,
                        8,
                        25.5,
                        List.of(10.0, 20.0, 30.0, 40.0),
                        1.2,
                        0.9,
                        0.7),
                new MemoryStats(16.0, 8.0, 8.0, 50.0),
                new DiskStats("/", 500.0, 100.0, 400.0, 20.0),
                List.of(new DiskIOStat("sda", 1.5, 0.5)),
                new NetworkStats(
                        List.of(new NetworkInterfaceStat("eth0", 2.0, 1.0)),
                        2.0,
                        1.0));
    }
}
