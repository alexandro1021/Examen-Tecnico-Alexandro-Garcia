package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.persistence.CachedShow;
import com.examen.tvmaze.persistence.ShowCacheRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private ShowCacheRepository showCacheRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-12T00:00:00Z"), ZoneOffset.UTC);
    private ShowService showService;

    @BeforeEach
    void setUp() {
        showService = new ShowService(tvMazeClient, showCacheRepository, objectMapper, clock);
    }

    @Test
    void returnsTheCachedShowWithoutCallingTvMaze() throws Exception {
        when(showCacheRepository.findById(139))
                .thenReturn(Optional.of(new CachedShow(139, "{\"id\":139,\"name\":\"Girls\"}", clock.instant())));

        assertThat(showService.getShow(139).path("name").asString()).isEqualTo("Girls");
        verify(tvMazeClient, never()).findShow(139);
    }

    @Test
    void storesTheShowWhenItIsNotCached() throws Exception {
        var show = objectMapper.readTree("{\"id\":139,\"name\":\"Girls\",\"runtime\":30}");
        when(showCacheRepository.findById(139)).thenReturn(Optional.empty());
        when(tvMazeClient.findShow(139)).thenReturn(Optional.of(show));

        assertThat(showService.getShow(139)).isEqualTo(show);

        ArgumentCaptor<CachedShow> captor = ArgumentCaptor.forClass(CachedShow.class);
        verify(showCacheRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(139);
        assertThat(captor.getValue().getCachedAt()).isEqualTo(clock.instant());
        assertThat(objectMapper.readTree(captor.getValue().getPayload()).path("runtime").asInt()).isEqualTo(30);
    }

    @Test
    void doesNotCacheAMissingShow() {
        when(showCacheRepository.findById(4)).thenReturn(Optional.empty());
        when(tvMazeClient.findShow(4)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> showService.getShow(4)).isInstanceOf(ShowNotFoundException.class);
        verify(showCacheRepository, never()).save(any());
    }
}
