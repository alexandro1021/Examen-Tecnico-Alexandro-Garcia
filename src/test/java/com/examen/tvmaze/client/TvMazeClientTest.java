package com.examen.tvmaze.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class TvMazeClientTest {

    private MockWebServer server;
    private TvMazeClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        String baseUrl = server.url("/").toString().replaceAll("/$", "");
        client = new TvMazeClient(RestClient.builder().baseUrl(baseUrl).build());
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void searchesShowsWithTheQueryParameter() throws Exception {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"show\":{\"id\":1,\"name\":\"Girls\"}}]"));

        var hits = client.searchShows("girls");

        assertThat(hits).hasSize(1);
        assertThat(hits.getFirst().path("show").path("name").asString()).isEqualTo("Girls");
        var recorded = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recorded.getPath()).isEqualTo("/search/shows?q=girls");
    }

    @Test
    void returnsAnEmptyListWhenTvMazeHasNoMatches() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        assertThat(client.searchShows("nada")).isEmpty();
    }

    @Test
    void failsWhenTvMazeReturnsAnError() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("nope"));

        assertThatThrownBy(() -> client.searchShows("girls"))
                .isInstanceOf(TvMazeClientException.class)
                .hasMessageContaining("500");
    }
}
