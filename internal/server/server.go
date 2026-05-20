package server

import (
	"encoding/json"
	"net/http"

	"github.com/vladopag/resource-monitor/monitor"
)

// Server exposes metrics over HTTP.
type Server struct {
	monitor *monitor.Monitor
}

// New creates a Server for the given disk path.
func New(diskPath string) *Server {
	return &Server{monitor: monitor.NewMonitor(diskPath)}
}

// Handler returns the HTTP handler for API routes.
func (s *Server) Handler() http.Handler {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /api/health", s.handleHealth)
	mux.HandleFunc("GET /api/metrics", s.handleMetrics)
	return mux
}

func (s *Server) handleHealth(w http.ResponseWriter, _ *http.Request) {
	writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
}

func (s *Server) handleMetrics(w http.ResponseWriter, _ *http.Request) {
	snap, err := s.monitor.Collect()
	if err != nil {
		writeJSON(w, http.StatusInternalServerError, map[string]string{"error": err.Error()})
		return
	}
	writeJSON(w, http.StatusOK, snap)
}

func writeJSON(w http.ResponseWriter, status int, v any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(v)
}
