# Resourcelyi

Version: v3.1

System resource monitor with a terminal CLI, Spring Boot REST API, and React web dashboard. The original Go implementation is archived in `backup/go-v2.0/`.

## Repository layout

| Path | Description |
|------|-------------|
| `backend/` | **Spring Boot** REST API and terminal CLI (`--cli`) |
| `backup/go-v2.0/` | Archived Go v2.0 (CLI + REST API + metrics) |
| `web/` | React dashboard (Vite); polls `/api/metrics` |

## Go backup (reference)

To run the archived Go version:

```bash
cd backup/go-v2.0
go build -o resourcelyi .
go build -o resourcelyi-server ./cmd/server
./resourcelyi-server -addr 127.0.0.1:8080
```

See [backup/go-v2.0/README.md](backup/go-v2.0/README.md) for details.

## Spring Boot backend

**REST API:**

```bash
cd backend
mvn spring-boot:run
```

**Terminal CLI only:**

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments=--cli
```

See [backend/README.md](backend/README.md) for configuration and build details.

Endpoints (same contract as the Go API):

- `GET /api/health`
- `GET /api/metrics`

## Web dashboard

```bash
cd web
npm install
npm run dev
```

Start the backend on port **8080** first; Vite proxies `/api` to `http://127.0.0.1:8080`.

## Docker

Build and run a single image (API + React dashboard on port 8080):

```bash
docker build -t resourcelyi:3.1 .
docker run --rm -p 8080:8080 \
  --pid=host \
  -v /:/hostfs:ro \
  -e RESOURCELYI_DISK_PATH=/hostfs \
  resourcelyi:3.1
```

Or with Docker Compose:

```bash
docker compose up --build
```

Open **http://localhost:8080** for the dashboard. API: `http://localhost:8080/api/metrics`.

On Linux, `pid: host` and the `/hostfs` volume mount help OSHI report host CPU/memory/disk instead of only the container. Adjust `RESOURCELYI_DISK_PATH` for the filesystem you want to monitor (e.g. `/hostfs` when using the compose file).

## Changelog

- v3.1 — 2026-06-08
	- Docker packaging (multi-stage image with embedded dashboard).

- v3.0 — 2026-06-07
	- Terminal CLI mode (`--cli`) in Spring Boot backend.
	- Java Spring Boot replaces Go as the active backend.
	- Docker image with embedded React dashboard.

- v2.0 — 2026-05-19
	- Web dashboard with React and REST API.
	- Spring Boot backend in `backend/`.
	- Go code archived to `backup/go-v2.0/`.

- v1.2 — 2026-05-15
	- Network statistics, disk I/O, cross-platform disk path flag.
