import type { Snapshot } from "./types";

export async function fetchMetrics(): Promise<Snapshot> {
  const res = await fetch("/api/metrics");
  if (!res.ok) {
    const body = await res.text();
    throw new Error(body || `HTTP ${res.status}`);
  }
  return res.json() as Promise<Snapshot>;
}
