# Resourcelyi Backend (Spring Boot)

Java Spring Boot REST API for system metrics. Compatible with the React dashboard in `../web`.

## Requirements

- **JDK 21** (full JDK with `javac` — the JRE alone is not enough)
- Maven 3.8+

### Install JDK on Ubuntu/WSL

If `mvn` fails with `release version 21 not supported`, you likely only have the JRE installed:

```bash
sudo apt install openjdk-21-jdk-headless
javac -version   # should print 21.x
mvn -version     # should show Java version: 21.x
```

## Run

### REST API (default)

```bash
cd backend
mvn spring-boot:run
```

### Terminal CLI (no web server)

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--cli
```

Options:

- `--cli` — run the live terminal dashboard instead of the HTTP server
- `-i, --interval <seconds>` — refresh interval (default: 1)
- `--version` — print version and exit
- `--resourcelyi.disk-path=/` — disk path to monitor

Example:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--cli,--interval=2
java -jar target/resourcelyi-backend-3.3.0.jar --cli
```

Press **Ctrl+C** to exit.

API:

- `GET http://127.0.0.1:8080/api/health`
- `GET http://127.0.0.1:8080/api/metrics`

## Configuration

`src/main/resources/application.yml`:

```yaml
server:
  port: 8080

resourcelyi:
  disk-path:   # empty = auto (C:\ on Windows, / on Linux/macOS)
```

Override disk path:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--resourcelyi.disk-path=/home
```

Or environment variable:

```bash
export RESOURCELYI_DISK_PATH=/
mvn spring-boot:run
```

## Build JAR

```bash
mvn package
java -jar target/resourcelyi-backend-3.3.0.jar
```

## Docker

From the repository root:

```bash
docker build -t resourcelyi:3.3 .
docker compose up --build
```

The image serves the API and React UI on port **8080**.

## With React dev server

Terminal 1:

```bash
mvn spring-boot:run
```

Terminal 2:

```bash
cd ../web && npm run dev
```

Open http://localhost:5173 (Vite proxies `/api` to port 8080).

## Stack

- Spring Boot 3.4
- [OSHI](https://github.com/oshi/oshi) for cross-platform system metrics
