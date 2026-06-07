package monitor

import (
	"fmt"
	"os"

	"golang.org/x/term"
)

// InteractiveTTY reports whether stdout is a terminal suitable for live UI.
func InteractiveTTY() bool {
	return term.IsTerminal(int(os.Stdout.Fd()))
}

// EnableTerminalModes enters the alternate screen buffer so updates stay in place.
// The returned function restores the main screen on exit.
func EnableTerminalModes() func() {
	if !InteractiveTTY() {
		return func() {}
	}

	fmt.Print("\033[?1049h")

	return func() {
		fmt.Print("\033[?1049l")
	}
}
