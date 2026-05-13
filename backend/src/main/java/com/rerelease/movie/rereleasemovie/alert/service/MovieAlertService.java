package com.rerelease.movie.rereleasemovie.alert.service;

import com.rerelease.movie.rereleasemovie.alert.dto.MovieAlertRequest;
import com.rerelease.movie.rereleasemovie.alert.dto.MovieAlertResponse;
import com.rerelease.movie.rereleasemovie.alert.dto.UserAlertManageDto;
import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.alert.repository.UserMovieAlertRepository;
import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import com.rerelease.movie.rereleasemovie.auth.repository.UserRepository;
import com.rerelease.movie.rereleasemovie.common.exception.AlertNotFoundException;
import com.rerelease.movie.rereleasemovie.common.exception.MovieAlreadyRegisteredException;
import com.rerelease.movie.rereleasemovie.common.exception.UserNotFoundException;
import com.rerelease.movie.rereleasemovie.notification.service.NotificationQueueService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieAlertService {

    private final UserMovieAlertRepository userMovieAlertRepository;
    private final UserRepository userRepository;
    private final NotificationQueueService notificationQueueService;

    @Transactional
    public MovieAlertResponse registerMovieAlert(String userEmail, MovieAlertRequest request) {
        Users currentUser = userRepository.findByEmail(userEmail)
                                          .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Optional<UserMovieAlert> existingAlert = userMovieAlertRepository.findByUserAndMovieId(currentUser,
                request.getTmdbId());

        if (existingAlert.isPresent()) {
            throw new MovieAlreadyRegisteredException("이미 등록된 영화입니다.");
        }

        UserMovieAlert newAlert = UserMovieAlert.builder()
                                                .user(currentUser)
                                                .movieId(request.getTmdbId())
                                                .movieTitle(request.getTitle())
                                                .posterPath(request.getPosterPath())
                                                .build();

        UserMovieAlert savedAlert = userMovieAlertRepository.save(newAlert);
        notificationQueueService.addAlertToQueue(savedAlert.getId());

        return MovieAlertResponse.builder()
                                 .message("영화 등록이 완료되었습니다.")
                                 .movieId(request.getTmdbId())
                                 .movieTitle(request.getTitle())
                                 .posterPath(request.getPosterPath())
                                 .userMovieAlertId(savedAlert.getId())
                                 .build();
    }

    @Transactional(readOnly = true)
    public List<UserAlertManageDto> getUserMovieAlerts(String userEmail) {
        Users user = userRepository.findByEmail(userEmail)
                                   .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return userMovieAlertRepository.findAllByUserOrderByCreatedAtDesc(user)
                                       .stream()
                                       .map(alert -> UserAlertManageDto.builder()
                                                                       .userMovieAlertId(alert.getId())
                                                                       .movieId(alert.getMovieId())
                                                                       .movieTitle(alert.getMovieTitle())
                                                                       .posterPath(alert.getPosterPath())
                                                                       .registeredAt(alert.getCreatedAt())
                                                                       .build())
                                       .toList();
    }

    @Transactional
    public void deleteUserMovieAlert(String userEmail, Long alertId) {
        Users user = userRepository.findByEmail(userEmail)
                                   .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        UserMovieAlert alert = userMovieAlertRepository.findById(alertId)
                                                       .orElseThrow(
                                                               () -> new AlertNotFoundException("해당 알림을 찾을 수 없습니다."));

        if (!alert.getUser()
                  .getId()
                  .equals(user.getId())) {
            throw new AccessDeniedException("본인의 알림만 삭제할 수 있습니다.");
        }

        notificationQueueService.deleteByAlert(alert);
        userMovieAlertRepository.delete(alert);
    }
}
