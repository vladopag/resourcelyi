package com.vladopag.resourcelyi.cli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vladopag.resourcelyi.dto.DiskIOStat;
import com.vladopag.resourcelyi.dto.NetworkInterfaceStat;
import com.vladopag.resourcelyi.dto.NetworkStats;
import com.vladopag.resourcelyi.dto.Snapshot;

public final class TerminalDisplay {

    private static final DateTimeFormatter UPDATED_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TerminalDisplay() {}

    public static String format(Snapshot snapshot) {
        StringBuilder output = new StringBuilder();

        appendSystemSection(output, snapshot);
        appendCpuSection(output, snapshot);
        appendMemorySection(output, snapshot);

        if (snapshot.disk() != null) {
            appendDiskSection(output, snapshot);
        }
        if (snapshot.diskIO() != null && !snapshot.diskIO().isEmpty()) {
            appendDiskIoSection(output, snapshot.diskIO());
        }
        if (snapshot.network() != null
                && snapshot.network().interfaces() != null
                && !snapshot.network().interfaces().isEmpty()) {
            appendNetworkSection(output, snapshot.network().interfaces(), snapshot.network());
        }

        return output.toString();
    }

    private static void appendSystemSection(StringBuilder output, Snapshot snapshot) {
        var system = snapshot.system();
        output.append("╔═══════════════════════════════════════╗\n");
        output.append("║         SYSTEM INFORMATION            ║\n");
        output.append("╚═══════════════════════════════════════╝\n");
        output.append("Hostname: ").append(system.hostname()).append('\n');
        output.append("OS: ")
                .append(system.os())
                .append(' ')
                .append(system.osVersion())
                .append(" (")
                .append(system.osFamily())
                .append(")\n");
        output.append("Kernel: ").append(system.kernel()).append('\n');
        output.append("Arch: ").append(system.arch()).append('\n');
        output.append("Uptime: ").append(system.uptime()).append('\n');
        output.append("Boot Time: ").append(system.bootTime()).append('\n');
        if ("docker".equals(system.runtimeEnvironment())) {
            output.append("Runtime: Docker container (not the physical host OS)\n");
        }
        output.append('\n');
    }

    private static void appendCpuSection(StringBuilder output, Snapshot snapshot) {
        var cpu = snapshot.cpu();
        output.append("╔═══════════════════════════════════════╗\n");
        output.append("║         CPU USAGE MONITORING          ║\n");
        output.append("╚═══════════════════════════════════════╝\n");
        output.append("\nSystem: ").append(cpu.model()).append(' ').append(cpu.vendor()).append('\n');
        output.append("Cores: ")
                .append(cpu.physicalCores())
                .append(" (Logical: ")
                .append(cpu.logicalCores())
                .append(")\n");
        output.append(String.format("%n├─ Total CPU Usage: %.2f%%%n", cpu.totalPercent()));
        output.append(String.format(
                "├─ Load Averages: %.2f, %.2f, %.2f (1m, 5m, 15m)%n",
                cpu.load1(), cpu.load5(), cpu.load15()));
        output.append("\n├─ Per-CPU Usage:\n");

        List<Double> perCore = cpu.perCorePercent();
        for (int i = 0; i < perCore.size(); i++) {
            double percent = perCore.get(i);
            output.append(String.format(
                    "│  CPU %2d: [%s] %.2f%%%n", i, usageBar(percent), percent));
        }

        output.append(String.format(
                "%n└─ Last Updated: %s%n", UPDATED_FORMAT.format(LocalDateTime.now())));
    }

    private static void appendMemorySection(StringBuilder output, Snapshot snapshot) {
        var memory = snapshot.memory();
        output.append("\n╔═══════════════════════════════════════╗\n");
        output.append("║            MEMORY USAGE               ║\n");
        output.append("╚═══════════════════════════════════════╝\n");
        output.append(String.format("Total: %.2f GB%n", memory.totalGB()));
        output.append(String.format("Used : %.2f GB (%.2f%%)%n", memory.usedGB(), memory.usedPercent()));
        output.append(String.format("Free : %.2f GB%n", memory.freeGB()));
        output.append(String.format(
                "Memory: [%s] %.2f%%%n", usageBar(memory.usedPercent()), memory.usedPercent()));
    }

    private static void appendDiskSection(StringBuilder output, Snapshot snapshot) {
        var disk = snapshot.disk();
        output.append("\n╔═══════════════════════════════════════╗\n");
        output.append("║            DISK USAGE                 ║\n");
        output.append("╚═══════════════════════════════════════╝\n");
        output.append(String.format("Total: %.2f GB%n", disk.totalGB()));
        output.append(String.format("Used : %.2f GB (%.2f%%)%n", disk.usedGB(), disk.usedPercent()));
        output.append(String.format("Free : %.2f GB%n", disk.freeGB()));
        output.append(String.format(
                "Disk : [%s] %.2f%%%n", usageBar(disk.usedPercent()), disk.usedPercent()));
    }

    private static void appendDiskIoSection(StringBuilder output, List<DiskIOStat> diskIO) {
        output.append("\n╔═══════════════════════════════════════╗\n");
        output.append("║         DISK I/O (Read/Write)         ║\n");
        output.append("╚═══════════════════════════════════════╝\n");
        for (DiskIOStat io : diskIO) {
            output.append(String.format(
                    "%s: Read: %.2f MB/s | Write: %.2f MB/s%n",
                    io.name(), io.readMBps(), io.writeMBps()));
        }
    }

    private static void appendNetworkSection(
            StringBuilder output, List<NetworkInterfaceStat> interfaces, NetworkStats network) {
        output.append("\n╔═══════════════════════════════════════╗\n");
        output.append("║         NETWORK (Recv/Send)           ║\n");
        output.append("╚═══════════════════════════════════════╝\n");
        for (NetworkInterfaceStat iface : interfaces) {
            output.append(String.format(
                    "%s: Rx: %.2f MB/s | Tx: %.2f MB/s%n",
                    iface.name(), iface.rxMBps(), iface.txMBps()));
        }
        output.append(String.format(
                "Total: Rx: %.2f MB/s | Tx: %.2f MB/s%n",
                network.totalRxMBps(), network.totalTxMBps()));
    }

    private static String usageBar(double percent) {
        int barLength = (int) (percent / 5.0);
        if (barLength > 20) {
            barLength = 20;
        }
        if (barLength < 0) {
            barLength = 0;
        }
        return "█".repeat(barLength) + "░".repeat(20 - barLength);
    }
}
