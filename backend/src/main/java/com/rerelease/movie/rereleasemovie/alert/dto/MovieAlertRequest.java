package com.rerelease.movie.rereleasemovie.alert.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 클라이언트 → 서버: 영화 알림 등록 요청 DTO
 * - 사용자가 영화 알림을 등록할 때 필요한 데이터
 */
@Getter
@NoArgsConstructor
public class MovieAlertRequest {
    private long tmdbId;
    private String title;
    private String posterPath;
}
