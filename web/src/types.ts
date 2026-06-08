export interface Snapshot {
  timestamp: string;
  system: SystemInfo;
  cpu: CPUStats;
  memory: MemoryStats;
  disk?: DiskStats;
  diskIO: DiskIOStat[];
  network: NetworkStats;
}

export interface SystemInfo {
  hostname: string;
  os: string;
  osVersion: string;
  osFamily: string;
  kernel: string;
  arch: string;
  uptime: string;
  uptimeSeconds: number;
  bootTime: string;
  runtimeEnvironment: string;
}

export interface CPUStats {
  model: string;
  vendor: string;
  physicalCores: number;
  logicalCores: number;
  totalPercent: number;
  perCorePercent: number[];
  load1: number;
  load5: number;
  load15: number;
}

export interface MemoryStats {
  totalGB: number;
  usedGB: number;
  freeGB: number;
  usedPercent: number;
}

export interface DiskStats {
  path: string;
  totalGB: number;
  usedGB: number;
  freeGB: number;
  usedPercent: number;
}

export interface DiskIOStat {
  name: string;
  readMBps: number;
  writeMBps: number;
}

export interface NetworkInterfaceStat {
  name: string;
  rxMBps: number;
  txMBps: number;
}

export interface NetworkStats {
  interfaces: NetworkInterfaceStat[];
  totalRxMBps: number;
  totalTxMBps: number;
}
