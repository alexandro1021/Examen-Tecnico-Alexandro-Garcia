package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.examen.tvmaze.dto.CommentView;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private ShowCatalog showCatalog;

    @Mock
    private CommentService commentService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ShowService showService;

    @BeforeEach
    void setUp() {
        showService = new ShowService(showCatalog, commentService);
    }

    @Test
    void addsCommentsWithoutRemovingShowFields() throws Exception {
        ObjectNode show = (ObjectNode) objectMapper.readTree("{\"id\":139,\"name\":\"Girls\",\"runtime\":30}");
        when(showCatalog.load(139)).thenReturn(show);
        when(commentService.findByShowId(139)).thenReturn(List.of(new CommentView("Muy buena", 5)));

        var response = showService.getShow(139);

        assertThat(response.path("name").asString()).isEqualTo("Girls");
        assertThat(response.path("runtime").asInt()).isEqualTo(30);
        assertThat(response.path("comments").size()).isEqualTo(1);
        assertThat(response.path("comments").get(0).path("comment").asString()).isEqualTo("Muy buena");
        assertThat(response.path("comments").get(0).path("rating").asInt()).isEqualTo(5);
        assertThat(show.has("comments")).isFalse();
    }

    @Test
    void returnsAnEmptyCommentListWhenTheShowHasNone() throws Exception {
        when(showCatalog.load(139)).thenReturn(objectMapper.readTree("{\"id\":139,\"name\":\"Girls\"}"));
        when(commentService.findByShowId(139)).thenReturn(List.of());

        assertThat(showService.getShow(139).path("comments").size()).isZero();
    }
}
