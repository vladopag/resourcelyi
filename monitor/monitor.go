package monitor

import (
	"fmt"
	"strings"
	"time"

	"github.com/shirou/gopsutil/v3/cpu"
	"github.com/shirou/gopsutil/v3/disk"
	"github.com/shirou/gopsutil/v3/load"
	"github.com/shirou/gopsutil/v3/mem"
)

// Monitor holds the resource monitoring functionality
type Monitor struct {
	lastCPUCheck    time.Time
	lastLines       int
	lastIOCounters  map[string]disk.IOCountersStat
	lastIOCheckTime time.Time
	diskPath        string
}

// NewMonitor creates a new Monitor instance
func NewMonitor(diskPath string) *Monitor {
	return &Monitor{
		lastCPUCheck:    time.Now(),
		lastLines:       0,
		lastIOCounters:  make(map[string]disk.IOCountersStat),
		lastIOCheckTime: time.Now(),
		diskPath:        diskPath,
	}
}

// DisplayCPUUsage displays the current CPU usage
func (m *Monitor) DisplayCPUUsage() {
	// Get per-CPU usage
	perCPUPercent, err := cpu.Percent(time.Second, true)
	if err != nil {
		fmt.Printf("Error getting CPU usage: %v\n", err)
		return
	}

	// Get overall CPU usage
	totalCPUPercent, err := cpu.Percent(0, false)
	if err != nil {
		fmt.Printf("Error getting total CPU usage: %v\n", err)
		return
	}

	// Get load average.
	loadAvg, err := load.Avg()
	if err != nil {
		fmt.Printf("Error getting load average: %v\n", err)
		return
	}

	// Get CPU info
	cpuInfo, err := cpu.Info()
	if err != nil {
		fmt.Printf("Error getting CPU info: %v\n", err)
		return
	}

	// Build output string instead of printing incrementally
	var output strings.Builder

	output.WriteString("╔═══════════════════════════════════════╗\n")
	output.WriteString("║         CPU USAGE MONITORING          ║\n")
	output.WriteString("╚═══════════════════════════════════════╝\n")
	output.WriteString(fmt.Sprintf("\nSystem: %s %s\n", cpuInfo[0].ModelName, cpuInfo[0].VendorID))
	output.WriteString(fmt.Sprintf("Cores: %d (Logical: %d)\n", len(cpuInfo), len(perCPUPercent)))

	output.WriteString(fmt.Sprintf("\n├─ Total CPU Usage: %.2f%%\n", totalCPUPercent[0]))
	output.WriteString(fmt.Sprintf("├─ Load Averages: %.2f, %.2f, %.2f (1m, 5m, 15m)\n",
		loadAvg.Load1, loadAvg.Load5, loadAvg.Load15))
	output.WriteString("\n├─ Per-CPU Usage:\n")

	for i, percent := range perCPUPercent {
		barLength := int(percent / 5) // 20 characters max
		bar := ""
		for j := 0; j < barLength; j++ {
			bar += "█"
		}
		for j := barLength; j < 20; j++ {
			bar += "░"
		}
		output.WriteString(fmt.Sprintf("│  CPU %2d: [%s] %.2f%%\n", i, bar, percent))
	}

	output.WriteString(fmt.Sprintf("\n└─ Last Updated: %s\n", time.Now().Format("2006-01-02 15:04:05")))

	// Memory (RAM) statistics
	memStats, err := mem.VirtualMemory()
	if err != nil {
		fmt.Printf("Error getting memory info: %v\n", err)
		return
	}

	// Human-readable sizes in GB
	totalGB := float64(memStats.Total) / 1024.0 / 1024.0 / 1024.0
	usedGB := float64(memStats.Used) / 1024.0 / 1024.0 / 1024.0

	output.WriteString("\n╔═══════════════════════════════════════╗\n")
	output.WriteString("║            MEMORY USAGE               ║\n")
	output.WriteString("╚═══════════════════════════════════════╝\n")
	output.WriteString(fmt.Sprintf("Total: %.2f GB\n", totalGB))
	output.WriteString(fmt.Sprintf("Used : %.2f GB (%.2f%%)\n", usedGB, memStats.UsedPercent))
	output.WriteString(fmt.Sprintf("Free : %.2f GB\n", float64(memStats.Free)/1024.0/1024.0/1024.0))

	// Memory usage bar (20 chars)
	memBarLen := int(memStats.UsedPercent / 5)
	memBar := ""
	for j := 0; j < memBarLen; j++ {
		memBar += "█"
	}
	for j := memBarLen; j < 20; j++ {
		memBar += "░"
	}
	output.WriteString(fmt.Sprintf("Memory: [%s] %.2f%%\n", memBar, memStats.UsedPercent))

	// Disk Usage Statistics
	diskUsage, err := disk.Usage(m.diskPath)
	if err != nil {
		fmt.Printf("Error getting disk usage: %v\n", err)
	} else {
		totalGB := float64(diskUsage.Total) / 1024.0 / 1024.0 / 1024.0
		usedGB := float64(diskUsage.Used) / 1024.0 / 1024.0 / 1024.0
		freeGB := float64(diskUsage.Free) / 1024.0 / 1024.0 / 1024.0

		diskBarLen := int(diskUsage.UsedPercent / 5)
		diskBar := ""
		for j := 0; j < diskBarLen; j++ {
			diskBar += "█"
		}
		for j := diskBarLen; j < 20; j++ {
			diskBar += "░"
		}

		output.WriteString("\n╔═══════════════════════════════════════╗\n")
		output.WriteString("║            DISK USAGE                 ║\n")
		output.WriteString("╚═══════════════════════════════════════╝\n")
		output.WriteString(fmt.Sprintf("Total: %.2f GB\n", totalGB))
		output.WriteString(fmt.Sprintf("Used : %.2f GB (%.2f%%)\n", usedGB, diskUsage.UsedPercent))
		output.WriteString(fmt.Sprintf("Free : %.2f GB\n", freeGB))
		output.WriteString(fmt.Sprintf("Disk : [%s] %.2f%%\n", diskBar, diskUsage.UsedPercent))
	}

	// Disk I/O Statistics
	ioCounters, err := disk.IOCounters()
	if err != nil {
		fmt.Printf("Error getting disk I/O: %v\n", err)
	} else {
		output.WriteString("\n╔═══════════════════════════════════════╗\n")
		output.WriteString("║         DISK I/O (Read/Write)         ║\n")
		output.WriteString("╚═══════════════════════════════════════╝\n")

		timeDiff := time.Since(m.lastIOCheckTime).Seconds()
		if timeDiff > 0 {
			for name, currentIO := range ioCounters {
				var readMBps, writeMBps float64

				if lastIO, exists := m.lastIOCounters[name]; exists {
					readBytes := currentIO.ReadBytes - lastIO.ReadBytes
					writeBytes := currentIO.WriteBytes - lastIO.WriteBytes

					readMBps = float64(readBytes) / timeDiff / 1024.0 / 1024.0
					writeMBps = float64(writeBytes) / timeDiff / 1024.0 / 1024.0
				}

				output.WriteString(fmt.Sprintf("%s: Read: %.2f MB/s | Write: %.2f MB/s\n", name, readMBps, writeMBps))
			}
		}

		m.lastIOCounters = ioCounters
		m.lastIOCheckTime = time.Now()
	}

	// Move cursor to top-left and clear the screen before printing output.
	fmt.Print("\033[H\033[J")
	fmt.Print(output.String())
	m.lastLines = strings.Count(output.String(), "\n")
}
