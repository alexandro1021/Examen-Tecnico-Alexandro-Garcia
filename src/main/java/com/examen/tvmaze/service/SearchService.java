package com.examen.tvmaze.service;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.dto.CommentView;
import com.examen.tvmaze.dto.ShowSearchItem;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final TvMazeClient tvMazeClient;
    private final CommentService commentService;

    public SearchService(TvMazeClient tvMazeClient, CommentService commentService) {
        this.tvMazeClient = tvMazeClient;
        this.commentService = commentService;
    }

    public List<ShowSearchItem> search(String query) {
        List<ShowSearchItem> items = tvMazeClient.searchShows(query).stream()
                .map(ShowMapper::toSearchItem)
                .flatMap(Optional::stream)
                .toList();
        Map<Integer, List<CommentView>> comments = commentService.findByShowIds(
                items.stream().map(ShowSearchItem::id).distinct().toList());
        return items.stream()
                .map(item -> item.withComments(comments.getOrDefault(item.id(), List.of())))
                .toList();
    }
}
