package main

import (
	"flag"
	"fmt"
	"os"
	"os/signal"
	"runtime"
	"syscall"
	"time"

	"github.com/vladopag/resource-monitor/monitor"
)

const Version = "v1.2"

// getDefaultDiskPath returns the appropriate default disk path based on the OS
func getDefaultDiskPath() string {
	switch runtime.GOOS {
	case "windows":
		return "C:\\"
	default:
		// Linux, macOS, and other Unix-like systems
		return "/"
	}
}

func main() {
	// Command-line flags
	interval := flag.Int("i", 1, "Update interval in seconds")
	flag.IntVar(interval, "interval", 1, "Update interval in seconds")

	defaultDiskPath := getDefaultDiskPath()
	helpText := fmt.Sprintf("Disk path to monitor (default: %s)", defaultDiskPath)
	diskPath := flag.String("disk", defaultDiskPath, helpText)

	showVersion := flag.Bool("version", false, "Show version and exit")
	flag.Parse()

	if *showVersion {
		fmt.Println("Resourcelyi", Version)
		return
	}

	if *interval < 1 {
		fmt.Println("Interval must be at least 1 second")
		os.Exit(1)
	}

	// Setup signal handling for graceful shutdown
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT)

	// Add SIGTERM only on Unix-like systems
	if runtime.GOOS != "windows" {
		signal.Notify(sigChan, syscall.SIGTERM)
	}

	ticker := time.NewTicker(time.Duration(*interval) * time.Second)
	defer ticker.Stop()

	// Create monitor instance
	resourceMonitor := monitor.NewMonitor(*diskPath)

	// Enter alternate screen buffer so updates stay in place.
	fmt.Print("\033[?1049h")
	defer fmt.Print("\033[?1049l")

	// Main monitoring loop
	for {
		select {
		case <-sigChan:
			fmt.Println("\nShutting down...")
			return
		case <-ticker.C:
			resourceMonitor.DisplayCPUUsage()
		}
	}
}
