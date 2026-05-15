# Resourcelyi

Version: v1.1

Resourcelyi is a high-performance system resource monitoring tool written in Go. Displays real-time information about your computer's CPU, RAM, disk usage, and more.

## Features

- ✅ **CPU Usage**: Monitor per-core and total CPU utilization with visual indicators
- ✅ **RAM Usage**: Monitor total, used, and free memory with a live usage bar
- 🔄 **Load Averages**: Display 1m, 5m, and 15m load averages
- ⏱️ **Real-time Updates**: Configurable refresh intervals for live monitoring
- 🚀 **Written in Go**: Fast, lightweight, and easy to deploy
- ✅ **Disk Usage**: Monitor disk total/used/free and visual bar
- ✅ **Disk I/O**: Show per-device read/write speeds (MB/s)
- 🔁 **Cross-Platform Defaults**: Auto-detects sensible disk path per-OS (Windows → `C:\`, Linux/macOS → `/`)

## Upcoming Features (Planned)

- 🔜 **Network Statistics**: Monitor network interfaces, throughput, and per-interface usage (planned)
- 🔜 **Process Information**: Display top processes by CPU and memory, with optional sorting and filtering (planned)
- 🔜 **System Information**: Show OS details, uptime, and hardware information such as temperature and sensors (planned)
- 🔜 **Alerts & Notifications**: Threshold-based alerts and optional integration with external notification systems (planned)


## Prerequisites

- Go 1.21 or higher
- Linux, macOS, or Windows

## Installation

### Clone the Repository
```bash
git clone https://github.com/yourusername/resource-monitor.git
cd resource-monitor
```

### Build the Application
```bash
go build -o resourcelyi
```

### Download Dependencies
The application uses `gopsutil` for system monitoring. Dependencies will be automatically downloaded when you build:
```bash
go mod download
```

## Usage

Run Resourcelyi with default settings (1-second refresh interval):
```bash
./resourcelyi
```

### Command Line Options
- `-i, --interval`: Set update interval in seconds (default: 1)
- `-disk`: Path to monitor for disk usage (default auto-detected per-OS)
- `-version`: Print the Resourcelyi version and exit

Example - Update every 2 seconds:
```bash
./resourcelyi -i 2
```

Example - Monitor a specific disk path (Windows):
```bash
./resourcelyi -disk C:\\
```

Example - Monitor root filesystem (Linux/macOS):
```bash
./resourcelyi -disk /
```

### Output Example
```
╔═══════════════════════════════════════╗
║         CPU USAGE MONITORING          ║
╚═══════════════════════════════════════╝

System: Intel Core i7 Intel
Cores: 8 (Logical: 8)

├─ Total CPU Usage: 12.34%
├─ Load Averages: 1.23, 0.98, 0.87 (1m, 5m, 15m)

├─ Per-CPU Usage:
│  CPU 0: [██░░░░░░░░░░░░░░░░░░] 15.42%
│  CPU 1: [█░░░░░░░░░░░░░░░░░░░░] 8.15%
...

├─ Last Updated: 2026-05-12 10:30:45

╔═══════════════════════════════════════╗
║            MEMORY USAGE               ║
╚═══════════════════════════════════════╝
Total: 31.95 GB
Used : 13.27 GB (41.50%)
Free : 18.68 GB
Memory: [████████░░░░░░░░░░░░░░░░] 41.50%
```

## Building for Different Platforms

### Linux
```bash
go build -o resourcelyi
```

### macOS
```bash
GOOS=darwin GOARCH=amd64 go build -o resourcelyi-darwin
```

### Windows
```bash
GOOS=windows GOARCH=amd64 go build -o resourcelyi.exe
```

## Project Structure
```
resource-monitor/
├── go.mod                 # Go module definition
├── main.go               # Entry point and CLI handling
├── monitor/
│   └── monitor.go        # Core monitoring functionality
└── README.md             # This file
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the GNU General Public License v3.0 or later.