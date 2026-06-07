package monitor

import (
	"fmt"
	"strings"
	"time"
)

// FormatSnapshot renders a snapshot for the terminal UI.
func FormatSnapshot(s *Snapshot) string {
	var output strings.Builder

	output.WriteString("╔═══════════════════════════════════════╗\n")
	output.WriteString("║         SYSTEM INFORMATION            ║\n")
	output.WriteString("╚═══════════════════════════════════════╝\n")
	output.WriteString(fmt.Sprintf(
		"Hostname: %s\nOS: %s %s (%s)\nKernel: %s\nArch: %s\nUptime: %s\nBoot Time: %s\n\n",
		s.System.Hostname,
		s.System.OS,
		s.System.OSVersion,
		s.System.OSFamily,
		s.System.Kernel,
		s.System.Arch,
		s.System.Uptime,
		s.System.BootTime,
	))

	output.WriteString("╔═══════════════════════════════════════╗\n")
	output.WriteString("║         CPU USAGE MONITORING          ║\n")
	output.WriteString("╚═══════════════════════════════════════╝\n")
	output.WriteString(fmt.Sprintf("\nSystem: %s %s\n", s.CPU.Model, s.CPU.Vendor))
	output.WriteString(fmt.Sprintf("Cores: %d (Logical: %d)\n", s.CPU.PhysicalCores, s.CPU.LogicalCores))
	output.WriteString(fmt.Sprintf("\n├─ Total CPU Usage: %.2f%%\n", s.CPU.TotalPercent))
	output.WriteString(fmt.Sprintf("├─ Load Averages: %.2f, %.2f, %.2f (1m, 5m, 15m)\n",
		s.CPU.Load1, s.CPU.Load5, s.CPU.Load15))
	output.WriteString("\n├─ Per-CPU Usage:\n")

	for i, percent := range s.CPU.PerCorePercent {
		output.WriteString(fmt.Sprintf("│  CPU %2d: [%s] %.2f%%\n", i, usageBar(percent), percent))
	}

	output.WriteString(fmt.Sprintf("\n└─ Last Updated: %s\n", time.Now().Format("2006-01-02 15:04:05")))

	output.WriteString("\n╔═══════════════════════════════════════╗\n")
	output.WriteString("║            MEMORY USAGE               ║\n")
	output.WriteString("╚═══════════════════════════════════════╝\n")
	output.WriteString(fmt.Sprintf("Total: %.2f GB\n", s.Memory.TotalGB))
	output.WriteString(fmt.Sprintf("Used : %.2f GB (%.2f%%)\n", s.Memory.UsedGB, s.Memory.UsedPercent))
	output.WriteString(fmt.Sprintf("Free : %.2f GB\n", s.Memory.FreeGB))
	output.WriteString(fmt.Sprintf("Memory: [%s] %.2f%%\n", usageBar(s.Memory.UsedPercent), s.Memory.UsedPercent))

	if s.Disk != nil {
		output.WriteString("\n╔═══════════════════════════════════════╗\n")
		output.WriteString("║            DISK USAGE                 ║\n")
		output.WriteString("╚═══════════════════════════════════════╝\n")
		output.WriteString(fmt.Sprintf("Total: %.2f GB\n", s.Disk.TotalGB))
		output.WriteString(fmt.Sprintf("Used : %.2f GB (%.2f%%)\n", s.Disk.UsedGB, s.Disk.UsedPercent))
		output.WriteString(fmt.Sprintf("Free : %.2f GB\n", s.Disk.FreeGB))
		output.WriteString(fmt.Sprintf("Disk : [%s] %.2f%%\n", usageBar(s.Disk.UsedPercent), s.Disk.UsedPercent))
	}

	if len(s.DiskIO) > 0 {
		output.WriteString("\n╔═══════════════════════════════════════╗\n")
		output.WriteString("║         DISK I/O (Read/Write)         ║\n")
		output.WriteString("╚═══════════════════════════════════════╝\n")
		for _, io := range s.DiskIO {
			output.WriteString(fmt.Sprintf("%s: Read: %.2f MB/s | Write: %.2f MB/s\n",
				io.Name, io.ReadMBps, io.WriteMBps))
		}
	}

	if len(s.Network.Interfaces) > 0 {
		output.WriteString("\n╔═══════════════════════════════════════╗\n")
		output.WriteString("║         NETWORK (Recv/Send)           ║\n")
		output.WriteString("╚═══════════════════════════════════════╝\n")
		for _, nc := range s.Network.Interfaces {
			output.WriteString(fmt.Sprintf("%s: Rx: %.2f MB/s | Tx: %.2f MB/s\n",
				nc.Name, nc.RxMBps, nc.TxMBps))
		}
		output.WriteString(fmt.Sprintf("Total: Rx: %.2f MB/s | Tx: %.2f MB/s\n",
			s.Network.TotalRxMBps, s.Network.TotalTxMBps))
	}

	return output.String()
}

func usageBar(percent float64) string {
	barLength := int(percent / 5)
	if barLength > 20 {
		barLength = 20
	}
	if barLength < 0 {
		barLength = 0
	}
	return strings.Repeat("█", barLength) + strings.Repeat("░", 20-barLength)
}
