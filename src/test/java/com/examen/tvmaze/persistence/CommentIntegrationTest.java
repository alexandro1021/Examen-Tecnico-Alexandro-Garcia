package com.examen.tvmaze.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
class CommentIntegrationTest {

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

    @Autowired
    private ShowCacheRepository showCacheRepository;

    @BeforeEach
    void clean() {
        showCommentRepository.deleteAll();
        showCacheRepository.deleteAll();
    }

    @Test
    void savesACommentLinkedToTheShow() throws Exception {
        TV_MAZE.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":139,\"name\":\"Girls\"}"));

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/comments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"show_id\":139,\"comment\":\"Muy buena\",\"rating\":4}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.body()).contains("\"status\":\"CREATED\"");
        assertThat(showCommentRepository.findByShowIdOrderByCreatedAtAsc(139))
                .singleElement()
                .satisfies(comment -> {
                    assertThat(comment.getComment()).isEqualTo("Muy buena");
                    assertThat(comment.getRating()).isEqualTo(4);
                });
    }
}
