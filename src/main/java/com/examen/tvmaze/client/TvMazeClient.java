package com.examen.tvmaze.client;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

@Component
public class TvMazeClient {

    private final RestClient restClient;

    public TvMazeClient(RestClient tvMazeRestClient) {
        this.restClient = tvMazeRestClient;
    }

    public List<JsonNode> searchShows(String query) {
        JsonNode body = read(restClient.get()
                .uri(builder -> builder.path("/search/shows").queryParam("q", query).build()));
        if (body == null || body.isNull()) {
            return List.of();
        }
        if (!body.isArray()) {
            throw new TvMazeClientException("La respuesta de búsqueda de TV Maze no es un arreglo");
        }
        List<JsonNode> hits = new ArrayList<>();
        body.forEach(hits::add);
        return List.copyOf(hits);
    }

    JsonNode read(RestClient.RequestHeadersSpec<?> request) {
        try {
            return request.exchange((httpRequest, response) -> {
                if (response.getStatusCode().isError()) {
                    int status = response.getStatusCode().value();
                    throw new TvMazeClientException(status, "TV Maze respondió con estado " + status);
                }
                return response.bodyTo(JsonNode.class);
            });
        } catch (TvMazeClientException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new TvMazeClientException("No fue posible consultar TV Maze", exception);
        }
    }
}
