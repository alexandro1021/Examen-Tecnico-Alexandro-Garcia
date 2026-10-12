package com.examen.tvmaze.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.examen.tvmaze.dto.RequestStatusResponse;
import com.examen.tvmaze.exception.GlobalExceptionHandler;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@Import(GlobalExceptionHandler.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void createsAComment() throws Exception {
        when(commentService.create(new com.examen.tvmaze.dto.CreateCommentRequest(139, "Muy buena", 5)))
                .thenReturn(new RequestStatusResponse("CREATED"));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"show_id\":139,\"comment\":\"Muy buena\",\"rating\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void rejectsARatingOutsideTheScale() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"show_id\":139,\"comment\":\"Demasiado\",\"rating\":6}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("rating debe estar entre 0 y 5"));
    }

    @Test
    void rejectsABlankComment() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"show_id\":139,\"comment\":\"   \",\"rating\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("comment es obligatorio"));
    }

    @Test
    void returnsNotFoundWhenTheShowDoesNotExist() throws Exception {
        when(commentService.create(new com.examen.tvmaze.dto.CreateCommentRequest(4, "No está", 1)))
                .thenThrow(new ShowNotFoundException(4));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"show_id\":4,\"comment\":\"No está\",\"rating\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }
}
