package com.examen.tvmaze.persistence;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
public class ShowComment {

    @Id
    private String id;

    @Indexed
    private Integer showId;

    private String comment;

    private int rating;

    private Instant createdAt;

    protected ShowComment() {
    }

    public ShowComment(Integer showId, String comment, int rating, Instant createdAt) {
        this.showId = showId;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Integer getShowId() {
        return showId;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
