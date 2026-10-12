package com.examen.tvmaze.dto;

import java.util.List;

public record ShowSearchItem(
        int id,
        String name,
        String channel,
        String summary,
        List<String> genres) {

    public ShowSearchItem {
        genres = genres == null ? List.of() : List.copyOf(genres);
    }
}
