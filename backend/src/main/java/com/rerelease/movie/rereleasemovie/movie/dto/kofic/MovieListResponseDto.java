package com.rerelease.movie.rereleasemovie.movie.dto.kofic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * KOFIC API 응답의 최상위 객체 (movieListResult를 포함)
 * - 실제 영화 목록은 movieListResult 내부의 movieList에 존재
 */
@Getter
public class MovieListResponseDto {
    @JsonProperty("movieListResult")
    private MovieListResultDto movieListResult;

    public List<MovieInfoDto> getMovieList() {
        if (movieListResult == null || movieListResult.getMovieList() == null) {
            return Collections.emptyList();
        }
        return movieListResult.getMovieList();
    }
}
