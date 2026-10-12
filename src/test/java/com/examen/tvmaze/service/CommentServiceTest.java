package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examen.tvmaze.dto.CreateCommentRequest;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.persistence.ShowComment;
import com.examen.tvmaze.persistence.ShowCommentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.node.JsonNodeFactory;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private ShowCommentRepository showCommentRepository;

    @Mock
    private ShowCatalog showCatalog;

    private final Clock clock = Clock.fixed(Instant.parse("2026-10-12T00:00:00Z"), ZoneOffset.UTC);
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(showCommentRepository, showCatalog, clock);
    }

    @Test
    void storesACommentWhenTheShowExists() {
        when(showCatalog.load(139)).thenReturn(JsonNodeFactory.instance.objectNode().put("id", 139));

        var status = commentService.create(new CreateCommentRequest(139, "  Muy buena  ", 5));

        assertThat(status.status()).isEqualTo("CREATED");
        ArgumentCaptor<ShowComment> captor = ArgumentCaptor.forClass(ShowComment.class);
        verify(showCommentRepository).save(captor.capture());
        assertThat(captor.getValue().getShowId()).isEqualTo(139);
        assertThat(captor.getValue().getComment()).isEqualTo("Muy buena");
        assertThat(captor.getValue().getRating()).isEqualTo(5);
        assertThat(captor.getValue().getCreatedAt()).isEqualTo(clock.instant());
    }

    @Test
    void doesNotStoreACommentForAnUnknownShow() {
        when(showCatalog.load(4)).thenThrow(new ShowNotFoundException(4));

        assertThatThrownBy(() -> commentService.create(new CreateCommentRequest(4, "No existe", 1)))
                .isInstanceOf(ShowNotFoundException.class);
        verify(showCommentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
