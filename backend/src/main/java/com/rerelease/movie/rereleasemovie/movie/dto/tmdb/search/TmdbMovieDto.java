package com.rerelease.movie.rereleasemovie.movie.dto.tmdb.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TmdbMovieDto {

    private Long id; // TMDB 영화 ID
    private String title; // 영화 제목
    private String overview; // 영화 설명

    @JsonProperty("release_date")
    private String releaseDate; // 개봉일

    @JsonProperty("poster_path")
    private String posterPath; // 포스터 이미지

    @JsonProperty("backdrop_path")
    private String backdropPath; // 배경 이미지

    @JsonProperty("vote_average")
    private double voteAverage; // 평점

    @JsonProperty("vote_count")
    private int voteCount; // 투표 수

    @JsonProperty("genre_ids")
    private List<Integer> genreIds; // 장르 ID 목록
}
