package com.vladopag.resourcelyi.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vladopag.resourcelyi.config.ResourcelyiProperties;
import com.vladopag.resourcelyi.dto.CpuStats;
import com.vladopag.resourcelyi.dto.DiskIOStat;
import com.vladopag.resourcelyi.dto.DiskStats;
import com.vladopag.resourcelyi.dto.MemoryStats;
import com.vladopag.resourcelyi.dto.NetworkInterfaceStat;
import com.vladopag.resourcelyi.dto.NetworkStats;
import com.vladopag.resourcelyi.dto.Snapshot;
import com.vladopag.resourcelyi.dto.SystemInfo;
import com.vladopag.resourcelyi.support.DurationFormatter;

import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

@Service
public class MetricsService {

    private static final DateTimeFormatter BOOT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final String diskPath;
    private final oshi.SystemInfo oshi = new oshi.SystemInfo();

    private Map<String, long[]> lastDiskIO = Map.of();
    private long lastDiskIOCheckMs = System.currentTimeMillis();

    private Map<String, long[]> lastNetIO = Map.of();
    private long lastNetCheckMs = System.currentTimeMillis();

    public MetricsService(ResourcelyiProperties properties) {
        this.diskPath = properties.resolvedDiskPath();
    }

    public synchronized Snapshot collect() throws InterruptedException {
        HardwareAbstractionLayer hal = oshi.getHardware();
        OperatingSystem os = oshi.getOperatingSystem();
        CentralProcessor processor = hal.getProcessor();

        long[] systemTicks = processor.getSystemCpuLoadTicks();
        long[][] prevTicks = processor.getProcessorCpuLoadTicks();
        Thread.sleep(1000);

        double totalPercent = round2(processor.getSystemCpuLoadBetweenTicks(systemTicks) * 100.0);

        double[] perCore = processor.getProcessorCpuLoadBetweenTicks(prevTicks);
        List<Double> perCorePercent = new ArrayList<>(perCore.length);
        for (double load : perCore) {
            perCorePercent.add(round2(load * 100.0));
        }

        double[] loadAvg = processor.getSystemLoadAverage(3);
        double load1 = sanitizeLoad(loadAvg, 0);
        double load5 = sanitizeLoad(loadAvg, 1);
        double load15 = sanitizeLoad(loadAvg, 2);

        var cpuId = processor.getProcessorIdentifier();
        CpuStats cpu = new CpuStats(
                cpuId.getName(),
                cpuId.getVendor(),
                processor.getPhysicalProcessorCount(),
                processor.getLogicalProcessorCount(),
                totalPercent,
                perCorePercent,
                load1,
                load5,
                load15);

        String hostname = os.getNetworkParams().getHostName();
        if (hostname == null || hostname.isBlank()) {
            hostname = "unknown";
        }

        String platform = os.getFamily();
        if (platform == null || platform.isBlank()) {
            platform = System.getProperty("os.name", "unknown");
        }

        var versionInfo = os.getVersionInfo();
        String osVersion = versionInfo != null ? versionInfo.getVersion() : "";
        String osFamily = versionInfo != null ? versionInfo.getCodeName() : "";
        if (osFamily == null) {
            osFamily = "";
        }

        long uptimeSeconds = os.getSystemUptime();
        Instant bootInstant = Instant.now().minusSeconds(uptimeSeconds);

        SystemInfo systemInfo = new SystemInfo(
                hostname,
                platform,
                osVersion != null ? osVersion : "",
                osFamily,
                platform + " " + (osVersion != null ? osVersion : ""),
                System.getProperty("os.arch", "unknown"),
                DurationFormatter.formatSeconds(uptimeSeconds),
                uptimeSeconds,
                BOOT_TIME_FORMAT.format(bootInstant));

        GlobalMemory memory = hal.getMemory();
        long memTotal = memory.getTotal();
        long memAvailable = memory.getAvailable();
        long memUsed = memTotal - memAvailable;
        double memUsedPercent = memTotal > 0 ? (memUsed * 100.0) / memTotal : 0.0;

        MemoryStats memoryStats = new MemoryStats(
                bytesToGB(memTotal),
                bytesToGB(memUsed),
                bytesToGB(memAvailable),
                round2(memUsedPercent));

        DiskStats diskStats = collectDiskUsage(os);
        List<DiskIOStat> diskIO = collectDiskIO(hal);
        NetworkStats network = collectNetwork(hal);

        return new Snapshot(
                Instant.now().toString(),
                systemInfo,
                cpu,
                memoryStats,
                diskStats,
                diskIO,
                network);
    }

    private DiskStats collectDiskUsage(OperatingSystem os) {
        for (OSFileStore store : os.getFileSystem().getFileStores()) {
            String mount = store.getMount();
            if (mount != null && pathsMatch(mount, diskPath)) {
                long total = store.getTotalSpace();
                long usable = store.getUsableSpace();
                long used = total - usable;
                double usedPercent = total > 0 ? (used * 100.0) / total : 0.0;
                return new DiskStats(
                        diskPath,
                        bytesToGB(total),
                        bytesToGB(used),
                        bytesToGB(usable),
                        round2(usedPercent));
            }
        }
        return null;
    }

    private boolean pathsMatch(String mount, String configured) {
        String a = normalizePath(mount);
        String b = normalizePath(configured);
        return a.equals(b) || a.startsWith(b) || b.startsWith(a);
    }

    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        return path.replace('\\', '/').replaceAll("/+$", "").toLowerCase();
    }

    private List<DiskIOStat> collectDiskIO(HardwareAbstractionLayer hal) {
        long now = System.currentTimeMillis();
        double seconds = (now - lastDiskIOCheckMs) / 1000.0;

        List<DiskIOStat> stats = new ArrayList<>();
        Map<String, long[]> current = new HashMap<>();

        for (HWDiskStore disk : hal.getDiskStores()) {
            disk.updateAttributes();
            String name = disk.getName();
            long read = disk.getReadBytes();
            long write = disk.getWriteBytes();
            current.put(name, new long[] {read, write});

            double readMBps = 0;
            double writeMBps = 0;
            if (seconds > 0 && lastDiskIO.containsKey(name)) {
                long[] prev = lastDiskIO.get(name);
                readMBps = bytesPerSecondToMBps(read - prev[0], seconds);
                writeMBps = bytesPerSecondToMBps(write - prev[1], seconds);
            }
            stats.add(new DiskIOStat(name, round2(readMBps), round2(writeMBps)));
        }

        stats.sort(Comparator.comparing(DiskIOStat::name));
        lastDiskIO = current;
        lastDiskIOCheckMs = now;
        return stats;
    }

    private NetworkStats collectNetwork(HardwareAbstractionLayer hal) {
        long now = System.currentTimeMillis();
        double seconds = (now - lastNetCheckMs) / 1000.0;

        List<NetworkInterfaceStat> interfaces = new ArrayList<>();
        Map<String, long[]> current = new HashMap<>();
        double totalRx = 0;
        double totalTx = 0;

        for (NetworkIF net : hal.getNetworkIFs()) {
            net.updateAttributes();
            String name = net.getName();
            long rx = net.getBytesRecv();
            long tx = net.getBytesSent();
            current.put(name, new long[] {rx, tx});

            double rxMBps = 0;
            double txMBps = 0;
            if (seconds > 0 && lastNetIO.containsKey(name)) {
                long[] prev = lastNetIO.get(name);
                rxMBps = bytesPerSecondToMBps(rx - prev[0], seconds);
                txMBps = bytesPerSecondToMBps(tx - prev[1], seconds);
            }
            interfaces.add(new NetworkInterfaceStat(name, round2(rxMBps), round2(txMBps)));
            totalRx += rxMBps;
            totalTx += txMBps;
        }

        lastNetIO = current;
        lastNetCheckMs = now;
        return new NetworkStats(interfaces, round2(totalRx), round2(totalTx));
    }

    private static double sanitizeLoad(double[] loadAvg, int index) {
        if (loadAvg == null || index >= loadAvg.length || loadAvg[index] < 0) {
            return 0;
        }
        return round2(loadAvg[index]);
    }

    private static double bytesToGB(long bytes) {
        return round2(bytes / 1024.0 / 1024.0 / 1024.0);
    }

    private static double bytesPerSecondToMBps(long byteDelta, double seconds) {
        return (byteDelta / seconds) / 1024.0 / 1024.0;
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
