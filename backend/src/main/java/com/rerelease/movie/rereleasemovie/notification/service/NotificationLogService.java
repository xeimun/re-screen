package com.rerelease.movie.rereleasemovie.notification.service;

import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.notification.entity.NotificationLog;
import com.rerelease.movie.rereleasemovie.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private static final String EMAIL_NOTIFICATION_TYPE = "EMAIL";
    private static final int SUCCESS_STATUS = 1;
    private static final int FAILURE_STATUS = 2;

    private final NotificationLogRepository notificationLogRepository;

    public void saveSuccess(UserMovieAlert alert) {
        save(alert, SUCCESS_STATUS, null);
    }

    public void saveFailure(UserMovieAlert alert, Exception e) {
        save(alert, FAILURE_STATUS, e.getClass()
                                    .getSimpleName());
    }

    private void save(UserMovieAlert alert, int status, String errorMessage) {
        notificationLogRepository.save(NotificationLog.builder()
                                                      .user(alert.getUser())
                                                      .movieId(alert.getMovieId())
                                                      .movieTitle(alert.getMovieTitle())
                                                      .posterPath(alert.getPosterPath())
                                                      .notificationType(EMAIL_NOTIFICATION_TYPE)
                                                      .status(status)
                                                      .errorMessage(errorMessage)
                                                      .registeredAt(alert.getCreatedAt())
                                                      .build());
    }
}
