package com.rerelease.movie.rereleasemovie.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private String token;
    private String message;
}
