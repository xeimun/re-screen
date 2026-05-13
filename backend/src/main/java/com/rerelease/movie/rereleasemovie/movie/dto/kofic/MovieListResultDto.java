package com.rerelease.movie.rereleasemovie.movie.dto.kofic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

/**
 * 영화 목록을 포함하는 DTO (movieList를 가지고 있음)
 * - 최상위 응답 객체 (MovieListResponseDto) 내부에서 사용됨
 */
@Getter
public class MovieListResultDto {
    @JsonProperty("movieList")
    private List<MovieInfoDto> movieList;
}
