package com.rerelease.movie.rereleasemovie.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {
    private String message;
}
