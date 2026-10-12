package com.examen.tvmaze.service;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.exception.ShowNotFoundException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class ShowService {

    private final TvMazeClient tvMazeClient;

    public ShowService(TvMazeClient tvMazeClient) {
        this.tvMazeClient = tvMazeClient;
    }

    public JsonNode getShow(int showId) {
        return tvMazeClient.findShow(showId).orElseThrow(() -> new ShowNotFoundException(showId));
    }
}
