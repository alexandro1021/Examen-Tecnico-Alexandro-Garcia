package com.examen.tvmaze.persistence;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shows")
public class CachedShow {

    @Id
    private Integer id;

    private String payload;

    private Instant cachedAt;

    protected CachedShow() {
    }

    public CachedShow(Integer id, String payload, Instant cachedAt) {
        this.id = id;
        this.payload = payload;
        this.cachedAt = cachedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCachedAt() {
        return cachedAt;
    }
}
