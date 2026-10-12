package com.examen.tvmaze.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.examen.tvmaze.exception.GlobalExceptionHandler;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.service.ShowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ShowController.class)
@Import(GlobalExceptionHandler.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowService showService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void returnsTheCompleteShow() throws Exception {
        when(showService.getShow(139)).thenReturn(objectMapper.readTree("""
                {"id":139,"name":"Girls","runtime":30,"network":{"name":"HBO"}}
                """));

        mockMvc.perform(get("/api/show/{show_id}", 139))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(139))
                .andExpect(jsonPath("$.name").value("Girls"))
                .andExpect(jsonPath("$.runtime").value(30))
                .andExpect(jsonPath("$.network.name").value("HBO"));
    }

    @Test
    void returnsNotFoundWhenTheShowDoesNotExist() throws Exception {
        when(showService.getShow(4)).thenThrow(new ShowNotFoundException(4));

        mockMvc.perform(get("/api/show/{show_id}", 4))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("No se encontró el show 4"));
    }

    @Test
    void rejectsANonNumericId() throws Exception {
        mockMvc.perform(get("/api/show/{show_id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El parámetro show_id tiene un formato inválido"));
    }
}
