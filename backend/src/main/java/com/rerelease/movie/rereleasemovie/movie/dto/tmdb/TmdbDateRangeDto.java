package com.rerelease.movie.rereleasemovie.movie.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TmdbDateRangeDto {

    @JsonProperty("maximum")
    private String maximumDate; // 최대 날짜 (마지막 개봉일)

    @JsonProperty("minimum")
    private String minimumDate; // 최소 날짜 (첫 개봉일)
}
