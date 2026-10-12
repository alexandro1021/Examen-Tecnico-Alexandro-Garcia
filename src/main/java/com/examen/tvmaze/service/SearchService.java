package com.examen.tvmaze.service;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.dto.ShowSearchItem;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final TvMazeClient tvMazeClient;

    public SearchService(TvMazeClient tvMazeClient) {
        this.tvMazeClient = tvMazeClient;
    }

    public List<ShowSearchItem> search(String query) {
        return tvMazeClient.searchShows(query).stream()
                .map(ShowMapper::toSearchItem)
                .flatMap(Optional::stream)
                .toList();
    }
}
