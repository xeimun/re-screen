package com.rerelease.movie.rereleasemovie.movie.dto.tmdb.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rerelease.movie.rereleasemovie.movie.dto.tmdb.TmdbMovieDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TmdbMovieSearchResponseDto {

    private int page; // 검색 결과 페이지
    private List<TmdbMovieDto> results; // 검색된 영화 목록

    @JsonProperty("total_pages")
    private int totalPages; // 전체 페이지 수

    @JsonProperty("total_results")
    private int totalResults; // 전체 검색 결과 개수
}
