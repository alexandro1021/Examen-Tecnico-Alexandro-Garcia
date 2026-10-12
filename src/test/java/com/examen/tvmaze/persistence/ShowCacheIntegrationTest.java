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
class ShowCacheIntegrationTest {

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
    private ShowCacheRepository showCacheRepository;

    @BeforeEach
    void cleanCache() {
        showCacheRepository.deleteAll();
    }

    @Test
    void readsTvMazeOnceAndThenServesMongo() throws Exception {
        TV_MAZE.enqueue(json(200, "{\"id\":139,\"name\":\"Girls\",\"runtime\":30}"));

        HttpResponse<String> first = get("/api/show/139");
        HttpResponse<String> second = get("/api/show/139");

        assertThat(first.statusCode()).isEqualTo(200);
        assertThat(second.body()).contains("\"name\":\"Girls\"").contains("\"runtime\":30");
        assertThat(TV_MAZE.getRequestCount()).isEqualTo(1);
        assertThat(showCacheRepository.findById(139)).isPresent();
    }

    @Test
    void doesNotStoreAShowThatTvMazeDoesNotHave() throws Exception {
        TV_MAZE.enqueue(json(404, "{\"status\":404}"));

        HttpResponse<String> response = get("/api/show/4");

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(showCacheRepository.findById(4)).isEmpty();
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static MockResponse json(int status, String body) {
        return new MockResponse()
                .setResponseCode(status)
                .setHeader("Content-Type", "application/json")
                .setBody(body);
    }
}
