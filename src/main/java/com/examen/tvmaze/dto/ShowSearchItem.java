package com.examen.tvmaze.dto;

import java.util.List;

public record ShowSearchItem(
        int id,
        String name,
        String channel,
        String summary,
        List<String> genres,
        List<CommentView> comments) {

    public ShowSearchItem {
        genres = genres == null ? List.of() : List.copyOf(genres);
        comments = comments == null ? List.of() : List.copyOf(comments);
    }

    public ShowSearchItem withComments(List<CommentView> comments) {
        return new ShowSearchItem(id, name, channel, summary, genres, comments);
    }
}
