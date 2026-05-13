package com.rerelease.movie.rereleasemovie.alert.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 서버 → 클라이언트: 영화 알림 등록 결과 응답 DTO
 * - 알림 등록 결과 메시지 및 등록된 영화 관련 정보 전달
 */
@Getter
@Builder
public class MovieAlertResponse {
    private String message;
    private long movieId;
    private String movieTitle;
    private String posterPath;
    private long userMovieAlertId;
}
