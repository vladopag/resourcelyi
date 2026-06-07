package com.vladopag.resourcelyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.vladopag.resourcelyi.cli.CliArguments;
import com.vladopag.resourcelyi.config.ResourcelyiProperties;

@SpringBootApplication
@EnableConfigurationProperties(ResourcelyiProperties.class)
public class ResourcelyiApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ResourcelyiApplication.class);
        if (CliArguments.isCliMode(args)) {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        app.run(args);
    }
}
