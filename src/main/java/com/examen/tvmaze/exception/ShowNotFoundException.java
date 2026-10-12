package com.examen.tvmaze.exception;

public class ShowNotFoundException extends RuntimeException {

    public ShowNotFoundException(int showId) {
        super("No se encontró el show " + showId);
    }
}
