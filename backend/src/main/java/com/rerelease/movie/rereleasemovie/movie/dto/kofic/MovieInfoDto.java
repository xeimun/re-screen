package com.rerelease.movie.rereleasemovie.movie.dto.kofic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

/**
 * 개별 영화 정보를 담는 DTO
 * - 영화 코드, 제목, 개봉일, 장르, 감독 목록 포함
 */
@Getter
public class MovieInfoDto {

    @JsonProperty("movieCd")
    private String movieCode;  // 영화 코드

    @JsonProperty("movieNm")
    private String title;  // 영화 제목(국문)

    @JsonProperty("movieNmEn")
    private String titleEn;  // 영화 제목(영문)

    @JsonProperty("openDt")
    private String openDate;  // 개봉일

    @JsonProperty("repGenreNm")
    private String representativeGenreName;  // 대표 장르명

    @JsonProperty("directors")
    private List<DirectorDto> directors;  // 감독 목록
}
