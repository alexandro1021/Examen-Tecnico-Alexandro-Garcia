package com.examen.tvmaze.controller;

import com.examen.tvmaze.service.ShowService;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@Validated
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/api/show/{show_id}")
    public JsonNode show(
            @PathVariable("show_id") @Positive(message = "show_id debe ser mayor a 0") int showId) {
        return showService.getShow(showId);
    }
}
