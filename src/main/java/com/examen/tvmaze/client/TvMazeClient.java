package com.examen.tvmaze.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
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
                .uri(builder -> builder.path("/search/shows").queryParam("q", query).build()), false);
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

    public Optional<JsonNode> findShow(int showId) {
        JsonNode body = read(restClient.get().uri("/shows/{showId}", showId), true);
        if (body == null || body.isNull()) {
            return Optional.empty();
        }
        if (!body.isObject()) {
            throw new TvMazeClientException("La respuesta del show de TV Maze no es un objeto");
        }
        return Optional.of(body);
    }

    private JsonNode read(RestClient.RequestHeadersSpec<?> request, boolean emptyOnNotFound) {
        try {
            return request.exchange((httpRequest, response) -> {
                int status = response.getStatusCode().value();
                if (emptyOnNotFound && status == HttpStatus.NOT_FOUND.value()) {
                    return null;
                }
                if (response.getStatusCode().isError()) {
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
