package com.examen.tvmaze.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.examen.tvmaze.dto.ShowSearchItem;
import com.examen.tvmaze.exception.GlobalExceptionHandler;
import com.examen.tvmaze.service.SearchService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SearchController.class)
@Import(GlobalExceptionHandler.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    void returnsTheShowProjection() throws Exception {
        when(searchService.search("girls")).thenReturn(List.of(
                new ShowSearchItem(139, "Girls", "HBO", "<p>Comic.</p>", List.of("Drama", "Romance"))));

        mockMvc.perform(get("/api/search").param("search_query", "girls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(139))
                .andExpect(jsonPath("$[0].name").value("Girls"))
                .andExpect(jsonPath("$[0].channel").value("HBO"))
                .andExpect(jsonPath("$[0].summary").value("<p>Comic.</p>"))
                .andExpect(jsonPath("$[0].genres[0]").value("Drama"))
                .andExpect(jsonPath("$[0].comments").doesNotExist());
    }

    @Test
    void rejectsABlankQuery() throws Exception {
        mockMvc.perform(get("/api/search").param("search_query", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("search_query es obligatorio"));
    }

    @Test
    void rejectsAMissingQuery() throws Exception {
        mockMvc.perform(get("/api/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El parámetro search_query es obligatorio"));
    }
}
