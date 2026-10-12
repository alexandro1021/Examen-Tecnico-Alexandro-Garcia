package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.dto.CommentView;
import com.examen.tvmaze.dto.ShowSearchItem;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private SearchService searchService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void addsStoredCommentsToEachShow() throws Exception {
        when(tvMazeClient.searchShows("girls")).thenReturn(List.of(
                objectMapper.readTree("""
                        {"show":{"id":139,"name":"Girls","genres":["Drama"],"network":{"name":"HBO"},"summary":"s"}}
                        """),
                objectMapper.readTree("""
                        {"show":{"id":2,"name":"Other","genres":[],"webChannel":{"name":"Hulu"},"summary":null}}
                        """),
                objectMapper.readTree("{\"score\":0}")));
        when(commentService.findByShowIds(List.of(139, 2)))
                .thenReturn(Map.of(139, List.of(new CommentView("Buena", 4))));

        List<ShowSearchItem> results = searchService.search("girls");

        assertThat(results).containsExactly(
                new ShowSearchItem(139, "Girls", "HBO", "s", List.of("Drama"), List.of(new CommentView("Buena", 4))),
                new ShowSearchItem(2, "Other", "Hulu", null, List.of(), List.of()));
        verify(commentService).findByShowIds(List.of(139, 2));
    }
}
