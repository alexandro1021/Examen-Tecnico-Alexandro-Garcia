package com.examen.tvmaze.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SearchCommentsIntegrationTest {

    private static final MockWebServer TV_MAZE = new MockWebServer();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @DynamicPropertySource
    static void tvMaze(DynamicPropertyRegistry registry) throws IOException {
        TV_MAZE.start();
        String baseUrl = TV_MAZE.url("/").toString().replaceAll("/$", "");
        registry.add("tvmaze.base-url", () -> baseUrl);
    }

    @AfterAll
    static void stopTvMaze() throws IOException {
        TV_MAZE.shutdown();
    }

    @LocalServerPort
    private int port;

    @Autowired
    private ShowCommentRepository showCommentRepository;

    @BeforeEach
    void clean() {
        showCommentRepository.deleteAll();
    }

    @Test
    void appendsStoredCommentsAfterTheTvMazeSearch() throws Exception {
        showCommentRepository.save(new ShowComment(139, "Muy buena", 5, Instant.parse("2026-10-12T00:00:00Z")));
        TV_MAZE.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        [{"show":{"id":139,"name":"Girls","genres":["Drama"],"summary":"<p>Comic.</p>","network":{"name":"HBO"},"webChannel":null}}]
                        """));

        HttpRequest request = HttpRequest.newBuilder(
                        URI.create("http://127.0.0.1:" + port + "/api/search?search_query=girls"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"channel\":\"HBO\"");
        assertThat(response.body()).contains("\"comment\":\"Muy buena\"");
        assertThat(response.body()).contains("\"rating\":5");
        assertThat(TV_MAZE.getRequestCount()).isEqualTo(1);
    }
}
