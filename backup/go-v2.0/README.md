# Archived Go implementation (v2.0)

This folder contains the original **Resourcelyi v2.0** implementation in Go (CLI, REST API, and shared metrics collection). It was moved here while the project is reimplemented with **Java Spring Boot**.

## Contents

- `main.go` — terminal CLI
- `cmd/server/` — REST API (`GET /api/metrics`, `GET /api/health`)
- `monitor/` — metrics collection and terminal formatting
- `internal/server/` — HTTP handlers
- `go.mod`, `go.sum` — Go module

## Build and run (from this directory)

```bash
cd backup/go-v2.0

# CLI
go build -o resourcelyi .
./resourcelyi

# REST API (for the React app in ../../web)
go build -o resourcelyi-server ./cmd/server
./resourcelyi-server -addr 127.0.0.1:8080
```

The React dashboard at `../../web` expects the same JSON shape from `/api/metrics` when pointing Vite’s proxy at port 8080.
