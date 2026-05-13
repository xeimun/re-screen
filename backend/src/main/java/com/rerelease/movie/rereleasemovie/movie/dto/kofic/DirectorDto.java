package com.rerelease.movie.rereleasemovie.movie.dto.kofic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 감독 정보를 담는 DTO
 * - 영화별로 여러 명의 감독이 있을 수 있음 (List 형태로 MovieInfoDto에서 사용됨)
 */
@Getter
public class DirectorDto {
    @JsonProperty("peopleNm")
    private String name; // 감독 이름
}
