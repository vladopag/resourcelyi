package monitor

import "runtime"

// DefaultDiskPath returns the OS-appropriate default disk path to monitor.
func DefaultDiskPath() string {
	switch runtime.GOOS {
	case "windows":
		return "C:\\"
	default:
		return "/"
	}
}
