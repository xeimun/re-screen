package com.rerelease.movie.rereleasemovie.alert.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAlertManageDto {
    private Long userMovieAlertId;
    private Long movieId;
    private String movieTitle;
    private String posterPath;
    private LocalDateTime registeredAt;
}
