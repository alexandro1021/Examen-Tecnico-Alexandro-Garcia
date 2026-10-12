package com.examen.tvmaze.service;

import com.examen.tvmaze.dto.ShowSearchItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

public final class ShowMapper {

    private ShowMapper() {
    }

    public static Optional<ShowSearchItem> toSearchItem(JsonNode hit) {
        if (hit == null || !hit.isObject()) {
            return Optional.empty();
        }
        JsonNode show = hit.path("show");
        if (!show.isObject() || !show.path("id").isNumber()) {
            return Optional.empty();
        }
        return Optional.of(new ShowSearchItem(
                show.path("id").asInt(),
                textOrNull(show, "name"),
                channel(show),
                textOrNull(show, "summary"),
                genres(show)));
    }

    static String channel(JsonNode show) {
        String network = nestedName(show.path("network"));
        if (network != null) {
            return network;
        }
        return nestedName(show.path("webChannel"));
    }

    private static String nestedName(JsonNode node) {
        if (!node.isObject()) {
            return null;
        }
        return textOrNull(node, "name");
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull() || !value.isString()) {
            return null;
        }
        String text = value.asString();
        return text.isBlank() ? null : text;
    }

    private static List<String> genres(JsonNode show) {
        JsonNode genres = show.path("genres");
        if (!genres.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        genres.forEach(genre -> {
            if (genre.isString() && !genre.asString().isBlank()) {
                values.add(genre.asString());
            }
        });
        return List.copyOf(values);
    }
}
