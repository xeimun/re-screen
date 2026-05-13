package com.rerelease.movie.rereleasemovie.service.impl;

import com.rerelease.movie.rereleasemovie.dto.MovieAlertRequest;
import com.rerelease.movie.rereleasemovie.dto.MovieAlertResponse;
import com.rerelease.movie.rereleasemovie.dto.UserAlertManageDto;
import com.rerelease.movie.rereleasemovie.exceptions.AlertNotFoundException;
import com.rerelease.movie.rereleasemovie.exceptions.MovieAlreadyRegisteredException;
import com.rerelease.movie.rereleasemovie.exceptions.UserNotFoundException;
import com.rerelease.movie.rereleasemovie.model.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.model.Users;
import com.rerelease.movie.rereleasemovie.repository.UserMovieAlertRepository;
import com.rerelease.movie.rereleasemovie.repository.UserRepository;
import com.rerelease.movie.rereleasemovie.service.MovieAlertService;
import com.rerelease.movie.rereleasemovie.service.NotificationQueueService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieAlertServiceImpl implements MovieAlertService {

    private final UserMovieAlertRepository userMovieAlertRepository;
    private final UserRepository userRepository;
    private final NotificationQueueService notificationQueueService;

    @Override
    @Transactional
    public MovieAlertResponse registerMovieAlert(String userEmail, MovieAlertRequest request) {

        // 1. 현재 사용자를 이메일로 조회 (사용자가 없는 경우 예외 발생)
        Users currentUser = userRepository.findByEmail(userEmail)
                                          .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 해당 사용자가 이미 같은 영화에 대해 알림 등록을 했는지 확인
        Optional<UserMovieAlert> existingAlert = userMovieAlertRepository.findByUserAndMovieId(currentUser,
                request.getTmdbId());

        // 3. 이미 등록된 영화일 경우 예외 발생
        if (existingAlert.isPresent()) {
            throw new MovieAlreadyRegisteredException("이미 등록된 영화입니다.");
        }

        // 4. 새로운 알림 객체 생성 (UserMovieAlert 엔티티)
        UserMovieAlert newAlert = UserMovieAlert.builder()
                                                .user(currentUser)
                                                .movieId(request.getTmdbId())
                                                .movieTitle(request.getTitle())
                                                .posterPath(request.getPosterPath())
                                                .build();

        // 5. 데이터베이스에 저장
        UserMovieAlert savedAlert = userMovieAlertRepository.save(newAlert);

        // 6. NotificationQueue 에 추가
        notificationQueueService.addAlertToQueue(savedAlert.getId());

        // 7. 성공 메시지를 담은 응답 객체 생성 및 반환
        return MovieAlertResponse.builder()
                                 .message("영화 등록이 완료되었습니다.")
                                 .movieId(request.getTmdbId())
                                 .movieTitle(request.getTitle())
                                 .posterPath(request.getPosterPath())
                                 .userMovieAlertId(savedAlert.getId())
                                 .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAlertManageDto> getUserMovieAlerts(String userEmail) {
        // 1. 사용자 조회
        Users user = userRepository.findByEmail(userEmail)
                                   .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 해당 사용자의 알림 목록 조회
        List<UserMovieAlert> alerts = userMovieAlertRepository.findAllByUserOrderByCreatedAtDesc(user);

        // 3. DTO로 변환
        return alerts.stream()
                     .map(alert -> UserAlertManageDto.builder()
                                                     .userMovieAlertId(alert.getId())
                                                     .movieId(alert.getMovieId())
                                                     .movieTitle(alert.getMovieTitle())
                                                     .posterPath(alert.getPosterPath())
                                                     .registeredAt(alert.getCreatedAt())
                                                     .build())
                     .toList();
    }

    @Override
    @Transactional
    public void deleteUserMovieAlert(String userEmail, Long alertId) {
        // 1. 사용자 조회
        Users user = userRepository.findByEmail(userEmail)
                                   .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 알림 조회 (해당 사용자의 것인지 확인)
        UserMovieAlert alert = userMovieAlertRepository.findById(alertId)
                                                       .orElseThrow(
                                                               () -> new AlertNotFoundException("해당 알림을 찾을 수 없습니다."));

        if (!alert.getUser()
                  .getId()
                  .equals(user.getId())) {
            throw new AccessDeniedException("본인의 알림만 삭제할 수 있습니다.");
        }

        // 3. 삭제 (user_movie_alert 삭제 시 notification_queue도 함께 제거됨 - cascade 설정)
        userMovieAlertRepository.delete(alert);
    }
}
