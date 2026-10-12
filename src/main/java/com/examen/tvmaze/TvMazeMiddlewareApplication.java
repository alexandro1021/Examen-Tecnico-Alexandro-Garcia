package com.examen.tvmaze;

import com.examen.tvmaze.config.TvMazeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TvMazeProperties.class)
public class TvMazeMiddlewareApplication {

    public static void main(String[] args) {
        SpringApplication.run(TvMazeMiddlewareApplication.class, args);
    }
}
