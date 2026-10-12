package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.exception.ShowNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

    @InjectMocks
    private ShowService showService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void returnsTheShowFromTvMaze() throws Exception {
        var show = objectMapper.readTree("{\"id\":139,\"name\":\"Girls\"}");
        when(tvMazeClient.findShow(139)).thenReturn(Optional.of(show));

        assertThat(showService.getShow(139)).isEqualTo(show);
    }

    @Test
    void failsWhenTvMazeDoesNotKnowTheShow() {
        when(tvMazeClient.findShow(4)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> showService.getShow(4))
                .isInstanceOf(ShowNotFoundException.class)
                .hasMessage("No se encontró el show 4");
    }
}
