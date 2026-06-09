package com.vladopag.resourcelyi.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vladopag.resourcelyi.support.AppVersion;

@RestController
public class HealthController {

    private final AppVersion appVersion;

    public HealthController(AppVersion appVersion) {
        this.appVersion = appVersion;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("status", "ok");
        body.put("version", appVersion.version());
        body.put("displayVersion", appVersion.displayVersion());
        return body;
    }
}
