import type { HealthResponse, Snapshot } from "./types";

export async function fetchHealth(): Promise<HealthResponse> {
  const res = await fetch("/api/health");
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }
  return res.json() as Promise<HealthResponse>;
}

export async function fetchMetrics(): Promise<Snapshot> {
  const res = await fetch("/api/metrics");
  if (!res.ok) {
    const body = await res.text();
    throw new Error(body || `HTTP ${res.status}`);
  }
  return res.json() as Promise<Snapshot>;
}
