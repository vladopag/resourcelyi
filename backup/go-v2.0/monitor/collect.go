package monitor

import (
	"fmt"
	"runtime"
	"sort"
	"time"

	"github.com/shirou/gopsutil/v3/cpu"
	"github.com/shirou/gopsutil/v3/disk"
	"github.com/shirou/gopsutil/v3/host"
	"github.com/shirou/gopsutil/v3/load"
	"github.com/shirou/gopsutil/v3/mem"
	"github.com/shirou/gopsutil/v3/net"
)

// Collect gathers a point-in-time snapshot of system metrics.
func (m *Monitor) Collect() (*Snapshot, error) {
	perCPUPercent, err := cpu.Percent(time.Second, true)
	if err != nil {
		return nil, fmt.Errorf("cpu percent: %w", err)
	}

	totalCPUPercent, err := cpu.Percent(0, false)
	if err != nil {
		return nil, fmt.Errorf("cpu total: %w", err)
	}

	loadAvg, err := load.Avg()
	if err != nil {
		return nil, fmt.Errorf("load average: %w", err)
	}

	cpuInfo, err := cpu.Info()
	if err != nil {
		return nil, fmt.Errorf("cpu info: %w", err)
	}

	hostInfo, err := host.Info()
	if err != nil {
		return nil, fmt.Errorf("host info: %w", err)
	}

	memStats, err := mem.VirtualMemory()
	if err != nil {
		return nil, fmt.Errorf("memory: %w", err)
	}

	if hostInfo.Platform == "" {
		hostInfo.Platform = hostInfo.OS
	}

	model := ""
	vendor := ""
	physicalCores := len(cpuInfo)
	if len(cpuInfo) > 0 {
		model = cpuInfo[0].ModelName
		vendor = cpuInfo[0].VendorID
	}

	snap := &Snapshot{
		Timestamp: time.Now().Format(time.RFC3339),
		System: SystemInfo{
			Hostname:      hostInfo.Hostname,
			OS:            hostInfo.Platform,
			OSVersion:     hostInfo.PlatformVersion,
			OSFamily:      hostInfo.PlatformFamily,
			Kernel:        hostInfo.KernelVersion,
			Arch:          runtime.GOARCH,
			Uptime:        formatDuration(time.Duration(hostInfo.Uptime) * time.Second),
			UptimeSeconds: hostInfo.Uptime,
			BootTime:      time.Unix(int64(hostInfo.BootTime), 0).Format("2006-01-02 15:04:05"),
		},
		CPU: CPUStats{
			Model:          model,
			Vendor:         vendor,
			PhysicalCores:  physicalCores,
			LogicalCores:   len(perCPUPercent),
			TotalPercent:   totalCPUPercent[0],
			PerCorePercent: perCPUPercent,
			Load1:          loadAvg.Load1,
			Load5:          loadAvg.Load5,
			Load15:         loadAvg.Load15,
		},
		Memory: MemoryStats{
			TotalGB:     bytesToGB(memStats.Total),
			UsedGB:      bytesToGB(memStats.Used),
			FreeGB:      bytesToGB(memStats.Free),
			UsedPercent: memStats.UsedPercent,
		},
	}

	if diskUsage, err := disk.Usage(m.diskPath); err == nil {
		snap.Disk = &DiskStats{
			Path:        m.diskPath,
			TotalGB:     bytesToGB(diskUsage.Total),
			UsedGB:      bytesToGB(diskUsage.Used),
			FreeGB:      bytesToGB(diskUsage.Free),
			UsedPercent: diskUsage.UsedPercent,
		}
	}

	if ioCounters, err := disk.IOCounters(); err == nil {
		timeDiff := time.Since(m.lastIOCheckTime).Seconds()
		names := make([]string, 0, len(ioCounters))
		for name := range ioCounters {
			names = append(names, name)
		}
		sort.Strings(names)

		if timeDiff > 0 {
			for _, name := range names {
				currentIO := ioCounters[name]
				var readMBps, writeMBps float64
				if lastIO, exists := m.lastIOCounters[name]; exists {
					readBytes := currentIO.ReadBytes - lastIO.ReadBytes
					writeBytes := currentIO.WriteBytes - lastIO.WriteBytes
					readMBps = float64(readBytes) / timeDiff / 1024.0 / 1024.0
					writeMBps = float64(writeBytes) / timeDiff / 1024.0 / 1024.0
				}
				snap.DiskIO = append(snap.DiskIO, DiskIOStat{
					Name:      name,
					ReadMBps:  readMBps,
					WriteMBps: writeMBps,
				})
			}
		}

		m.lastIOCounters = ioCounters
		m.lastIOCheckTime = time.Now()
	}

	if netCounters, err := net.IOCounters(true); err == nil {
		timeDiffNet := time.Since(m.lastNetCheckTime).Seconds()
		if timeDiffNet > 0 {
			for _, nc := range netCounters {
				var rxMBps, txMBps float64
				if last, exists := m.lastNetCounters[nc.Name]; exists {
					rxBytes := nc.BytesRecv - last.BytesRecv
					txBytes := nc.BytesSent - last.BytesSent
					rxMBps = float64(rxBytes) / timeDiffNet / 1024.0 / 1024.0
					txMBps = float64(txBytes) / timeDiffNet / 1024.0 / 1024.0
				}
				snap.Network.Interfaces = append(snap.Network.Interfaces, NetworkInterfaceStat{
					Name:   nc.Name,
					RxMBps: rxMBps,
					TxMBps: txMBps,
				})
				snap.Network.TotalRxMBps += rxMBps
				snap.Network.TotalTxMBps += txMBps
			}
		}

		m.lastNetCounters = make(map[string]net.IOCountersStat)
		for _, nc := range netCounters {
			m.lastNetCounters[nc.Name] = nc
		}
		m.lastNetCheckTime = time.Now()
	}

	return snap, nil
}

func bytesToGB(b uint64) float64 {
	return float64(b) / 1024.0 / 1024.0 / 1024.0
}
