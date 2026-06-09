package com.vladopag.resourcelyi.cli;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.vladopag.resourcelyi.support.TestSnapshots;

class TerminalDisplayTest {

    @Test
    void formatIncludesMajorSections() {
        String output = TerminalDisplay.format(TestSnapshots.sample());

        assertTrue(output.contains("SYSTEM INFORMATION"));
        assertTrue(output.contains("CPU USAGE MONITORING"));
        assertTrue(output.contains("MEMORY USAGE"));
        assertTrue(output.contains("DISK USAGE"));
        assertTrue(output.contains("DISK I/O"));
        assertTrue(output.contains("NETWORK"));
        assertTrue(output.contains("test-host"));
        assertTrue(output.contains("Intel Core i7"));
    }

    @Test
    void formatShowsDockerWarningWhenContainerized() {
        var snapshot = TestSnapshots.sample();
        var dockerSystem = new com.vladopag.resourcelyi.dto.SystemInfo(
                snapshot.system().hostname(),
                snapshot.system().os(),
                snapshot.system().osVersion(),
                snapshot.system().osFamily(),
                snapshot.system().kernel(),
                snapshot.system().arch(),
                snapshot.system().uptime(),
                snapshot.system().uptimeSeconds(),
                snapshot.system().bootTime(),
                "docker");
        var dockerSnapshot = new com.vladopag.resourcelyi.dto.Snapshot(
                snapshot.timestamp(),
                dockerSystem,
                snapshot.cpu(),
                snapshot.memory(),
                snapshot.disk(),
                snapshot.diskIO(),
                snapshot.network());

        String output = TerminalDisplay.format(dockerSnapshot);

        assertTrue(output.contains("Docker container"));
    }
}
