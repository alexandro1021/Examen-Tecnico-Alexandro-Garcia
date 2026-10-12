package com.examen.tvmaze.controller;

import com.examen.tvmaze.dto.ShowSearchItem;
import com.examen.tvmaze.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public List<ShowSearchItem> search(
            @RequestParam("search_query")
            @NotBlank(message = "search_query es obligatorio")
            @Size(max = 200, message = "search_query no puede superar 200 caracteres")
            String searchQuery) {
        return searchService.search(searchQuery.trim());
    }
}
