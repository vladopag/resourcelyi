package monitor

import (
	"fmt"
	"time"

	"github.com/shirou/gopsutil/v3/disk"
	"github.com/shirou/gopsutil/v3/net"
)

// Monitor holds the resource monitoring functionality.
type Monitor struct {
	lastCPUCheck     time.Time
	lastIOCounters   map[string]disk.IOCountersStat
	lastIOCheckTime  time.Time
	diskPath         string
	lastNetCounters  map[string]net.IOCountersStat
	lastNetCheckTime time.Time
}

// NewMonitor creates a new Monitor instance.
func NewMonitor(diskPath string) *Monitor {
	return &Monitor{
		lastCPUCheck:     time.Now(),
		lastIOCounters:   make(map[string]disk.IOCountersStat),
		lastIOCheckTime:  time.Now(),
		diskPath:         diskPath,
		lastNetCounters:  make(map[string]net.IOCountersStat),
		lastNetCheckTime: time.Now(),
	}
}

func formatDuration(d time.Duration) string {
	hours := int(d.Hours())
	minutes := int(d.Minutes()) % 60
	seconds := int(d.Seconds()) % 60
	days := hours / 24
	hours = hours % 24

	if days > 0 {
		return fmt.Sprintf("%dd %dh %dm %ds", days, hours, minutes, seconds)
	}
	if hours > 0 {
		return fmt.Sprintf("%dh %dm %ds", hours, minutes, seconds)
	}
	return fmt.Sprintf("%dm %ds", minutes, seconds)
}

// DisplayCPUUsage collects metrics and renders them to the terminal.
func (m *Monitor) DisplayCPUUsage() {
	snap, err := m.Collect()
	if err != nil {
		fmt.Printf("Error collecting metrics: %v\n", err)
		return
	}
	m.render(FormatSnapshot(snap))
}

// render clears the screen and prints the full dashboard in one pass.
func (m *Monitor) render(content string) {
	if InteractiveTTY() {
		fmt.Print("\033[H\033[2J")
	}
	fmt.Print(content)
}
