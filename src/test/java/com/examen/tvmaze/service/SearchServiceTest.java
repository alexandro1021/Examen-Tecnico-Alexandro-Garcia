package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.dto.ShowSearchItem;
import java.util.List;
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

    @InjectMocks
    private SearchService searchService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void returnsMappedShowsAndDropsIncompleteHits() throws Exception {
        when(tvMazeClient.searchShows("girls")).thenReturn(List.of(
                objectMapper.readTree("""
                        {"show":{"id":139,"name":"Girls","genres":["Drama"],"network":{"name":"HBO"},"summary":"s"}}
                        """),
                objectMapper.readTree("{\"score\":0}")));

        List<ShowSearchItem> results = searchService.search("girls");

        assertThat(results).containsExactly(new ShowSearchItem(139, "Girls", "HBO", "s", List.of("Drama")));
    }
}
