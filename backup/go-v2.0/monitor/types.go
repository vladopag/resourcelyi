package monitor

// Snapshot is the JSON payload returned by the REST API.
type Snapshot struct {
	Timestamp string       `json:"timestamp"`
	System    SystemInfo   `json:"system"`
	CPU       CPUStats     `json:"cpu"`
	Memory    MemoryStats  `json:"memory"`
	Disk      *DiskStats   `json:"disk,omitempty"`
	DiskIO    []DiskIOStat `json:"diskIO"`
	Network   NetworkStats `json:"network"`
}

type SystemInfo struct {
	Hostname      string `json:"hostname"`
	OS            string `json:"os"`
	OSVersion     string `json:"osVersion"`
	OSFamily      string `json:"osFamily"`
	Kernel        string `json:"kernel"`
	Arch          string `json:"arch"`
	Uptime        string `json:"uptime"`
	UptimeSeconds uint64 `json:"uptimeSeconds"`
	BootTime      string `json:"bootTime"`
}

type CPUStats struct {
	Model          string    `json:"model"`
	Vendor         string    `json:"vendor"`
	PhysicalCores  int       `json:"physicalCores"`
	LogicalCores   int       `json:"logicalCores"`
	TotalPercent   float64   `json:"totalPercent"`
	PerCorePercent []float64 `json:"perCorePercent"`
	Load1          float64   `json:"load1"`
	Load5          float64   `json:"load5"`
	Load15         float64   `json:"load15"`
}

type MemoryStats struct {
	TotalGB      float64 `json:"totalGB"`
	UsedGB       float64 `json:"usedGB"`
	FreeGB       float64 `json:"freeGB"`
	UsedPercent  float64 `json:"usedPercent"`
}

type DiskStats struct {
	Path        string  `json:"path"`
	TotalGB     float64 `json:"totalGB"`
	UsedGB      float64 `json:"usedGB"`
	FreeGB      float64 `json:"freeGB"`
	UsedPercent float64 `json:"usedPercent"`
}

type DiskIOStat struct {
	Name      string  `json:"name"`
	ReadMBps  float64 `json:"readMBps"`
	WriteMBps float64 `json:"writeMBps"`
}

type NetworkInterfaceStat struct {
	Name   string  `json:"name"`
	RxMBps float64 `json:"rxMBps"`
	TxMBps float64 `json:"txMBps"`
}

type NetworkStats struct {
	Interfaces  []NetworkInterfaceStat `json:"interfaces"`
	TotalRxMBps float64                `json:"totalRxMBps"`
	TotalTxMBps float64                `json:"totalTxMBps"`
}
