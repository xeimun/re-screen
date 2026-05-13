package com.rerelease.movie.rereleasemovie.common.exception;

public class MovieAlreadyRegisteredException extends RuntimeException {
    public MovieAlreadyRegisteredException(String message) {
        super(message);
    }
}
