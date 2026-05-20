# Resourcelyi

Version: v2.0

Resourcelyi is a high-performance system resource monitoring tool written in Go. Displays real-time information about your computer's CPU, RAM, disk usage, system info, and more.

## Features

- ✅ **CPU Usage**: Monitor per-core and total CPU utilization with visual indicators
- ✅ **RAM Usage**: Monitor total, used, and free memory with a live usage bar
- 🔄 **Load Averages**: Display 1m, 5m, and 15m load averages
- ⏱️ **Real-time Updates**: Configurable refresh intervals for live monitoring
- 🚀 **Written in Go**: Fast, lightweight, and easy to deploy
- ✅ **Disk Usage**: Monitor disk total/used/free and visual bar
- ✅ **Disk I/O**: Show per-device read/write speeds (MB/s)
- 🔁 **Cross-Platform Defaults**: Auto-detects sensible disk path per-OS (Windows → `C:\`, Linux/macOS → `/`)
- ✅ **Network Statistics**: Monitor network interfaces, throughput, and per-interface usage
- ✅ **System Information**: Shows OS details, uptime, hostname, kernel version, and architecture
- ✅ **Web Dashboard**: React GUI with REST API (`/api/metrics`)

## Changelog

- v2.0 — 2026-05-19
	- Web dashboard with React and REST API (`/api/metrics`).
	- Refactored metrics collection into shared `Collect()` for CLI and API.

- v1.2 — 2026-05-15
	- Updated version and release notes.
	- Network statistics implemented (per-interface Rx/Tx and totals).
	- Disk I/O implemented (per-device read/write speeds).
	- Added cross-platform defaults and disk path flag.

## Upcoming Features (Planned)

- 🔜 **Process Information**: Display top processes by CPU and memory, with optional sorting and filtering (planned)
- 🔜 **Alerts & Notifications**: Threshold-based alerts and optional integration with external notification systems (planned)


## Prerequisites

- Go 1.21 or higher
- Node.js 18+ (for the web dashboard only)
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

## Web Dashboard (React + REST)

The GUI uses a Go REST API and a React frontend in `web/`.

### 1. Start the API server

```bash
go run ./cmd/server
```

Options:

- `-addr`: Listen address (default: `127.0.0.1:8080`)
- `-disk`: Disk path to monitor (default: OS-specific, same as CLI)

Endpoints:

- `GET /api/health` — health check
- `GET /api/metrics` — full metrics JSON snapshot

Example:

```bash
curl http://127.0.0.1:8080/api/metrics
```

### 2. Start the React dev server

In a second terminal:

```bash
cd web
npm install
npm run dev
```

Open **http://localhost:5173**. Vite proxies `/api` to the Go server on port 8080.

### 3. Production build (optional)

```bash
cd web && npm run build
```

Serve `web/dist/` with any static file server, or add `go:embed` later to ship a single binary.

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

### Disk I/O Output Example

```
╔═══════════════════════════════════════╗
║         DISK I/O (Read/Write)         ║
╚═══════════════════════════════════════╝
sda: Read: 10.24 MB/s | Write: 2.50 MB/s
nvme0n1: Read: 0.00 MB/s | Write: 0.00 MB/s
```

### Network Output Example

```
╔═══════════════════════════════════════╗
║         NETWORK (Recv/Send)           ║
╚═══════════════════════════════════════╝
eth0: Rx: 12.34 MB/s | Tx: 3.21 MB/s
wlan0: Rx: 0.12 MB/s | Tx: 0.03 MB/s
Total: Rx: 12.46 MB/s | Tx: 3.24 MB/s
```

### Notes on Interface Filtering

- The monitor reports all network interfaces by default. If you want to limit output to specific interfaces (for example `eth0` or `wlan0`), you can run the binary and pipe/grep the interface lines, or set a future `-net` filter flag (planned).



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
├── go.mod
├── main.go                 # CLI entry point
├── cmd/server/main.go      # REST API server
├── internal/server/        # HTTP handlers
├── monitor/                # Metrics collection + terminal UI
│   ├── collect.go
│   ├── display.go
│   ├── types.go
│   └── ...
├── web/                    # React dashboard (Vite)
│   └── src/
└── README.md
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the GNU General Public License v3.0 or later.