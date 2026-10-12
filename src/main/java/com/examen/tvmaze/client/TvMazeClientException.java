package com.examen.tvmaze.client;

public class TvMazeClientException extends RuntimeException {

    private final int upstreamStatus;

    public TvMazeClientException(String message) {
        this(0, message, null);
    }

    public TvMazeClientException(String message, Throwable cause) {
        this(0, message, cause);
    }

    public TvMazeClientException(int upstreamStatus, String message) {
        this(upstreamStatus, message, null);
    }

    public TvMazeClientException(int upstreamStatus, String message, Throwable cause) {
        super(message, cause);
        this.upstreamStatus = upstreamStatus;
    }

    public int upstreamStatus() {
        return upstreamStatus;
    }
}
