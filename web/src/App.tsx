import { useCallback, useEffect, useState } from "react";
import { fetchMetrics } from "./api";
import type { Snapshot } from "./types";

const POLL_MS = 1000;

function UsageBar({ label, percent }: { label: string; percent: number }) {
  const clamped = Math.min(100, Math.max(0, percent));
  return (
    <div className="bar-row">
      <div className="bar-label">
        <span>{label}</span>
        <span>{clamped.toFixed(2)}%</span>
      </div>
      <div className="bar-track">
        <div className="bar-fill" style={{ width: `${clamped}%` }} />
      </div>
    </div>
  );
}

export default function App() {
  const [data, setData] = useState<Snapshot | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    try {
      const snap = await fetchMetrics();
      setData(snap);
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to fetch metrics");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
    const id = setInterval(load, POLL_MS);
    return () => clearInterval(id);
  }, [load]);

  return (
    <div className="app">
      <header className="header">
        <div>
          <h1>Resourcelyi</h1>
          <p>System resource dashboard</p>
        </div>
        <div className="status">
          {loading && !data && !error && <div>Connecting…</div>}
          {data && (
            <div>
              Last update
              <br />
              {new Date(data.timestamp).toLocaleString()}
            </div>
          )}
        </div>
      </header>

      {data && (
        <>
          <section className="grid">
            <article className="card">
              <h2>System</h2>
              <div className="kv">
                <div>
                  <span>Hostname</span>
                  <span>{data.system.hostname}</span>
                </div>
                <div>
                  <span>OS</span>
                  <span>
                    {data.system.os} {data.system.osVersion} ({data.system.osFamily})
                  </span>
                </div>
                <div>
                  <span>Kernel</span>
                  <span>{data.system.kernel}</span>
                </div>
                <div>
                  <span>Arch</span>
                  <span>{data.system.arch}</span>
                </div>
                <div>
                  <span>Uptime</span>
                  <span>{data.system.uptime}</span>
                </div>
                <div>
                  <span>Boot time</span>
                  <span>{data.system.bootTime}</span>
                </div>
              </div>
            </article>

            <article className="card">
              <h2>CPU</h2>
              <div className="kv">
                <div>
                  <span>Model</span>
                  <span>
                    {data.cpu.model} {data.cpu.vendor}
                  </span>
                </div>
                <div>
                  <span>Cores</span>
                  <span>
                    {data.cpu.physicalCores} physical / {data.cpu.logicalCores} logical
                  </span>
                </div>
                <div>
                  <span>Load (1m / 5m / 15m)</span>
                  <span>
                    {data.cpu.load1.toFixed(2)} / {data.cpu.load5.toFixed(2)} /{" "}
                    {data.cpu.load15.toFixed(2)}
                  </span>
                </div>
              </div>
              <UsageBar label="Total CPU" percent={data.cpu.totalPercent} />
              <div className="core-list">
                {data.cpu.perCorePercent.map((pct, i) => (
                  <UsageBar key={i} label={`CPU ${i}`} percent={pct} />
                ))}
              </div>
            </article>
          </section>

          <section className="grid two" style={{ marginTop: "1rem" }}>
            <article className="card">
              <h2>Memory</h2>
              <div className="kv">
                <div>
                  <span>Total</span>
                  <span>{data.memory.totalGB.toFixed(2)} GB</span>
                </div>
                <div>
                  <span>Used</span>
                  <span>
                    {data.memory.usedGB.toFixed(2)} GB ({data.memory.usedPercent.toFixed(2)}%)
                  </span>
                </div>
                <div>
                  <span>Free</span>
                  <span>{data.memory.freeGB.toFixed(2)} GB</span>
                </div>
              </div>
              <UsageBar label="Memory" percent={data.memory.usedPercent} />
            </article>

            {data.disk && (
              <article className="card">
                <h2>Disk ({data.disk.path})</h2>
                <div className="kv">
                  <div>
                    <span>Total</span>
                    <span>{data.disk.totalGB.toFixed(2)} GB</span>
                  </div>
                  <div>
                    <span>Used</span>
                    <span>
                      {data.disk.usedGB.toFixed(2)} GB ({data.disk.usedPercent.toFixed(2)}%)
                    </span>
                  </div>
                  <div>
                    <span>Free</span>
                    <span>{data.disk.freeGB.toFixed(2)} GB</span>
                  </div>
                </div>
                <UsageBar label="Disk" percent={data.disk.usedPercent} />
              </article>
            )}
          </section>

          {data.diskIO.length > 0 && (
            <section className="card" style={{ marginTop: "1rem" }}>
              <h2>Disk I/O</h2>
              <table className="table">
                <thead>
                  <tr>
                    <th>Device</th>
                    <th>Read (MB/s)</th>
                    <th>Write (MB/s)</th>
                  </tr>
                </thead>
                <tbody>
                  {data.diskIO.map((d) => (
                    <tr key={d.name}>
                      <td>{d.name}</td>
                      <td>{d.readMBps.toFixed(2)}</td>
                      <td>{d.writeMBps.toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          )}

          {data.network.interfaces.length > 0 && (
            <section className="card" style={{ marginTop: "1rem" }}>
              <h2>Network</h2>
              <table className="table">
                <thead>
                  <tr>
                    <th>Interface</th>
                    <th>Rx (MB/s)</th>
                    <th>Tx (MB/s)</th>
                  </tr>
                </thead>
                <tbody>
                  {data.network.interfaces.map((n) => (
                    <tr key={n.name}>
                      <td>{n.name}</td>
                      <td>{n.rxMBps.toFixed(2)}</td>
                      <td>{n.txMBps.toFixed(2)}</td>
                    </tr>
                  ))}
                  <tr>
                    <td>
                      <strong>Total</strong>
                    </td>
                    <td>
                      <strong>{data.network.totalRxMBps.toFixed(2)}</strong>
                    </td>
                    <td>
                      <strong>{data.network.totalTxMBps.toFixed(2)}</strong>
                    </td>
                  </tr>
                </tbody>
              </table>
            </section>
          )}
        </>
      )}

      {!data && !loading && error && (
        <div className="error-panel">
          <p className="status error">{error}</p>
          <p>
            Start the Spring Boot API server:{" "}
            <code>cd backend && mvn spring-boot:run</code>
          </p>
        </div>
      )}
    </div>
  );
}
