package com.vladopag.resourcelyi.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vladopag.resourcelyi.service.MetricsService;
import com.vladopag.resourcelyi.support.AppVersion;

@Component
public class CliRunner implements CommandLineRunner {

    private final MetricsService metricsService;
    private final AppVersion appVersion;

    public CliRunner(MetricsService metricsService, AppVersion appVersion) {
        this.metricsService = metricsService;
        this.appVersion = appVersion;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!CliArguments.isCliMode(args)) {
            return;
        }

        if (CliArguments.hasFlag(args, "--version")) {
            System.out.println("Resourcelyi " + appVersion.displayVersion());
            return;
        }

        int intervalSeconds = CliArguments.parseInterval(args);
        if (intervalSeconds < 1) {
            System.err.println("Interval must be at least 1 second");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(TerminalRenderer::exit));

        TerminalRenderer.enter();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    var snapshot = metricsService.collect();
                    TerminalRenderer.render(TerminalDisplay.format(snapshot));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error collecting metrics: " + e.getMessage());
                }
                Thread.sleep(intervalSeconds * 1000L);
            }
        } finally {
            TerminalRenderer.exit();
            System.out.println("\nShutting down...");
        }
    }
}
