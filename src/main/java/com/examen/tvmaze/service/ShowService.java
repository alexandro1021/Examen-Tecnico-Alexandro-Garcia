package com.examen.tvmaze.service;

import com.examen.tvmaze.client.TvMazeClient;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.persistence.CachedShow;
import com.examen.tvmaze.persistence.ShowCacheRepository;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class ShowService {

    private static final Logger log = LoggerFactory.getLogger(ShowService.class);

    private final TvMazeClient tvMazeClient;
    private final ShowCacheRepository showCacheRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public ShowService(
            TvMazeClient tvMazeClient,
            ShowCacheRepository showCacheRepository,
            ObjectMapper objectMapper,
            Clock clock) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheRepository = showCacheRepository;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public JsonNode getShow(int showId) {
        return showCacheRepository.findById(showId)
                .map(this::readCached)
                .orElseGet(() -> fetchAndStore(showId));
    }

    private JsonNode readCached(CachedShow cached) {
        try {
            JsonNode show = objectMapper.readTree(cached.getPayload());
            if (show != null && show.isObject()) {
                log.debug("Show {} servido desde MongoDB", cached.getId());
                return show;
            }
        } catch (JacksonException exception) {
            log.warn("La caché del show {} está dañada y se volverá a consultar", cached.getId());
        }
        showCacheRepository.deleteById(cached.getId());
        return fetchAndStore(cached.getId());
    }

    private JsonNode fetchAndStore(int showId) {
        JsonNode show = tvMazeClient.findShow(showId).orElseThrow(() -> new ShowNotFoundException(showId));
        showCacheRepository.save(new CachedShow(showId, objectMapper.writeValueAsString(show), clock.instant()));
        log.debug("Show {} guardado en MongoDB", showId);
        return show;
    }
}
