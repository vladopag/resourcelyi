package com.vladopag.resourcelyi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.vladopag.resourcelyi.support.AppVersion;

@Component
public class StartupLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    private final AppVersion appVersion;
    private final Environment environment;

    public StartupLogger(AppVersion appVersion, Environment environment) {
        this.appVersion = appVersion;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        String port = environment.getProperty("local.server.port", "8080");
        log.info(
                "Resourcelyi {} started — http://localhost:{}/ (API: /api/health, /api/metrics)",
                appVersion.displayVersion(),
                port);
    }
}
