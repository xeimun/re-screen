package com.rerelease.movie.rereleasemovie.movie.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class TmdbMovieListResponseDto {

    @JsonProperty("dates")
    private TmdbDateRangeDto dates; // 개봉 예정 영화의 날짜 범위

    @JsonProperty("page")
    private Integer page; // 현재 페이지

    @JsonProperty("total_pages")
    private Integer totalPages; // 전체 페이지 수

    @JsonProperty("total_results")
    private Integer totalResults; // 전체 결과 수

    @JsonProperty("results")
    private List<TmdbMovieDto> results; // 영화 목록
}
