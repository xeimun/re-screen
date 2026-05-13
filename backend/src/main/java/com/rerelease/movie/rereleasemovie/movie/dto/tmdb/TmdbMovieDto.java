package com.rerelease.movie.rereleasemovie.movie.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class TmdbMovieDto {

    @JsonProperty("id")
    private Long id; // 영화 ID

    @JsonProperty("title")
    private String title; // 영화 제목

    @JsonProperty("original_title")
    private String originalTitle; // 원제

    @JsonProperty("release_date")
    private String releaseDate; // 개봉일

    @JsonProperty("overview")
    private String overview; // 줄거리

    @JsonProperty("poster_path")
    private String posterPath; // 포스터 경로

    @JsonProperty("backdrop_path")
    private String backdropPath; // 배경 이미지 경로

    @JsonProperty("vote_average")
    private Double voteAverage; // 평점

    @JsonProperty("vote_count")
    private Integer voteCount; // 투표 수

    @JsonProperty("popularity")
    private Double popularity; // 인기 지수

    @JsonProperty("genre_ids")
    private List<Integer> genreIds; // 장르 ID 목록

    @JsonProperty("original_language")
    private String originalLanguage; // 원어

    @JsonProperty("adult")
    private boolean adult; // 성인 영화 여부

    @JsonProperty("video")
    private boolean video; // 비디오 여부
}
