package com.examen.tvmaze.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotNull(message = "show_id es obligatorio")
        @Positive(message = "show_id debe ser mayor a 0")
        @JsonProperty("show_id")
        Integer showId,

        @NotBlank(message = "comment es obligatorio")
        @Size(max = 2000, message = "comment no puede superar 2000 caracteres")
        String comment,

        @NotNull(message = "rating es obligatorio")
        @Min(value = 0, message = "rating debe estar entre 0 y 5")
        @Max(value = 5, message = "rating debe estar entre 0 y 5")
        Integer rating) {
}
