package main

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/vladopag/resource-monitor/internal/server"
	"github.com/vladopag/resource-monitor/monitor"
)

const version = "v2.0"

func main() {
	addr := flag.String("addr", "127.0.0.1:8080", "Listen address (host:port)")
	diskPath := flag.String("disk", monitor.DefaultDiskPath(), "Disk path to monitor")
	showVersion := flag.Bool("version", false, "Show version and exit")
	flag.Parse()

	if *showVersion {
		fmt.Println("Resourcelyi server", version)
		return
	}

	srv := server.New(*diskPath)
	log.Printf("Resourcelyi %s API listening on http://%s", version, *addr)
	log.Printf("  GET http://%s/api/metrics", *addr)
	log.Printf("  GET http://%s/api/health", *addr)

	if err := http.ListenAndServe(*addr, srv.Handler()); err != nil {
		fmt.Fprintf(os.Stderr, "server error: %v\n", err)
		os.Exit(1)
	}
}
