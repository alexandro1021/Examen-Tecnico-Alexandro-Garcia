package com.examen.tvmaze.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tvmaze")
public record TvMazeProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {
}
