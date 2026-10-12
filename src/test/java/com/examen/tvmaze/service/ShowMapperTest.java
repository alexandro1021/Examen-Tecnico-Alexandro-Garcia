package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.examen.tvmaze.dto.ShowSearchItem;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ShowMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mapsNetworkNameAsChannel() throws Exception {
        String json = """
                {"score":1,"show":{
                  "id":139,
                  "name":"Girls",
                  "genres":["Drama","Romance"],
                  "summary":"<p>A comic look.</p>",
                  "network":{"name":"HBO"},
                  "webChannel":null
                }}
                """;

        ShowSearchItem item = ShowMapper.toSearchItem(objectMapper.readTree(json)).orElseThrow();

        assertThat(item.id()).isEqualTo(139);
        assertThat(item.name()).isEqualTo("Girls");
        assertThat(item.channel()).isEqualTo("HBO");
        assertThat(item.summary()).isEqualTo("<p>A comic look.</p>");
        assertThat(item.genres()).containsExactly("Drama", "Romance");
    }

    @Test
    void usesWebChannelWhenNetworkIsMissing() throws Exception {
        String json = """
                {"show":{
                  "id":48830,
                  "name":"Only Murders in the Building",
                  "genres":["Comedy"],
                  "summary":null,
                  "network":null,
                  "webChannel":{"name":"Hulu"}
                }}
                """;

        ShowSearchItem item = ShowMapper.toSearchItem(objectMapper.readTree(json)).orElseThrow();

        assertThat(item.channel()).isEqualTo("Hulu");
        assertThat(item.summary()).isNull();
    }

    @Test
    void skipsHitsWithoutAShowId() throws Exception {
        assertThat(ShowMapper.toSearchItem(objectMapper.readTree("{\"score\":0.1}"))).isEmpty();
    }
}
