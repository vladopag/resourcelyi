# Resourcelyi

Version: v3.3

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
docker build -t resourcelyi:3.3 .
docker run --rm -p 8080:8080 \
  --pid=host \
  -v /:/hostfs:ro \
  -e RESOURCELYI_DISK_PATH=/hostfs \
  resourcelyi:3.3
```

Or with Docker Compose:

```bash
docker compose up --build
```

Open **http://localhost:8080** for the dashboard. API: `http://localhost:8080/api/metrics`.

On **Linux**, `pid: host` and the `/hostfs` volume mount help OSHI report host CPU/memory/disk instead of only the container.

### Docker on Windows — important

Docker Desktop on Windows runs a **Linux container** (Alpine). The dashboard will show **container / Linux VM** info (e.g. Alpine Linux, container hostname), **not** your Windows PC. That is expected — containers cannot see the Windows host OS through OSHI.

**To monitor your real Windows machine**, run the app **without Docker**:

1. Install **JDK 21** ([Eclipse Temurin](https://adoptium.net/)) and **Node.js 18+**
2. Build (includes React dashboard):

```bat
scripts\build-windows.bat
```

3. Run:

```bat
scripts\run-windows.bat
```

Or manually: `java -jar backend\target\resourcelyi-backend-3.3.0.jar --resourcelyi.disk-path=C:\`

Open **http://localhost:8080** — the JAR serves the API and embedded dashboard.

Use **Docker** when you want easy deployment (Linux server, or container metrics). Use the **JAR on Windows** when you want true Windows host metrics.

### Share via Docker Hub (Option B)

```bash
docker tag resourcelyi:3.3 YOUR_USER/resourcelyi:3.3
docker push YOUR_USER/resourcelyi:3.3
```

On another PC:

```bash
docker pull YOUR_USER/resourcelyi:3.3
docker run --rm -p 8080:8080 YOUR_USER/resourcelyi:3.3
```

For **Windows host metrics** on that PC, distribute the JAR instead (or build from source).

## CI/CD

GitHub Actions workflow [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml):

1. **Unit tests** — `mvn test` in `backend/`
2. **Docker build** — on PRs, builds only (no push)
3. **Docker push** — on `main`, pushes to Docker Hub as `:3.3` and `:latest`

### GitHub secrets (for Docker Hub push)

In your repo: **Settings → Secrets and variables → Actions**, add:

| Secret | Value |
|--------|--------|
| `DOCKERHUB_USERNAME` | Your Docker Hub username |
| `DOCKERHUB_TOKEN` | Docker Hub access token ([create here](https://hub.docker.com/settings/security)) |

After a push to `main`:

```bash
docker pull YOUR_USER/resourcelyi:latest
docker run --rm -p 8080:8080 YOUR_USER/resourcelyi:latest
```

Run tests locally:

```bash
cd backend && mvn test
```

## Changelog

- v3.3 — 2026-06-09
	- GitHub Actions CI/CD with unit tests and Docker Hub publish.

- v3.2 — 2026-06-08
	- Docker container detection and dashboard warning banner.
	- Windows native build/run scripts for full host metrics.

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
